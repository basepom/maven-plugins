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

import org.basepom.mojo.propertyhelper.Field;
import org.basepom.mojo.propertyhelper.InterpolatorFactory;
import org.basepom.mojo.propertyhelper.TransformerRegistry;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.definitions.DateDefinition;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;


public final class DateField extends Field<ZonedDateTime, DateDefinition> {

    private final ValueProvider valueProvider;
    private final ZoneId timezone;

    @VisibleForTesting
    public static DateField forTesting(DateDefinition dateDefinition, ValueProvider valueProvider) {
        return new DateField(dateDefinition, valueProvider, InterpolatorFactory.forTesting(), TransformerRegistry.INSTANCE);
    }

    public DateField(final DateDefinition dateDefinition, final ValueProvider valueProvider,
        final InterpolatorFactory interpolatorFactory,  final TransformerRegistry transformerRegistry) {
        super(dateDefinition, interpolatorFactory, transformerRegistry);

        this.valueProvider = valueProvider;
        this.timezone = dateDefinition.getTimezone();
    }

    @Override
    public String getFieldName() {
        return fieldDefinition.getId();
    }

    @Override
    public String getValue() {
        ZonedDateTime date = valueProvider.getValue()
            .map(value -> fieldDefinition.getParser().apply(value))
            .orElseGet(() -> fieldDefinition.getValue()
                .map(fieldDefinition.getLongParser())
                .orElseGet(this::now));

        String result = formatResult(date);

        if (fieldDefinition.getFormatter().isPresent()) {
            // format was set, store time in the chosen format
            valueProvider.setValue(result);
        } else {
            // not format was set. Store time as millis.
            valueProvider.setValue(Long.toString(date.toInstant().toEpochMilli()));
        }

        return result;
    }

    @Override
    public boolean isExposeAsProperty() {
        return fieldDefinition.isExport();
    }

    private ZonedDateTime now() {
        // code only saves in millisecond precision.
        return ZonedDateTime.now(timezone).truncatedTo(ChronoUnit.MILLIS);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DateField.class.getSimpleName() + "[", "]")
            .add("valueProvider=" + valueProvider)
            .toString();
    }
}
