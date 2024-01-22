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

import static java.lang.String.format;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.dateDefinition;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setFormat;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setTimezone;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.basepom.mojo.propertyhelper.ValueProvider.PropertyProvider;
import org.basepom.mojo.propertyhelper.definitions.DateDefinition;
import org.basepom.mojo.propertyhelper.fields.DateField;

import java.util.Optional;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.Test;


public class TestDateField {

    @Test
    public void testSimple() {
        final DateDefinition d1 = dateDefinition("hello");
        setValue(d1, 0L);
        setTimezone(d1, "UTC");
        setFormat(d1, "yyyyMMdd_HHmmss");

        d1.check();

        final DateField sd1 = new DateField(d1, ValueProvider.NULL_PROVIDER);
        assertEquals("19700101_000000", sd1.getValue());
    }

    @Test
    public void testProperty() {
        final String format = "yyyyMMdd_HHmmss";
        final DateDefinition d1 = dateDefinition("hello");
        setFormat(d1, format);

        d1.check();

        final long now = System.currentTimeMillis();
        final Properties props = new Properties();
        final String value = DateTimeFormat.forPattern(format).print(now);
        props.setProperty("hello", value);
        final DateField sd1 = new DateField(d1, new PropertyProvider(props, d1.getPropertyName()));

        assertEquals(value, sd1.getValue());
    }

    @Test
    public void testUnformattedLongProperty() {
        final DateDefinition d1 = dateDefinition("hello");

        d1.check();

        final long now = System.currentTimeMillis();
        final Properties props = new Properties();
        props.setProperty("hello", Long.toString(now));
        final DateField sd1 = new DateField(d1, new PropertyProvider(props, d1.getPropertyName()));

        assertEquals(new DateTime(now).toString(), sd1.getValue());
    }

    @Test
    public void testUnformattedStringProperty() {
        final DateDefinition d1 = dateDefinition("hello");

        d1.check();

        final String value = DateTime.now().toString();

        final Properties props = new Properties();
        props.setProperty("hello", value);
        final DateField sd1 = new DateField(d1, new PropertyProvider(props, d1.getPropertyName()));

        assertEquals(value, sd1.getValue());
    }

    @Test
    public void testNow() {
        final String format = "yyyyMMdd_HHmmss";
        final DateDefinition d1 = dateDefinition("hello");
        setFormat(d1, format);

        d1.check();

        final DateField sd1 = new DateField(d1, ValueProvider.NULL_PROVIDER);

        final var value = sd1.getValue();

        final DateTime now = new DateTime().withMillisOfSecond(0);

        final DateTime propTime = DateTimeFormat.forPattern(format).parseDateTime(value);
        final Duration d = new Duration(propTime, now);
        assertTrue(d.getStandardSeconds() <= 1, format("propTime: %s,  now: %s, diff is %s", propTime, now, d));
    }
}
