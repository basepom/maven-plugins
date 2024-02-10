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
import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.definitions.UuidDefinition;

import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;
import java.util.UUID;

import com.google.common.annotations.VisibleForTesting;

public final class UuidField extends Field<String, UuidDefinition> {

    private final ValueProvider valueProvider;
    private final Random secureRandom;

    @VisibleForTesting
    public static UuidField forTesting(UuidDefinition uuidDefinition, ValueProvider valueProvider) {
        return new UuidField(uuidDefinition, valueProvider, FieldContext.forTesting());
    }

    public UuidField(final UuidDefinition uuidDefinition, final ValueProvider valueProvider,
        FieldContext fieldContext) {
        super(uuidDefinition, fieldContext);

        this.valueProvider = checkNotNull(valueProvider, "valueProvider is null");

        this.secureRandom = fieldContext.getRandom();
    }

    @Override
    public String getFieldName() {
        return fieldDefinition.getId();
    }

    @Override
    public String getValue() {
        final Optional<String> propValue = valueProvider.getValue();

        // Only add the value from the provider if it is not null.
        UUID result = propValue.map(UUID::fromString)
            .orElse(fieldDefinition.getValue()
                .orElseGet(this::createRandomUUID));

        valueProvider.setValue(result.toString());
        return formatResult(result.toString());
    }

    private UUID createRandomUUID() {
        long upperValue = secureRandom.nextLong();
        long lowerValue = secureRandom.nextLong();

        // UUID v4 (random) - see https://datatracker.ietf.org/doc/html/rfc4122#section-4.4
        lowerValue &= 0xff0fffffffffffffL; // clear top four bits of time_hi_and_version
        lowerValue |= 0x0040000000000000L; // set to 0100 (UUID v4)
        upperValue &= 0xffffffffffffff3fL; // clear top two bits of clock_seq_hi_and_reserved
        upperValue |= 0x0000000000000080L; // set to 1 and 0

        return new UUID(upperValue, lowerValue);
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", UuidField.class.getSimpleName() + "[", "]")
            .add("uuidDefinition=" + fieldDefinition)
            .add("valueProvider=" + valueProvider)
            .toString();
    }
}
