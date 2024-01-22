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

import static java.lang.String.format;
import static org.basepom.mojo.propertyhelper.IgnoreWarnFail.checkIgnoreWarnFailState;

import org.basepom.mojo.propertyhelper.Field;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.definitions.StringDefinition;

import java.util.List;
import java.util.Optional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class StringField
    implements Field {

    private final StringDefinition stringDefinition;
    private final ValueProvider valueProvider;

    public StringField(final StringDefinition stringDefinition, final ValueProvider valueProvider) {
        this.stringDefinition = stringDefinition;
        this.valueProvider = valueProvider;
    }

    @Override
    public String getFieldName() {
        // This is not the property name (because many definitions can map onto one prop)
        // but the actual id.
        return stringDefinition.getId();
    }

    @Override
    public String getValue() {
        final List<String> values = Lists.newArrayList();

        final Optional<String> propValue = valueProvider.getValue();
        final List<String> definedValues = stringDefinition.getValues();

        // Only add the value from the provider if it is not null.
        propValue.ifPresent(values::add);

        values.addAll(definedValues);

        for (String value : values) {
            var stringResult = Strings.nullToEmpty(value);
            if (stringDefinition.isBlankIsValid() || !stringResult.isBlank()) {
                return stringDefinition.formatResult(value);
            }
        }

        checkIgnoreWarnFailState(false, stringDefinition.getOnMissingValue(),
            () -> "",
            () -> format("No value for string field %s found, using an empty value!", getFieldName()));

        return "";
    }

    @Override
    public boolean isExposeAsProperty() {
        return stringDefinition.isExport();
    }

    @Override
    public String toString() {
        return getValue();
    }
}
