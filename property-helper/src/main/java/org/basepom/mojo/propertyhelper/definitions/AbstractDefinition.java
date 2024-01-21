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

package org.basepom.mojo.propertyhelper.definitions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import org.basepom.mojo.propertyhelper.IgnoreWarnFailCreate;
import org.basepom.mojo.propertyhelper.PropertyElement;
import org.basepom.mojo.propertyhelper.PropertyElementContext;
import org.basepom.mojo.propertyhelper.TransformerRegistry;
import org.basepom.mojo.propertyhelper.ValueCache;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;

public abstract class AbstractDefinition<T extends AbstractDefinition<T>> {

    /**
     * Name of the build property to define. Field injected by Maven.
     */
    String id = null;

    /**
     * True skips the parsing of this definition. Field injected by Maven.
     */
    boolean skip = false;

    /**
     * Whether to export this number directly. Field injected by Maven.
     */
    boolean export = false;

    /**
     * Name of the property from the properties file. Field injected by Maven.
     */
    String propertyName = null;

    /**
     * Name of the properties file to persist the count. Field injected by Maven.
     */
    File propertyFile = null;

    /**
     * What to do when the property is missing from the file. Field injected by Maven.
     */
    String onMissingFile = "fail";

    /**
     * What to do when the property is missing from the file. Field injected by Maven.
     */
    String onMissingProperty = "fail";

    /**
     * The initial value for this field. Field injected by Maven.
     */
    String initialValue = null;

    String initialProperty = null;

    /**
     * Format for this element. Field injected by Maven.
     */
    String format = null;

    /**
     * Comma separated list of String transformers.
     */
    String transformers = null;

    protected AbstractDefinition() {
    }

    public abstract PropertyElement createPropertyElement(PropertyElementContext context, ValueCache valueCache) throws IOException;

    public String getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public T setId(final String id) {
        this.id = checkNotNull(id, "id is null").trim();
        return (T) this;
    }

    public boolean isSkip() {
        return skip;
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public T setSkip(final boolean skip) {
        this.skip = skip;
        return (T) this;
    }

    public Optional<String> getTransformers() {
        return Optional.ofNullable(transformers);
    }

    public Optional<String> getInitialValue() {
        return Optional.ofNullable(initialValue);
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public T setInitialValue(final String initialValue) {
        checkState(initialValue != null, "initialValue is null");
        this.initialValue = initialValue.trim();
        return (T) this;
    }

    public Optional<String> getInitialProperty() {
        return Optional.ofNullable(initialProperty);
    }

    public boolean isExport() {
        return export;
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public T setExport(final boolean export) {
        this.export = export;
        return (T) this;
    }

    public String getPropertyName() {
        return Objects.requireNonNullElse(propertyName, id);
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public T setPropertyName(final String propertyName) {
        this.propertyName = checkNotNull(propertyName, "propertyName is null").trim();
        return (T) this;
    }

    public Optional<File> getPropertyFile() {
        return Optional.ofNullable(propertyFile);
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public T setPropertyFile(final File propertyFile) {
        this.propertyFile = checkNotNull(propertyFile, "propertyFile is null");
        return (T) this;
    }

    public IgnoreWarnFailCreate getOnMissingFile() {
        return IgnoreWarnFailCreate.forString(onMissingFile);
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public T setOnMissingFile(final String onMissingFile) {
        IgnoreWarnFailCreate.forString(onMissingFile);
        this.onMissingFile = onMissingFile;
        return (T) this;
    }

    public IgnoreWarnFailCreate getOnMissingProperty() {
        return IgnoreWarnFailCreate.forString(onMissingProperty);
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public T setOnMissingProperty(final String onMissingProperty) {
        IgnoreWarnFailCreate.forString(onMissingProperty);
        this.onMissingProperty = onMissingProperty;
        return (T) this;
    }

    public Optional<String> formatResult(final String value) {
        final Optional<String> format = getFormat();
        String res = format.isPresent() ? format(format.get(), value) : value;

        res = TransformerRegistry.applyTransformers(transformers, res);

        return Optional.ofNullable(res);
    }

    public Optional<String> getFormat() {
        return Optional.ofNullable(format);
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public T setFormat(final String format) {
        this.format = checkNotNull(format, "format is null");
        return (T) this;
    }

    public void check() {
        checkState(id != null, "the id element must not be empty!");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AbstractDefinition.class.getSimpleName() + "[", "]")
            .add("id='" + id + "'")
            .add("skip=" + skip)
            .add("export=" + export)
            .add("propertyName='" + propertyName + "'")
            .add("propertyFile=" + propertyFile)
            .add("onMissingFile='" + onMissingFile + "'")
            .add("onMissingProperty='" + onMissingProperty + "'")
            .add("initialValue='" + initialValue + "'")
            .add("format='" + format + "'")
            .add("transformers='" + transformers + "'")
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
        AbstractDefinition<?> that = (AbstractDefinition<?>) o;
        return skip == that.skip && export == that.export && Objects.equals(id, that.id) && Objects.equals(propertyName, that.propertyName)
            && Objects.equals(propertyFile, that.propertyFile) && Objects.equals(onMissingFile, that.onMissingFile)
            && Objects.equals(onMissingProperty, that.onMissingProperty) && Objects.equals(initialValue, that.initialValue)
            && Objects.equals(format, that.format) && Objects.equals(transformers, that.transformers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, skip, export, propertyName, propertyFile, onMissingFile, onMissingProperty, initialValue, format, transformers);
    }
}
