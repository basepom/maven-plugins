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

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import org.basepom.mojo.propertyhelper.Field;
import org.basepom.mojo.propertyhelper.IgnoreWarnFailCreate;
import org.basepom.mojo.propertyhelper.PropertyElementContext;
import org.basepom.mojo.propertyhelper.TransformerRegistry;
import org.basepom.mojo.propertyhelper.ValueCache;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;

/**
 * Common properties for a field.
 */
public abstract class FieldDefinition {

    protected FieldDefinition() {
    }

    @VisibleForTesting
    protected FieldDefinition(String id) {
        this.id = id;
    }

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
    String propertyNameInFile = null;

    /**
     * Name of the properties file to persist the count. Field injected by Maven.
     */
    File propertyFile = null;

    /**
     * What to do when the property is missing from the file.
     */
    private IgnoreWarnFailCreate onMissingFile = IgnoreWarnFailCreate.FAIL;

    public void setOnMissingFile(String onMissingFile) {
        this.onMissingFile = IgnoreWarnFailCreate.forString(onMissingFile);
    }

    /**
     * What to do when the property is missing from the file.
     */
    private IgnoreWarnFailCreate onMissingProperty = IgnoreWarnFailCreate.FAIL;

    public void setOnMissingProperty(String onMissingProperty) {
        this.onMissingProperty = IgnoreWarnFailCreate.forString(onMissingProperty);
    }

    /**
     * The initial value for this field. Field injected by Maven.
     */
    String initialValue = null;

    /**
     * Format for this element. Field injected by Maven.
     */
    String format = null;

    /**
     * Comma separated list of String transformers.
     */
    private List<String> transformers = List.of();

    void setTransformers(String transformers) {
        this.transformers = Splitter.on(",")
            .omitEmptyStrings()
            .trimResults()
            .splitToList(transformers);
    }

    public abstract Field createPropertyElement(PropertyElementContext context, ValueCache valueCache) throws IOException;

    public String getId() {
        return id;
    }

    public boolean isSkip() {
        return skip;
    }

    public List<String> getTransformers() {
        return transformers;
    }

    public Optional<String> getInitialValue() {
        return Optional.ofNullable(initialValue);
    }

    public boolean isExport() {
        return export;
    }

    public String getPropertyNameInFile() {
        return Objects.requireNonNullElse(propertyNameInFile, id);
    }

    public Optional<File> getPropertyFile() {
        return Optional.ofNullable(propertyFile);
    }

    public IgnoreWarnFailCreate getOnMissingFile() {
        return onMissingFile;
    }

    public IgnoreWarnFailCreate getOnMissingProperty() {
        return onMissingProperty;
    }

    public String formatResult(final String value) {
        final Optional<String> format = getFormat();
        String res = format.map(f -> format(f, value)).orElse(value);

        res = TransformerRegistry.INSTANCE.applyTransformers(transformers, res);

        return Optional.ofNullable(res).orElse("");
    }

    public Optional<String> getFormat() {
        return Optional.ofNullable(format);
    }

    public void check() {
        checkState(id != null, "the id element must not be empty!");

        if (propertyNameInFile != null) {
            checkState(propertyFile != null, "can not define <propertyNameInFile> without defining <propertyFile>!");
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FieldDefinition.class.getSimpleName() + "[", "]")
            .add("id='" + id + "'")
            .add("skip=" + skip)
            .add("export=" + export)
            .add("propertyNameInFile='" + propertyNameInFile + "'")
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
        FieldDefinition that = (FieldDefinition) o;
        return skip == that.skip && export == that.export && Objects.equals(id, that.id) && Objects.equals(propertyNameInFile, that.propertyNameInFile)
            && Objects.equals(propertyFile, that.propertyFile) && Objects.equals(onMissingFile, that.onMissingFile)
            && Objects.equals(onMissingProperty, that.onMissingProperty) && Objects.equals(initialValue, that.initialValue)
            && Objects.equals(format, that.format) && Objects.equals(transformers, that.transformers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, skip, export, propertyNameInFile, propertyFile, onMissingFile, onMissingProperty, initialValue, format, transformers);
    }
}
