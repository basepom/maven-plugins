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

import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.fields.NumberField;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;

public class NumberDefinition extends FieldDefinition<String> {

    public static final String INITIAL_VALUE = "0";

    /**
     * If a multi-number, which field to increment. Field injected by Maven.
     */
    Integer fieldNumber;

    /**
     * Increment of the property when changing it. Field injected by Maven.
     */
    int increment = 1;

    public NumberDefinition() {
        initialValue = INITIAL_VALUE;
    }

    @VisibleForTesting
    NumberDefinition(String id) {
        super(id);
        initialValue = INITIAL_VALUE;
    }


    public Optional<Integer> getFieldNumber() {
        return Optional.ofNullable(fieldNumber);
    }

    public int getIncrement() {
        return increment;
    }

    @Override
    public void check() {
        super.check();
        checkState(getInitialValue().isPresent(), "initial value must be defined");
        getFieldNumber().ifPresent(fieldNumber -> checkState(fieldNumber >= 0, "the field number must be >= 0"));
    }

    @Override
    public NumberField createField(FieldContext context, ValueCache valueCache) throws IOException {
        checkNotNull(context, "context is null");
        checkNotNull(valueCache, "valueCache is null");

        check();

        final ValueProvider numberValue = valueCache.getValueProvider(this);
        return new NumberField(this, numberValue, context);
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
        return increment == that.increment && Objects.equals(fieldNumber, that.fieldNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fieldNumber, increment);
    }
}
