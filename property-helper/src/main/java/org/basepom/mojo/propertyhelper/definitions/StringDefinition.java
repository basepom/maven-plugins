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

import org.basepom.mojo.propertyhelper.IgnoreWarnFail;
import org.basepom.mojo.propertyhelper.PropertyElement;
import org.basepom.mojo.propertyhelper.PropertyElementContext;
import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.fields.StringField;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

public class StringDefinition
    extends AbstractDefinition<StringDefinition> {

    /**
     * Values for this string. Field injected by Maven.
     */
    List<String> values = ImmutableList.of();

    /**
     * Whether a blank string is a valid value. Field injected by Maven.
     */
    boolean blankIsValid = true;

    /**
     * Default action on missing value. Field injected by Maven.
     */
    IgnoreWarnFail onMissingValue = IgnoreWarnFail.FAIL;

    public List<String> getValues() {
        return values;
    }

    @VisibleForTesting
    public StringDefinition setValues(final List<String> values) {
        checkNotNull(values, "values is null");
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (String value : values) {
            builder.add(Objects.requireNonNullElse(value, ""));
        }
        this.values = builder.build();
        return this;
    }

    public boolean isBlankIsValid() {
        return blankIsValid;
    }

    @VisibleForTesting
    public StringDefinition setBlankIsValid(final boolean blankIsValid) {
        this.blankIsValid = blankIsValid;
        return this;
    }

    public IgnoreWarnFail getOnMissingValue() {
        return onMissingValue;
    }

    @VisibleForTesting
    public StringDefinition setOnMissingValue(final String onMissingValue) {
        checkNotNull(onMissingValue, "onMissingValue is null");
        this.onMissingValue = IgnoreWarnFail.forString(onMissingValue);
        return this;
    }

    @Override
    public PropertyElement createPropertyElement(PropertyElementContext context, ValueCache valueCache) throws IOException {
        checkNotNull(context, "context is null");
        checkNotNull(valueCache, "valueCache is null");

        check();

        final ValueProvider stringValue = valueCache.getValueProvider(this);
        return new StringField(this, stringValue);
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
