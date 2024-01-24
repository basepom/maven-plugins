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

import static org.assertj.core.api.Assertions.assertThat;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.numberDefinition;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setFieldNumber;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.basepom.mojo.propertyhelper.ValueProvider.PropertyBackedValueAdapter;
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
        final NumberField nf1 = NumberField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));
        assertThat(nf1.getNumberValue()).isPresent().contains(100L);

        assertThat(nf1.getValue()).isEqualTo("100");
        assertThat(props).extracting("hello").isEqualTo("100");
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

        final NumberField nf1 = NumberField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));
        final NumberField nf2 = NumberField.forTesting(f2, new PropertyBackedValueAdapter(props, f2.getId()));
        final NumberField nf3 = NumberField.forTesting(f3, new PropertyBackedValueAdapter(props, f3.getId()));
        assertThat(nf1.getNumberValue()).isPresent().contains(4L);
        assertThat(nf2.getNumberValue()).isPresent().contains(8L);
        assertThat(nf3.getNumberValue()).isPresent().contains(15L);

        assertEquals("4", nf1.getValue());
        assertEquals("8", nf2.getValue());
        assertEquals("15", nf3.getValue());
        assertThat(props).extracting("hello").isEqualTo("4.8.15");
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

        final NumberField nf1 = NumberField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));
        final NumberField nf2 = NumberField.forTesting(f2, new PropertyBackedValueAdapter(props, f2.getId()));
        final NumberField nf3 = NumberField.forTesting(f3, new PropertyBackedValueAdapter(props, f3.getId()));
        final NumberField nf4 = NumberField.forTesting(f4, new PropertyBackedValueAdapter(props, f4.getId()));
        assertThat(nf1.getNumberValue()).isPresent().contains(3L);
        assertThat(nf2.getNumberValue()).isPresent().contains(2L);
        assertThat(nf3.getNumberValue()).isPresent().contains(1L);
        assertThat(nf4.getNumberValue()).isPresent().contains(4L);

        assertEquals("3", nf1.getValue());
        assertEquals("2", nf2.getValue());
        assertEquals("1", nf3.getValue());
        assertEquals("4", nf4.getValue());

        assertThat(props).extracting("hello").isEqualTo(value);
    }

    @Test
    public void testIncrementSingle() {
        final NumberDefinition f1 = numberDefinition("hello");
        setFieldNumber(f1, 0);

        final String value = "foobar-1.2-barfoo-3";

        final Properties props = new Properties();
        props.setProperty("hello", value);

        final NumberField nf1 = NumberField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));

        assertThat(nf1.getNumberValue()).isPresent().contains(1L);
        assertEquals("1", nf1.getValue());

        assertThat(props).extracting("hello").isEqualTo(value);

        nf1.increment();

        assertThat(nf1.getNumberValue()).isPresent().contains(2L);
        assertEquals("2", nf1.getValue());

        assertThat(props).extracting("hello").isEqualTo("foobar-2.2-barfoo-3");
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

        final NumberField nf1 = NumberField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));
        final NumberField nf2 = NumberField.forTesting(f2, new PropertyBackedValueAdapter(props, f2.getId()));

        assertThat(nf1.getNumberValue()).isPresent().contains(4L);
        assertThat(nf2.getNumberValue()).isPresent().contains(8L);

        assertEquals("4", nf1.getValue());
        assertEquals("8", nf2.getValue());

        assertThat(props).extracting("hello").isEqualTo(value);

        nf1.increment();

        assertThat(nf1.getNumberValue()).isPresent().contains(5L);
        assertThat(nf2.getNumberValue()).isPresent().contains(8L);
        assertEquals("5", nf1.getValue());
        assertEquals("8", nf2.getValue());

        assertThat(props).extracting("hello").isEqualTo("5.8");

        nf2.increment();

        assertThat(nf1.getNumberValue()).isPresent().contains(5L);
        assertThat(nf2.getNumberValue()).isPresent().contains(9L);
        assertEquals("5", nf1.getValue());
        assertEquals("9", nf2.getValue());

        assertThat(props).extracting("hello").isEqualTo("5.9");
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

        final NumberField nf1 = NumberField.forTesting(f1, new PropertyBackedValueAdapter(props, f1.getId()));
        final NumberField nf2 = NumberField.forTesting(f2, new PropertyBackedValueAdapter(props, f2.getId()));
        assertThat(nf1.getNumberValue()).isPresent().contains(4L);
        assertThat(nf2.getNumberValue()).isPresent().contains(4L);

        assertEquals("4", nf1.getValue());
        assertEquals("4", nf2.getValue());

        assertThat(props).extracting("hello").isEqualTo(value);

        nf1.increment();

        assertThat(nf1.getNumberValue()).isPresent().contains(5L);
        assertThat(nf2.getNumberValue()).isPresent().contains(5L);
        assertEquals("5", nf1.getValue());
        assertEquals("5", nf2.getValue());

        assertThat(props).extracting("hello").isEqualTo("5.8");

        nf2.increment();

        assertThat(nf1.getNumberValue()).isPresent().contains(6L);
        assertThat(nf2.getNumberValue()).isPresent().contains(6L);

        assertEquals("6", nf1.getValue());
        assertEquals("6", nf2.getValue());

        assertThat(props).extracting("hello").isEqualTo("6.8");
    }
}

