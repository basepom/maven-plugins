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

import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.numberDefinition;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.basepom.mojo.propertyhelper.IgnoreWarnFailCreate;

import java.util.UUID;

import org.junit.jupiter.api.Test;


public class TestNumberDefinition {

    @Test
    public void testValid() {
        final NumberDefinition nd = numberDefinition("hello");
        nd.export = true;
        nd.initialValue = "1";

        nd.check();
    }

    @Test
    public void testValid2() {
        final NumberDefinition nd = new NumberDefinition();
        nd.id = "hello";
        nd.check();
    }

    @Test
    public void testDefaults() {
        final String id = UUID.randomUUID().toString();
        final NumberDefinition nd = numberDefinition(id);

        nd.check();
        assertEquals(id, nd.getId());
        assertEquals("0", nd.getInitialValue().get());
        assertEquals(0, nd.getFieldNumber());
        assertEquals(1, nd.getIncrement());
        assertEquals(id, nd.getId());
        assertFalse(nd.getPropertyFile().isPresent());
        assertEquals(IgnoreWarnFailCreate.FAIL, nd.getOnMissingFile());
        assertEquals(IgnoreWarnFailCreate.FAIL, nd.getOnMissingProperty());
        assertFalse(nd.isExport());
    }

    @Test
    public void testPropNameOverridesId() {
        final NumberDefinition nd = numberDefinition("hello");
        nd.propertyNameInFile = "world";

        assertEquals("hello", nd.getId());
        assertEquals("world", nd.getPropertyNameInFile());
    }

    @Test
    public void testIdSuppliesPropName() {
        final NumberDefinition nd = numberDefinition("hello");

        assertEquals("hello", nd.getId());
        assertEquals("hello", nd.getPropertyNameInFile());
    }

    @Test
    public void testNullInitialValue() {
        assertThrows(IllegalStateException.class, () -> {
            final NumberDefinition nd = new NumberDefinition();
            nd.initialValue = null;

            nd.check();
        });
    }

    @Test
    public void testBlankInitialValue() {
        assertThrows(IllegalStateException.class, () -> {
            final NumberDefinition nd = new NumberDefinition();
            nd.initialValue = "";

            nd.check();
        });
    }

    @Test
    public void testBadFieldNumber() {
        assertThrows(IllegalStateException.class, () -> {
            final NumberDefinition nd = new NumberDefinition();
            nd.fieldNumber = -1;

            nd.check();
        });
    }
}
