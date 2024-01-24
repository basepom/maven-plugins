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
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setOnMissingFile;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setOnMissingFileProperty;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setPropertyFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.basepom.mojo.propertyhelper.definitions.NumberDefinition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPropertyCache {

    private final Properties props = new Properties();
    private ValueCache pc = null;
    private File propFile = null;
    private FileWriter writer = null;

    @BeforeEach
    public void setUp()
        throws IOException {
        assertNull(pc);
        pc = new ValueCache();

        assertNull(propFile);
        propFile = File.createTempFile("test", null);
        propFile.deleteOnExit();

        assertNull(writer);
        writer = new FileWriter(propFile);
    }

    @AfterEach
    public void tearDown()
        throws IOException {
        assertNotNull(pc);
        assertNotNull(propFile);
        assertNotNull(writer);
        writer.close();
    }

    @Test
    public void testEphemeralDefault()
        throws IOException {
        final NumberDefinition ephemeral = numberDefinition("hello");
        ephemeral.check();
        final ValueProvider valueProvider = pc.getValueProvider(ephemeral);
        assertEquals(ephemeral.getInitialValue(), valueProvider.getValue());
    }

    @Test
    @SuppressFBWarnings(value = "DMI_HARDCODED_ABSOLUTE_FILENAME", justification = "unit test")
    public void testMissingPropertyFileFail() {
        assertThrows(IllegalStateException.class, () -> {
            final NumberDefinition fileBacked = numberDefinition("hello");
            setOnMissingFile(fileBacked, "FAIL");
            setOnMissingFileProperty(fileBacked, "IGNORE");
            setPropertyFile(fileBacked, new File("/does/not/exist"));

            fileBacked.check();
            pc.getValueProvider(fileBacked);
        });
    }

    @Test
    public void testEmptyPropertyFileCreate()
        throws IOException {
        props.store(writer, null);
        writer.flush();
        writer.close();

        final NumberDefinition fileBacked = numberDefinition("hello");
        setOnMissingFile(fileBacked, "FAIL");
        setOnMissingFileProperty(fileBacked, "CREATE");
        setPropertyFile(fileBacked, propFile);
        fileBacked.check();
        final ValueProvider valueProvider = pc.getValueProvider(fileBacked);
        assertEquals(fileBacked.getInitialValue(), valueProvider.getValue());
    }

    @Test
    public void testEmptyPropertyFileIgnore()
        throws IOException {
        props.store(writer, null);
        writer.flush();
        writer.close();

        final NumberDefinition fileBacked = numberDefinition("hello");
        setOnMissingFile(fileBacked, "FAIL");
        setOnMissingFileProperty(fileBacked, "IGNORE");
        setPropertyFile(fileBacked, propFile);

        fileBacked.check();
        final ValueProvider valueProvider = pc.getValueProvider(fileBacked);
        assertFalse(valueProvider.getValue().isPresent());
    }

    @Test
    public void testEmptyPropertyFileFail() {

        assertThrows(IllegalStateException.class, () -> {
            final Properties props = new Properties();
            final FileWriter writer = new FileWriter(propFile);
            props.store(writer, null);
            writer.flush();
            writer.close();

            final NumberDefinition fileBacked = numberDefinition("hello");
            setOnMissingFile(fileBacked, "FAIL");
            setOnMissingFileProperty(fileBacked, "FAIL");
            setPropertyFile(fileBacked, propFile);

            fileBacked.check();
            pc.getValueProvider(fileBacked);
        });
    }

    @Test
    public void testLoadProperty()
        throws IOException {
        final Properties props = new Properties();
        final FileWriter writer = new FileWriter(propFile);
        final String propValue = "12345";

        props.setProperty("hello", propValue);
        props.store(writer, null);
        writer.flush();
        writer.close();

        final NumberDefinition fileBacked = numberDefinition("hello");
        setOnMissingFile(fileBacked, "FAIL");
        setOnMissingFileProperty(fileBacked, "FAIL");
        setPropertyFile(fileBacked, propFile);

        fileBacked.check();
        final ValueProvider valueProvider = pc.getValueProvider(fileBacked);
        assertEquals(propValue, valueProvider.getValue().get());
    }

    @Test
    public void testIgnoreCreate() throws IOException {
        final Properties props = new Properties();
        final FileWriter writer = new FileWriter(propFile);
        final String propValue = "12345";

        props.setProperty("hello", propValue);
        props.store(writer, null);
        writer.flush();
        writer.close();

        final NumberDefinition fileBacked = numberDefinition("hello");
        setOnMissingFile(fileBacked, "FAIL");
        setOnMissingFileProperty(fileBacked, "CREATE");
        setPropertyFile(fileBacked, propFile);
        fileBacked.check();
        final ValueProvider valueProvider = pc.getValueProvider(fileBacked);
        assertEquals(propValue, valueProvider.getValue().get());
    }

    @Test
    public void samePropertyObject()
        throws IOException {
        final Properties props = new Properties();
        final FileWriter writer = new FileWriter(propFile);

        props.setProperty("hello", "hello");
        props.setProperty("world", "world");
        props.store(writer, null);
        writer.flush();
        writer.close();

        final NumberDefinition n1 = numberDefinition("hello");
        setOnMissingFile(n1, "FAIL");
        setOnMissingFileProperty(n1, "FAIL");
        setPropertyFile(n1, propFile);

        final NumberDefinition n2 = numberDefinition("world");
        setOnMissingFile(n2, "FAIL");
        setOnMissingFileProperty(n2, "FAIL");
        setPropertyFile(n2, propFile);

        n1.check();
        n2.check();
        assertEquals("hello", pc.getValueProvider(n1).getValue().get());
        assertEquals("world", pc.getValueProvider(n2).getValue().get());

        assertEquals(pc.getValues(n1).get(), pc.getValues(n2).get());
    }
}
