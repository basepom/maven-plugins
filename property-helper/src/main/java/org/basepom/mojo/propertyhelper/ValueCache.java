/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.basepom.mojo.propertyhelper;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.basepom.mojo.propertyhelper.IgnoreWarnFailCreate.checkIgnoreWarnFailCreateState;

import org.basepom.mojo.propertyhelper.ValueProvider.MapBackedValueAdapter;
import org.basepom.mojo.propertyhelper.definitions.FieldDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import com.google.common.flogger.FluentLogger;

public final class ValueCache {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    private final Map<String, String> ephemeralValues = Maps.newHashMap();

    /**
     * Cache for values files loaded from disk
     */
    private final Map<File, ValueCacheEntry> valueFiles = Maps.newHashMap();

    @VisibleForTesting
    public ValueProvider findCurrentValueProvider(final Map<String, String> values, final FieldDefinition<?> definition) {
        checkNotNull(values, "values is null");

        final String propertyNameInFile = definition.getPropertyNameInFile();
        final boolean hasValue = values.containsKey(propertyNameInFile);

        final boolean createProperty = checkIgnoreWarnFailCreateState(hasValue, definition.getOnMissingFileProperty(),
            () -> format("property '%s' has value '%s'", propertyNameInFile, values.get(propertyNameInFile)),
            () -> format("property '%s' has no value defined", propertyNameInFile));

        if (hasValue) {
            return new MapBackedValueAdapter(values, propertyNameInFile);
        } else if (createProperty) {
            Optional<String> initialValue = definition.getInitialValue();
            initialValue.ifPresent(value -> values.put(propertyNameInFile, value));

            return new MapBackedValueAdapter(values, propertyNameInFile);
        } else {
            return ValueProvider.NULL_PROVIDER;
        }
    }

    public ValueProvider getValueProvider(final FieldDefinition<?> definition) throws IOException {
        final Optional<Map<String, String>> values = getValues(definition);
        if (values.isEmpty()) {
            final String name = definition.getId();
            final Optional<String> initialValue = definition.getInitialValue();
            initialValue.ifPresent(s -> ephemeralValues.put(name, s));

            return new MapBackedValueAdapter(ephemeralValues, name);
        } else {
            return findCurrentValueProvider(values.get(), definition);
        }
    }

    @VisibleForTesting
    Optional<Map<String, String>> getValues(final FieldDefinition<?> definition) throws IOException {
        final Optional<File> definitionFile = definition.getPropertyFile();

        // Ephemeral, so return null.
        if (definitionFile.isEmpty()) {
            return Optional.empty();
        }

        ValueCacheEntry cacheEntry;
        final File canonicalFile = definitionFile.get().getCanonicalFile();
        final String canonicalPath = definitionFile.get().getCanonicalPath();

        // Throws an exception if the file must exist and does not.
        final boolean createFile = checkIgnoreWarnFailCreateState(canonicalFile.exists(), definition.getOnMissingFile(),
            () -> format("property file '%s' exists", canonicalPath),
            () -> format("property file '%s' does not exist!", canonicalPath));

        cacheEntry = valueFiles.get(canonicalFile);

        if (cacheEntry != null) {
            // If there is a cache hit, something either has loaded the file
            // or another property has already put in a creation order.
            // Make sure that if this number has a creation order it is obeyed.
            if (createFile) {
                cacheEntry.doCreate();
            }
        } else {
            // Try loading or creating properties.
            final Properties props = new Properties();

            if (!canonicalFile.exists()) {
                cacheEntry = new ValueCacheEntry(props, false, createFile); // does not exist
                valueFiles.put(canonicalFile, cacheEntry);
            } else {
                if (canonicalFile.isFile() && canonicalFile.canRead()) {
                    try (InputStream stream = Files.newInputStream(canonicalFile.toPath())) {
                        props.load(stream);
                        cacheEntry = new ValueCacheEntry(props, true, createFile);
                        valueFiles.put(canonicalFile, cacheEntry);
                    }
                } else {
                    throw new IllegalStateException(
                        format("Can not load %s, not a file!", definitionFile.get().getCanonicalPath()));
                }
            }
        }

        return Optional.of(cacheEntry.getValues());
    }

