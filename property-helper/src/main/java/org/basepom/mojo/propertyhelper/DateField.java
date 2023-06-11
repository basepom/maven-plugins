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

import org.basepom.mojo.propertyhelper.beans.DateDefinition;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateField implements PropertyElement {

    private final DateDefinition dateDefinition;
    private final ValueProvider valueProvider;

    public DateField(final DateDefinition dateDefinition, final ValueProvider valueProvider) {
        this.dateDefinition = dateDefinition;
        this.valueProvider = valueProvider;
    }

    public static List<DateField> createDates(final ValueCache valueCache, final DateDefinition... dateDefinitions) throws IOException {
        checkNotNull(valueCache, "valueCache is null");

        final Builder<DateField> result = ImmutableList.builder();

        for (DateDefinition dateDefinition : dateDefinitions) {
            dateDefinition.check();
            final ValueProvider dateValue = valueCache.getValueProvider(dateDefinition);
            final DateField dateField = new DateField(dateDefinition, dateValue);
            result.add(dateField);
        }
        return result.build();
    }

    @Override
    public String getPropertyName() {
        return dateDefinition.getId();
    }

    @Override
    public Optional<String> getPropertyValue() {
        final DateTimeZone timeZone = dateDefinition.getTimezone()
                .map(DateTimeZone::forID)
                .orElse(DateTimeZone.getDefault());

        final Optional<String> format = dateDefinition.getFormat();
        final DateTimeFormatter formatter = format.map(DateTimeFormat::forPattern)
                .orElse(null);

        DateTime date = valueProvider.getValue()
                .map(value -> getDateTime(value, formatter, timeZone))
                .orElse(null);

        if (date == null) {
            date = dateDefinition.getValue()
                .map(definition -> new DateTime(definition, timeZone))
                .orElse(new DateTime(timeZone));
        }

        final String result;
        if (formatter != null) {
            result = formatter.print(date);
            valueProvider.setValue(result);
        } else {
            result = date.toString();
            valueProvider.setValue(Long.toString(date.getMillis()));
        }

        return Optional.ofNullable(
            dateDefinition.getTransformers()
                .map(definition -> TransformerRegistry.applyTransformers(definition, result))
                .orElse(result));
    }

    private DateTime getDateTime(String value, final DateTimeFormatter formatter, final DateTimeZone timeZone) {
        if (value == null) {
            return null;
        }

        if (formatter != null) {
            return formatter.parseDateTime(value).withZone(timeZone);
        }

        try {
            return new DateTime(Long.parseLong(value), timeZone);
        } catch (NumberFormatException nfe) {
            return new DateTime(value, timeZone);
        }
    }

    @Override
    public boolean isExport() {
        return dateDefinition.isExport();
    }

    @Override
    public String toString() {
        return getPropertyValue().orElse("");
    }
}
