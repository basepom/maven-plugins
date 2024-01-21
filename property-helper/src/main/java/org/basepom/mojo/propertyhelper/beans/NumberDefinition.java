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

package org.basepom.mojo.propertyhelper.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.basepom.mojo.propertyhelper.NumberField;
import org.basepom.mojo.propertyhelper.PropertyElement;
import org.basepom.mojo.propertyhelper.PropertyElementContext;
import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider;

import java.io.IOException;
import java.util.Objects;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;

public class NumberDefinition
    extends AbstractDefinition<NumberDefinition> {

    public static final String INITIAL_VALUE = "0";

    /**
     * If a multi-number, which field to increment. Field injected by Maven.
     */
    int fieldNumber = 0;

    /**
     * Increment of the property when changing it. Field injected by Maven.
     */
    int increment = 1;

    public NumberDefinition() {
        super();
        setInitialValue(INITIAL_VALUE);
    }

    public int getFieldNumber() {
        return fieldNumber;
    }

    @VisibleForTesting
    public NumberDefinition setFieldNumber(final int fieldNumber) {
        this.fieldNumber = fieldNumber;
        return this;
    }

    public int getIncrement() {
        return increment;
    }

    @VisibleForTesting
    public NumberDefinition setIncrement(final int increment) {
        this.increment = increment;
        return this;
    }

    @Override
    public void check() {
        super.check();
        checkState(getInitialValue().isPresent(), "the initial value must not be empty");
        checkState(fieldNumber >= 0, "the field number must be >= 0");
    }

    @Override
    public PropertyElement createPropertyElement(PropertyElementContext context, ValueCache valueCache) throws IOException {
        checkNotNull(context, "context is null");
        checkNotNull(valueCache, "valueCache is null");

        check();

        final ValueProvider numberValue = valueCache.getValueProvider(this);
        return new NumberField(this, numberValue);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NumberDefinition.class.getSimpleName() + "[", "]")
            .add("fieldNumber=" + fieldNumber)
            .add("increment=" + increment)
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
        NumberDefinition that = (NumberDefinition) o;
        return fieldNumber == that.fieldNumber && increment == that.increment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fieldNumber, increment);
    }
}
