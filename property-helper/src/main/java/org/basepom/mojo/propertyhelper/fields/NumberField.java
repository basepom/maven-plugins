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
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.definitions.NumberDefinition;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class NumberField
    implements Field {

    private static final Pattern MATCH_GROUPS = Pattern.compile("\\d+|\\D+");

    private final NumberDefinition numberDefinition;
    private final ValueProvider valueProvider;

    private final List<String> elements = Lists.newArrayList();
    private final List<Integer> numberElements = Lists.newArrayList();

    public NumberField(final NumberDefinition numberDefinition, final ValueProvider valueProvider) {
        this.numberDefinition = numberDefinition;
        this.valueProvider = valueProvider;
    }

    @Override
    public String getFieldName() {
        // This is not the property name (because many definitions can map onto one prop)
        // but the actual id.
        return numberDefinition.getId();
    }

    @Override
    public String getValue() {
        parse();
        final String value = Joiner.on("").join(elements);
        return numberDefinition.formatResult(value);
    }

    @Override
    public boolean isExposeAsProperty() {
        return numberDefinition.isExport();
    }

    private void parse() {
        final Optional<String> value = valueProvider.getValue();

        if (value.isPresent()) {
            final Matcher m = MATCH_GROUPS.matcher(value.get());
            elements.clear();
            numberElements.clear();

            while (m.find()) {
                final String matchValue = m.group();
                elements.add(matchValue);
                if (isNumber(matchValue)) {
                    numberElements.add(elements.size() - 1);
                }
            }

            checkState(numberElements.size() > numberDefinition.getFieldNumber(), "Only %s fields in %s, field %s requested.",
                numberElements.size(), value, numberDefinition.getFieldNumber());
        }
    }

    private boolean isNumber(final CharSequence c) {
        for (int i = 0; i < c.length(); i++) {
            if (!Character.isDigit(c.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void increment() {
        final Long value = getNumberValue();
        if (value != null) {
            setNumberValue(value + numberDefinition.getIncrement());
        }
    }

    public Long getNumberValue() {
        parse();
        return numberElements.isEmpty() ? null : Long.valueOf(elements.get(numberElements.get(numberDefinition.getFieldNumber())));
    }

    public void setNumberValue(final Long value) {
        parse();
        if (!numberElements.isEmpty()) {
            elements.set(numberElements.get(numberDefinition.getFieldNumber()), value.toString());
            valueProvider.setValue(Joiner.on("").join(elements));
        }
    }

    @Override
    public String toString() {
        return getValue();
    }
}
