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

import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setBlankIsValid;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setOnMissingFileProperty;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setOnMissingValue;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setValues;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.stringDefinition;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.ValueProvider.PropertyBackedValueAdapter;
import org.basepom.mojo.propertyhelper.definitions.DefinitionHelper;
import org.basepom.mojo.propertyhelper.definitions.StringDefinition;

import java.util.Properties;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

public class TestStringField {

    @Test
    public void testSimple() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setValues(f1, "foo");

        f1.check();

        final StringField sf1 = StringField.forTesting(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("foo", sf1.getValue());
    }

    @Test
    public void testTwoValues() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setValues(f1, "foo", "bar", "baz");

        f1.check();

        final StringField sf1 = StringField.forTesting(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("foo", sf1.getValue());
    }

    @Test
    public void testIgnoreBlank() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setValues(f1, "", "      ", "baz");
        setBlankIsValid(f1, false);

        f1.check();

        final StringField sf1 = StringField.forTesting(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("baz", sf1.getValue());
    }

    @Test
    public void testAcceptBlank() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setValues(f1, "      ", "baz");
        setBlankIsValid(f1, true);

        f1.check();

        final StringField sf1 = StringField.forTesting(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("      ", sf1.getValue());
    }

    @Test
    public void testAcceptEmpty() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setValues(f1, "", "baz");
        setBlankIsValid(f1, true);

        f1.check();

        final StringField sf1 = StringField.forTesting(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("", sf1.getValue());
    }

    @Test
    public void testNullValueIsEmptyString() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setValues(f1, null, "wibble");
        setBlankIsValid(f1, true);

        f1.check();

        final StringField sf1 = StringField.forTesting(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("", sf1.getValue());
    }

    @Test
    public void testSimpleProperty() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello", "foo");
        final StringField sf1 = StringField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));
        assertEquals("foo", sf1.getValue());
    }

    @Test
    public void testSimplePropertyWithDefault() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setValues(f1, "baz");

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello", "foo");
        final StringField sf1 = StringField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));
        assertEquals("foo", sf1.getValue());
    }

    @Test
    public void testNoProperty() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setValues(f1, "baz");

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello2", "foo");
        final StringField sf1 = StringField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));
        assertEquals("baz", sf1.getValue());
    }

    @Test
    public void testIgnoreBlankProperty() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setBlankIsValid(f1, false);
        setValues(f1, "baz");

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello", "");
        final StringField sf1 = StringField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));
        assertEquals("baz", sf1.getValue());
    }

    @Test
    public void testNothing() {

        assertThrows(IllegalStateException.class, () -> {
            final StringDefinition f1 = stringDefinition("hello");
            setOnMissingValue(f1, "fail");
            setBlankIsValid(f1, true);
            f1.check();

            final StringField sf1 = StringField.forTesting(f1, ValueProvider.NULL_PROVIDER);
            assertEquals("baz", sf1.getValue());
        });
    }

    @Test
    public void testNothingIgnore() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setOnMissingValue(f1, "ignore");
        setBlankIsValid(f1, true);

        f1.check();

        final StringField sf1 = StringField.forTesting(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("", sf1.getValue());
    }

    @Test
    public void testMissingProperty() {
        ValueCache valueCache = new ValueCache();

        assertThrows(IllegalStateException.class, () -> {
            final StringDefinition f1 = stringDefinition("hello");
            setOnMissingValue(f1, "ignore");
            setOnMissingFileProperty(f1, "fail");
            setBlankIsValid(f1, true);

            f1.check();

            final ValueProvider provider = valueCache.findCurrentValueProvider(ImmutableMap.<String, String>of(), f1);

            final StringField sf1 = StringField.forTesting(f1, provider);
            assertEquals("", sf1.getValue());
        });
    }

    @Test
    public void testBlankPropertyValue() {
        ValueCache valueCache = new ValueCache();

        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setBlankIsValid(f1, true);

        f1.check();

        final ImmutableMap<String, String> props = ImmutableMap.of("hello", "");
        final ValueProvider provider = valueCache.findCurrentValueProvider(props, f1);

        final StringField sf1 = StringField.forTesting(f1, provider);
        assertEquals("", sf1.getValue());
    }

    @Test
    public void testBlankValue() {
        final StringDefinition f1 = DefinitionHelper.stringDefinition("hello");
        setValues(f1, "", "foo");
        setBlankIsValid(f1, true);

        f1.check();

        final StringField sf1 = StringField.forTesting(f1, ValueProvider.NULL_PROVIDER);
        assertEquals("", sf1.getValue());
    }
}
