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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.dateDefinition;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setFormat;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setTimezone;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.ValueProvider.PropertyBackedValueAdapter;
import org.basepom.mojo.propertyhelper.definitions.DateDefinition;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import org.junit.jupiter.api.Test;


public class TestDateField {

    @Test
    public void testSimple() {
        final DateDefinition d1 = dateDefinition("hello");
        setValue(d1, 0L);
        setTimezone(d1, "UTC");
        setFormat(d1, "yyyyMMdd_HHmmss");

        d1.check();

        final DateField sd1 = DateField.forTesting(d1, ValueProvider.NULL_PROVIDER);
        assertEquals("19700101_000000", sd1.getValue());
    }

    @Test
    public void testProperty() {
        final String format = "yyyyMMdd_HHmmss";
        final DateDefinition d1 = dateDefinition("hello");
        setFormat(d1, format);

        d1.check();

        final var now = LocalDateTime.now();
        final Properties props = new Properties();
        final String value = DateTimeFormatter.ofPattern(format).format(now);
        props.setProperty("hello", value);
        final DateField sd1 = DateField.forTesting(d1, new PropertyBackedValueAdapter(props, d1.getId()));

        assertEquals(value, sd1.getValue());
    }

    @Test
    public void testUnformattedLongProperty() {
        final DateDefinition d1 = dateDefinition("hello");

        d1.check();

        final var now = Instant.now();
        final Properties props = new Properties();
        props.setProperty("hello", Long.toString(now.toEpochMilli()));
        final DateField sd1 = DateField.forTesting(d1, new PropertyBackedValueAdapter(props, d1.getId()));

        ZonedDateTime result = ZonedDateTime.parse(sd1.getValue(), DateTimeFormatter.ISO_DATE_TIME);

        assertThat(result).isCloseTo(ZonedDateTime.ofInstant(now, ZoneId.systemDefault()), within(1, ChronoUnit.MILLIS));
    }

    @Test
    public void testUnformattedStringProperty() {
        final DateDefinition d1 = dateDefinition("hello");

        d1.check();

        final String value = DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.now());

        final Properties props = new Properties();
        props.setProperty("hello", value);
        final DateField sd1 = DateField.forTesting(d1, new PropertyBackedValueAdapter(props, d1.getId()));

        assertEquals(value, sd1.getValue());
    }

    @Test
    public void testNow() {
        final String format = "yyyyMMdd_HHmmss";
        final DateDefinition d1 = dateDefinition("hello");
        setFormat(d1, format);

        d1.check();

        final DateField sd1 = DateField.forTesting(d1, ValueProvider.NULL_PROVIDER);

        final var value = sd1.getValue();

        var now = ZonedDateTime.now().withNano(0);

        var propTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
        final Duration d = Duration.between(propTime, now);
        assertTrue(d.getSeconds() <= 1, format("propTime: %s,  now: %s, diff is %s", propTime, now, d));
    }
}
