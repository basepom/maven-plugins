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

import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.fields.DateField;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

import com.google.common.annotations.VisibleForTesting;

public class DateDefinition extends FieldDefinition<ZonedDateTime> {

    /**
     * Timezone for this date. Field injected by Maven.
     */
    String timezone = null;

    /**
     * Value for this date. Field injected by Maven.
     */
    Long value = null;

    public DateDefinition() {
    }

    @VisibleForTesting
    DateDefinition(String id) {
        super(id);
    }

    public ZoneId getTimezone() {
        return Optional.ofNullable(timezone)
            .map(ZoneId::of)
            .orElse(ZoneId.systemDefault());
    }

    public Optional<Long> getValue() {
        return Optional.ofNullable(value);
    }

    @Override
    public DateField createField(FieldContext context, ValueCache valueCache) throws IOException {
        checkNotNull(context, "context is null");
        checkNotNull(valueCache, "valueCache is null");

        check();

        final ValueProvider dateValue = valueCache.getValueProvider(this);
        return new DateField(this, dateValue, context);
    }

    @Override
    public Function<ZonedDateTime, String> getFormat() {
        DateTimeFormatter dateTimeFormatter = getFormatter().orElseGet(this::getFallbackFormatter);

        return dateTimeFormatter::format;
    }

    public Function<String, ZonedDateTime> getParser() {
        return value -> getFormatter().map(f -> {
            try {
                return ZonedDateTime.parse(value, f);
            } catch (DateTimeParseException e) {
                return null;
            }
        }).orElseGet(() -> parseLong(value).map(getLongParser())
            .orElseGet(() -> ZonedDateTime.parse(value, getFallbackFormatter())));
    }

    private Optional<Long> parseLong(String value) {
        try {
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Function<Long, ZonedDateTime> getLongParser() {
        return longValue -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(longValue), getTimezone());
    }

    public Optional<DateTimeFormatter> getFormatter() {
        return Optional.ofNullable(format)
            .map(DateTimeFormatter::ofPattern)
            .map(formatter -> formatter.withZone(getTimezone()));
    }

    public DateTimeFormatter getFallbackFormatter() {
        return DateTimeFormatter.ISO_DATE_TIME.withZone(getTimezone());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DateDefinition.class.getSimpleName() + "[", "]")
            .add("timezone='" + timezone + "'")
            .add("value=" + value)
            .add(super.toString())
            .toString();
    }
}
