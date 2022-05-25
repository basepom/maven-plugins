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

import org.basepom.mojo.propertyhelper.beans.IgnoreWarnFail;
import org.basepom.mojo.propertyhelper.beans.StringDefinition;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

public class StringField
        implements PropertyElement {

    private final StringDefinition stringDefinition;
    private final ValueProvider valueProvider;

    public StringField(final StringDefinition stringDefinition, final ValueProvider valueProvider) {
        this.stringDefinition = stringDefinition;
        this.valueProvider = valueProvider;
    }

    public static List<StringField> createStrings(final ValueCache valueCache, final StringDefinition... stringDefinitions)
            throws IOException {
        checkNotNull(valueCache, "valueCache is null");

        final Builder<StringField> result = ImmutableList.builder();

        for (StringDefinition stringDefinition : stringDefinitions) {
            stringDefinition.check();
            final ValueProvider stringValue = valueCache.getValueProvider(stringDefinition);
            final StringField stringField = new StringField(stringDefinition, stringValue);
            result.add(stringField);
        }
        return result.build();
    }

    @Override
    public String getPropertyName() {
        // This is not the property name (because many definitions can map onto one prop)
        // but the actual id.
        return stringDefinition.getId();
    }

    @Override
    public Optional<String> getPropertyValue() {
        final List<String> values = Lists.newArrayList();

        final Optional<String> propValue = valueProvider.getValue();
        final List<String> definedValues = stringDefinition.getValues();

        // Only add the value from the provider if it is not null.
        propValue.ifPresent(values::add);

        values.addAll(definedValues);

        for (String value : values) {
            if (stringDefinition.isBlankIsValid() || (value != null && !value.trim().isEmpty())) {
                return stringDefinition.formatResult(value);
            }
        }

        IgnoreWarnFail.checkState(stringDefinition.getOnMissingValue(), false, "value");

        return Optional.empty();
    }

    @Override
    public boolean isExport() {
        return stringDefinition.isExport();
    }

    @Override
    public String toString() {
        return getPropertyValue().orElse("");
    }
}
