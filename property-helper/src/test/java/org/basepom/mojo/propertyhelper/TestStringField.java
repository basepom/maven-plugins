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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.basepom.mojo.propertyhelper.ValueProvider.PropertyProvider;
import org.basepom.mojo.propertyhelper.definitions.StringDefinition;
import org.basepom.mojo.propertyhelper.fields.StringField;

import java.util.List;
import java.util.Properties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

public class TestStringField {

    @Test
    public void testSimple() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setValues(ImmutableList.of("foo"));

        f1.check();

        final StringField sf1 = new StringField(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("foo", sf1.getPropertyValue().get());
    }

    @Test
    public void testTwoValues() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setValues(ImmutableList.of("foo", "bar", "baz"));

        f1.check();

        final StringField sf1 = new StringField(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("foo", sf1.getPropertyValue().get());
    }

    @Test
    public void testIgnoreBlank() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setValues(ImmutableList.of("", "      ", "baz"))
                .setBlankIsValid(false);

        f1.check();

        final StringField sf1 = new StringField(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("baz", sf1.getPropertyValue().get());
    }

    @Test
    public void testAcceptBlank() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setValues(ImmutableList.of("      ", "baz"))
                .setBlankIsValid(true);

        f1.check();

        final StringField sf1 = new StringField(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("      ", sf1.getPropertyValue().get());
    }

    @Test
    public void testAcceptEmpty() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setValues(ImmutableList.of("", "baz"))
                .setBlankIsValid(true);

        f1.check();

        final StringField sf1 = new StringField(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("", sf1.getPropertyValue().get());
    }

    @Test
    public void testNullValueIsEmptyString() {
        final List<String> values = Lists.newArrayList();
        values.add(null);
        values.add("wibble");

        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setValues(values)
                .setBlankIsValid(true);

        f1.check();

        final StringField sf1 = new StringField(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("", sf1.getPropertyValue().get());
    }

    @Test
    public void testSimpleProperty() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello");

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello", "foo");
        final StringField sf1 = new StringField(f1, new PropertyProvider(props, f1.getPropertyName()));
        assertEquals("foo", sf1.getPropertyValue().get());
    }

    @Test
    public void testSimplePropertyWithDefault() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setValues(ImmutableList.of("baz"));

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello", "foo");
        final StringField sf1 = new StringField(f1, new PropertyProvider(props, f1.getPropertyName()));
        assertEquals("foo", sf1.getPropertyValue().get());
    }

    @Test
    public void testNoProperty() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setValues(ImmutableList.of("baz"));

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello2", "foo");
        final StringField sf1 = new StringField(f1, new PropertyProvider(props, f1.getPropertyName()));
        assertEquals("baz", sf1.getPropertyValue().get());
    }

    @Test
    public void testIgnoreBlankProperty() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setBlankIsValid(false)
                .setValues(ImmutableList.of("baz"));

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello", "");
        final StringField sf1 = new StringField(f1, new PropertyProvider(props, f1.getPropertyName()));
        assertEquals("baz", sf1.getPropertyValue().get());
    }

    @Test
    public void testNothing() {

        assertThrows(IllegalStateException.class, () -> {
            final StringDefinition f1 = new StringDefinition()
                    .setId("hello")
                    .setOnMissingValue("fail")
                    .setBlankIsValid(true);
            f1.check();

            final StringField sf1 = new StringField(f1, ValueProvider.NULL_PROVIDER);
            assertEquals("baz", sf1.getPropertyValue().get());
        });
    }

    @Test
    public void testNothingIgnore() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setOnMissingValue("ignore")
                .setBlankIsValid(true);

        f1.check();

        final StringField sf1 = new StringField(f1, ValueProvider.NULL_PROVIDER);
        assertFalse(sf1.getPropertyValue().isPresent());
    }

    @Test
    public void testMissingProperty() {
        ValueCache valueCache = ValueCache.forTesting();

        assertThrows(IllegalStateException.class, () -> {
            final StringDefinition f1 = new StringDefinition()
                    .setId("hello")
                    .setOnMissingValue("ignore")
                    .setOnMissingProperty("fail")
                    .setBlankIsValid(true);

            f1.check();

            final ValueProvider provider = valueCache.findCurrentValueProvider(ImmutableMap.<String, String>of(), f1);

            final StringField sf1 = new StringField(f1, provider);
            assertFalse(sf1.getPropertyValue().isPresent());
        });
    }

    @Test
    public void testBlankPropertyValue() {
        ValueCache valueCache = ValueCache.forTesting();

        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setBlankIsValid(true);

        f1.check();

        final ImmutableMap<String, String> props = ImmutableMap.of("hello", "");
        final ValueProvider provider = valueCache.findCurrentValueProvider(props, f1);

        final StringField sf1 = new StringField(f1, provider);
        assertEquals("", sf1.getPropertyValue().get());
    }

    @Test
    public void testBlankValue() {
        final StringDefinition f1 = new StringDefinition()
                .setId("hello")
                .setValues(ImmutableList.of("", "foo"))
                .setBlankIsValid(true);

        f1.check();

        final StringField sf1 = new StringField(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("", sf1.getPropertyValue().get());
    }
}
