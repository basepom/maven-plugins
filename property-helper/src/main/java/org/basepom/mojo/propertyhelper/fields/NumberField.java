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

package org.basepom.mojo.propertyhelper.fields;

import static com.google.common.base.Preconditions.checkState;

import org.basepom.mojo.propertyhelper.Field;
import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.definitions.NumberDefinition;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public final class NumberField extends Field<String, NumberDefinition> {

    private final ValueProvider valueProvider;

    // internal state
    private List<NumberElement> numberElements;
    private List<NumberElement> numberIndex;

    @VisibleForTesting
    public static NumberField forTesting(NumberDefinition numberDefinition, ValueProvider valueProvider) {
        return new NumberField(numberDefinition, valueProvider, FieldContext.forTesting());
    }

    public NumberField(final NumberDefinition fieldDefinition, final ValueProvider valueProvider,
        FieldContext fieldContext) {
        super(fieldDefinition, fieldContext);

        this.valueProvider = valueProvider;
    }

    @Override
    public String getFieldName() {
        // This is not the property name (because many definitions can map onto one prop)
        // but the actual id.
        return fieldDefinition.getId();
    }

    @Override
    @SuppressWarnings("PMD.LambdaCanBeMethodReference") // https://github.com/pmd/pmd/issues/5043
    public String getValue() {
        parse();

        return formatResult(value().map(v -> Long.toString(v))
            .orElse(valueProvider.getValue().orElse("")));
    }

    private void parse() {
        final Optional<String> value = valueProvider.getValue();

        ImmutableList.Builder<NumberElement> numberELementBuilder = ImmutableList.builder();
        ImmutableList.Builder<NumberElement> numberIndexBuilder = ImmutableList.builder();

        if (value.isPresent()) {
            String numberValue = value.get();
            if (!numberValue.isBlank()) {
                StringBuilder sb = new StringBuilder();
                int charIndex = 0;
                boolean number = Character.isDigit(numberValue.charAt(charIndex));

                while (charIndex < numberValue.length()) {
                    char currentChar = numberValue.charAt(charIndex);
                    if (number != Character.isDigit(currentChar)) {
                        var numberElement = new NumberElement(number, sb.toString());

                        numberELementBuilder.add(numberElement);
                        if (number) {
                            numberIndexBuilder.add(numberElement);
                        }

                        number = !number;
                        sb.setLength(0);
                    }
                    sb.append(currentChar);
                    charIndex++;
                }
                if (sb.length() > 0) {
                    var numberElement = new NumberElement(number, sb.toString());

                    numberELementBuilder.add(numberElement);
                    if (number) {
                        numberIndexBuilder.add(numberElement);
                    }
                }
            }
        }

        this.numberElements = numberELementBuilder.build();
        this.numberIndex = numberIndexBuilder.build();
    }

    private String print() {
        return Joiner.on("").join(numberElements.stream().map(NumberElement::getFieldValue).iterator());
    }

    private Optional<Long> value() {
        return fieldDefinition.getFieldNumber()
            .map(numberIndex::get)
            .flatMap(NumberElement::getLongValue);
    }

    private void set(long value) {
        Optional<NumberElement> numberElement = fieldDefinition.getFieldNumber()
            .map(numberIndex::get);

        if (numberElement.isPresent()) {
            numberElement.get().setLongValue(value);
            valueProvider.setValue(print());
        } else {
            valueProvider.setValue(Long.toString(value));
        }
    }

    public void increment() {
        parse();

        fieldDefinition.getFieldNumber().ifPresent(fieldNumber -> checkState(numberIndex.size() > fieldNumber,
            "Only %s fields in %s, field %s requested.", numberElements.size(), print(), fieldNumber));

        value().ifPresent(value -> {
            set(value + fieldDefinition.getIncrement());
        });
    }

    public Optional<Long> getNumberValue() {
        parse();

        fieldDefinition.getFieldNumber().ifPresent(fieldNumber -> checkState(numberIndex.size() > fieldNumber,
            "Only %s fields in %s, field %s requested.", numberElements.size(), print(), fieldNumber));

        return value();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NumberField.class.getSimpleName() + "[", "]")
            .add("valueProvider=" + valueProvider)
            .add("numberElements=" + numberElements)
            .add("numberIndex=" + numberIndex)
            .add("fieldDefinition=" + fieldDefinition)
            .toString();
    }

    private static final class NumberElement {

        private final boolean number;
        private String value;

        private NumberElement(boolean number, String value) {
            this.number = number;
            this.value = value;
        }

        String getFieldValue() {
            return value;
        }

        void setLongValue(long value) {
            this.value = Long.toString(value);
        }

        Optional<Long> getLongValue() {
            return number ? Optional.of(Long.parseLong(value)) : Optional.empty();
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", NumberElement.class.getSimpleName() + "[", "]")
                .add("number=" + number)
                .add("value='" + value + "'")
                .toString();
        }
    }
}