    public void persist()
        throws IOException {
        for (final Entry<File, ValueCacheEntry> entries : valueFiles.entrySet()) {
            final ValueCacheEntry entry = entries.getValue();
            if (!entry.isDirty()) {
                continue;
            }
            final File file = entries.getKey();
            if (entry.isExists() || entry.isCreate()) {
                checkNotNull(file, "no file defined, can not persist!");
                final File oldFile = new File(file.getCanonicalPath() + ".bak");

                if (entry.isExists()) {
                    checkState(file.exists(), "'%s' should exist!", file.getCanonicalPath());
                    // unlink an old file if necessary
                    if (oldFile.exists()) {
                        checkState(oldFile.delete(), "Could not delete '%s'", file.getCanonicalPath());
                    }
                }

                final File folder = file.getParentFile();
                if (!folder.exists()) {
                    checkState(folder.mkdirs(), "Could not create folder '%s'", folder.getCanonicalPath());
                }

                final File newFile = new File(file.getCanonicalPath() + ".new");
                try (OutputStream stream = Files.newOutputStream(newFile.toPath())) {
                    entry.store(stream, "created by property-helper-maven-plugin");
                }

                if (file.exists()) {
                    if (!file.renameTo(oldFile)) {
                        LOG.atWarning().log("Could not rename '%s' to '%s'!", file, oldFile);
                    }
                }

                if (!file.exists()) {
                    if (!newFile.renameTo(file)) {
                        LOG.atWarning().log("Could not rename '%s' to '%s'!", newFile, file);
                    }
                }
            }
        }
    }

    public static class ValueCacheEntry {

        private final Map<String, String> values = Maps.newHashMap();

        private final boolean exists;

        private boolean create;

        private boolean dirty = false;

        ValueCacheEntry(final Properties props,
            final boolean exists,
            final boolean create) {
            checkNotNull(props, "props is null");

            values.putAll(Maps.fromProperties(props));

            this.exists = exists;
            this.create = create;
        }

        public void store(final OutputStream out, final String comment)
            throws IOException {
            final Properties p = new Properties();
            for (Entry<String, String> entry : values.entrySet()) {
                p.setProperty(entry.getKey(), entry.getValue());
            }
            p.store(out, comment);
        }

        public boolean isDirty() {
            return dirty;
        }

        public void dirty() {
            this.dirty = true;
        }

        public Map<String, String> getValues() {
            return new ForwardingMap<>() {
                @Override
                protected Map<String, String> delegate() {
                    return values;
                }

                @Override
                public String remove(Object object) {
                    dirty();
                    return super.remove(object);
                }

                @Override
                public void clear() {
                    dirty();
                    super.clear();
                }

                @Override
                public String put(String key, String value) {
                    final String oldValue = super.put(key, value);
                    if (!Objects.equals(value, oldValue)) {
                        dirty();
                    }
                    return oldValue;
                }

                @Override
                public void putAll(Map<? extends String, ? extends String> map) {
                    dirty();
                    super.putAll(map);
                }
            };
        }

        public boolean isExists() {
            return exists;
        }

        public boolean isCreate() {
            return create;
        }

        public void doCreate() {
            this.create = true;
            dirty();
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", ValueCacheEntry.class.getSimpleName() + "[", "]")
                .add("values=" + values)
                .add("exists=" + exists)
                .add("create=" + create)
                .add("dirty=" + dirty)
                .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ValueCacheEntry that = (ValueCacheEntry) o;
            return exists == that.exists && create == that.create && dirty == that.dirty
                && Objects.equals(values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(values, exists, create, dirty);
        }
    }
}
