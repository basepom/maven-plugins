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

import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.numberDefinition;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setFieldNumber;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.basepom.mojo.propertyhelper.ValueProvider.PropertyProvider;
import org.basepom.mojo.propertyhelper.definitions.NumberDefinition;
import org.basepom.mojo.propertyhelper.fields.NumberField;

import java.util.Properties;

import org.junit.jupiter.api.Test;


public class TestNumberField {

    @Test
    public void testSimple() {
        final NumberDefinition f1 = numberDefinition("hello");
        setFieldNumber(f1, 0);
        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello", "100");
        final NumberField nf1 = new NumberField(f1, new PropertyProvider(props, f1.getPropertyName()));
        assertEquals(100L, nf1.getNumberValue().longValue());
    }

    @Test
    public void testThreeElements() {
        final NumberDefinition f1 = numberDefinition("hello");
        setFieldNumber(f1, 0);
        final NumberDefinition f2 = numberDefinition("hello");
        setFieldNumber(f2, 1);
        final NumberDefinition f3 = numberDefinition("hello");
        setFieldNumber(f3, 2);
        f1.check();
        f2.check();
        f3.check();

        final String value = "4.8.15";

        final Properties props = new Properties();
        props.setProperty("hello", value);

        final NumberField nf1 = new NumberField(f1, new PropertyProvider(props, f1.getPropertyName()));
        final NumberField nf2 = new NumberField(f2, new PropertyProvider(props, f2.getPropertyName()));
        final NumberField nf3 = new NumberField(f3, new PropertyProvider(props, f3.getPropertyName()));
        assertEquals(4L, nf1.getNumberValue().longValue());
        assertEquals(8L, nf2.getNumberValue().longValue());
        assertEquals(15L, nf3.getNumberValue().longValue());

        assertEquals(value, nf1.getValue());
        assertEquals(value, nf2.getValue());
        assertEquals(value, nf3.getValue());
    }

    @Test
    public void testItsComplicated() {
        final NumberDefinition f1 = numberDefinition("hello");
        setFieldNumber(f1, 0);
        final NumberDefinition f2 = numberDefinition("hello");
        setFieldNumber(f2, 1);
        final NumberDefinition f3 = numberDefinition("hello");
        setFieldNumber(f3, 2);
        final NumberDefinition f4 = numberDefinition("hello");
        setFieldNumber(f4, 3);
        f1.check();
        f2.check();
        f3.check();
        f4.check();

        final String value = "3.2-alpha-1-test-4";

        final Properties props = new Properties();
        props.setProperty("hello", value);

        final NumberField nf1 = new NumberField(f1, new PropertyProvider(props, f1.getPropertyName()));
        final NumberField nf2 = new NumberField(f2, new PropertyProvider(props, f2.getPropertyName()));
        final NumberField nf3 = new NumberField(f3, new PropertyProvider(props, f3.getPropertyName()));
        final NumberField nf4 = new NumberField(f4, new PropertyProvider(props, f4.getPropertyName()));

        assertEquals(3L, nf1.getNumberValue().longValue());
        assertEquals(2L, nf2.getNumberValue().longValue());
        assertEquals(1L, nf3.getNumberValue().longValue());
        assertEquals(4L, nf4.getNumberValue().longValue());

        assertEquals(value, nf1.getValue());
        assertEquals(value, nf2.getValue());
        assertEquals(value, nf3.getValue());
        assertEquals(value, nf4.getValue());
    }

    @Test
    public void testIncrementSingle() {
        final NumberDefinition f1 = numberDefinition("hello");
        setFieldNumber(f1, 0);

        final String value = "foobar-1.2-barfoo-3";

        final Properties props = new Properties();
        props.setProperty("hello", value);

        final NumberField nf1 = new NumberField(f1, new PropertyProvider(props, f1.getPropertyName()));

        assertEquals(1L, nf1.getNumberValue().longValue());
        assertEquals(value, nf1.getValue());

        nf1.increment();

        assertEquals(2L, nf1.getNumberValue().longValue());
        assertEquals("foobar-2.2-barfoo-3", nf1.getValue());
    }

    @Test
    public void testIncrement2() {
        final NumberDefinition f1 = numberDefinition("hello");
        setFieldNumber(f1, 0);
        final NumberDefinition f2 = numberDefinition("hello");
        setFieldNumber(f2, 1);
        f1.check();
        f2.check();

        final String value = "4.8";

        final Properties props = new Properties();
        props.setProperty("hello", value);

        final NumberField nf1 = new NumberField(f1, new PropertyProvider(props, f1.getPropertyName()));
        final NumberField nf2 = new NumberField(f2, new PropertyProvider(props, f2.getPropertyName()));

        assertEquals(4L, nf1.getNumberValue().longValue());
        assertEquals(8L, nf2.getNumberValue().longValue());
        assertEquals(value, nf1.getValue());
        assertEquals(value, nf2.getValue());

        nf1.increment();
        assertEquals(5L, nf1.getNumberValue().longValue());
        assertEquals(8L, nf2.getNumberValue().longValue());
        assertEquals("5.8", nf1.getValue());
        assertEquals("5.8", nf2.getValue());

        nf2.increment();
        assertEquals(5L, nf1.getNumberValue().longValue());
        assertEquals(9L, nf2.getNumberValue().longValue());
        assertEquals("5.9", nf1.getValue());
        assertEquals("5.9", nf2.getValue());
    }

    @Test
    public void testDoubleIncrement() {
        final NumberDefinition f1 = numberDefinition("hello");
        setFieldNumber(f1, 0);
        final NumberDefinition f2 = numberDefinition("hello");
        setFieldNumber(f2, 0);
        f1.check();
        f2.check();

        final String value = "4.8";

        final Properties props = new Properties();
        props.setProperty("hello", value);

        final NumberField nf1 = new NumberField(f1, new PropertyProvider(props, f1.getPropertyName()));
        final NumberField nf2 = new NumberField(f2, new PropertyProvider(props, f2.getPropertyName()));

        assertEquals(4L, nf1.getNumberValue().longValue());
        assertEquals(4L, nf2.getNumberValue().longValue());
        assertEquals(value, nf1.getValue());
        assertEquals(value, nf2.getValue());

        nf1.increment();
        assertEquals(5L, nf1.getNumberValue().longValue());
        assertEquals(5L, nf2.getNumberValue().longValue());
        assertEquals("5.8", nf1.getValue());
        assertEquals("5.8", nf2.getValue());

        nf2.increment();
        assertEquals(6L, nf1.getNumberValue().longValue());
        assertEquals(6L, nf2.getNumberValue().longValue());
        assertEquals("6.8", nf1.getValue());
        assertEquals("6.8", nf2.getValue());
    }
}

