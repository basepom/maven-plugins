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
import org.basepom.mojo.propertyhelper.TransformerRegistry;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.definitions.DateDefinition;

import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateField implements Field {

    private final DateDefinition dateDefinition;
    private final ValueProvider valueProvider;

    public DateField(final DateDefinition dateDefinition, final ValueProvider valueProvider) {
        this.dateDefinition = dateDefinition;
        this.valueProvider = valueProvider;
    }

    @Override
    public String getFieldName() {
        return dateDefinition.getId();
    }

    @Override
    public String getValue() {

        final DateTimeZone timeZone = dateDefinition.getTimezone()
            .map(DateTimeZone::forID)
            .orElse(DateTimeZone.getDefault());

        final Optional<DateTimeFormatter> formatter = dateDefinition.getFormat()
            .map(DateTimeFormat::forPattern);

        DateTime date = valueProvider.getValue()
            .map(value -> getDateTime(value, formatter, timeZone))
            .orElseGet(() -> dateDefinition.getValue()
                .map(definition -> new DateTime(definition, timeZone))
                .orElse(new DateTime(timeZone)));

        String result = formatter.map(f -> f.print(date))
            .orElse(date.toString());

        if (formatter.isPresent()) {
            valueProvider.setValue(result);
        } else {
            valueProvider.setValue(Long.toString(date.getMillis()));
        }

        return Optional.ofNullable(TransformerRegistry.INSTANCE.applyTransformers(dateDefinition.getTransformers(), result))
            .orElse("");
    }

    @Override
    public boolean isExposeAsProperty() {
        return dateDefinition.isExport();
    }

    private DateTime getDateTime(String value, final Optional<DateTimeFormatter> formatter, final DateTimeZone timeZone) {
        if (value == null) {
            return null;
        }

        return formatter.map(f -> f.parseDateTime(value).withZone(timeZone))
            .orElseGet(() -> {
                try {
                    return new DateTime(Long.parseLong(value), timeZone);
                } catch (NumberFormatException nfe) {
                    return new DateTime(value, timeZone);
                }
            });
    }

    @Override
    public String toString() {
        return getValue();
    }
}