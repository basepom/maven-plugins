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

import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.IgnoreWarnFail;
import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.fields.StringField;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

/**
 * Defines a string field. This is a config element that is populated by maven.
 */
public class StringDefinition extends FieldDefinition<String> {

    private List<String> values = ImmutableList.of();


    /**
     * called by maven
     */
    public StringDefinition setValues(final List<String> values) {
        checkNotNull(values, "values is null");
        this.values = values.stream().map(v -> Objects.requireNonNullElse(v, "")).collect(ImmutableList.toImmutableList());
        return this;
    }


    /**
     * Whether a blank string is a valid value. Field injected by Maven.
     */
    boolean blankIsValid = true;

    /**
     * Default action on missing value.
     */
    private IgnoreWarnFail onMissingValue = IgnoreWarnFail.FAIL;

    /**
     * called by maven
     */
    public void setOnMissingValue(String onMissingValue) {
        this.onMissingValue = IgnoreWarnFail.forString(onMissingValue);
    }

    /**
     * called by maven
     */
    public StringDefinition() {
    }

    @VisibleForTesting
    StringDefinition(String id) {
        super(id);
    }

    public List<String> getValues() {
        return values;
    }

    public boolean isBlankIsValid() {
        return blankIsValid;
    }

    public IgnoreWarnFail getOnMissingValue() {
        return onMissingValue;
    }

    @Override
    public StringField createField(FieldContext context, ValueCache valueCache) throws IOException {
        checkNotNull(context, "context is null");
        checkNotNull(valueCache, "valueCache is null");

        check();

        final ValueProvider stringValue = valueCache.getValueProvider(this);
        return StringField.forTesting(this, stringValue);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StringDefinition.class.getSimpleName() + "[", "]")
            .add("values=" + values)
            .add("blankIsValid=" + blankIsValid)
            .add("onMissingValue=" + onMissingValue)
            .add(super.toString())
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
        if (!super.equals(o)) {
            return false;
        }
        StringDefinition that = (StringDefinition) o;
        return blankIsValid == that.blankIsValid && Objects.equals(values, that.values) && onMissingValue == that.onMissingValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), values, blankIsValid, onMissingValue);
    }
}
