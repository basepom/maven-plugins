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

import static com.google.common.base.Preconditions.checkNotNull;

import org.basepom.mojo.propertyhelper.Field;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.definitions.UuidDefinition;

import java.util.Optional;
import java.util.UUID;

public class UuidField
    implements Field {

    private final UuidDefinition uuidDefinition;
    private final ValueProvider valueProvider;

    public UuidField(final UuidDefinition uuidDefinition, final ValueProvider valueProvider) {
        this.uuidDefinition = checkNotNull(uuidDefinition, "uuidDefinition is null");
        this.valueProvider = checkNotNull(valueProvider, "valueProvider is null");
    }

    @Override
    public String getFieldName() {
        return uuidDefinition.getId();
    }

    @Override
    public String getValue() {
        final Optional<String> propValue = valueProvider.getValue();

        // Only add the value from the provider if it is not null.
        UUID result = propValue.map(UUID::fromString)
            .orElse(uuidDefinition.getValue()
                .orElse(UUID.randomUUID()));

        valueProvider.setValue(result.toString());
        return uuidDefinition.formatResult(result.toString());
    }

    @Override
    public boolean isExposeAsProperty() {
        return uuidDefinition.isExport();
    }

    @Override
    public String toString() {
        return getValue();
    }
}