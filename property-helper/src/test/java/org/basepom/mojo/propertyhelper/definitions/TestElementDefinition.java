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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.basepom.mojo.propertyhelper.Field;
import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.ValueCache;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestElementDefinition {

    @Test
    public void testValidId() {
        final BasicDefinition fieldDefinition = new BasicDefinition();

        fieldDefinition.id = "hello";
        fieldDefinition.check();
    }

    @Test
    public void testUnsetId() {
        assertThrows(IllegalStateException.class, () -> {
            final BasicDefinition fieldDefinition = new BasicDefinition();
            fieldDefinition.check();
        });
    }

    @Test
    public void testBlankId() {
        assertThrows(IllegalStateException.class, () -> {
            final BasicDefinition fieldDefinition = new BasicDefinition();
            fieldDefinition.check();
        });
    }

    @Test
    public void testDefaults() {
        final String id = UUID.randomUUID().toString();
        final BasicDefinition fieldDefinition = new BasicDefinition();
        fieldDefinition.id = id;
        fieldDefinition.check();
        Assertions.assertEquals(id, fieldDefinition.getId());
        Assertions.assertFalse(fieldDefinition.isSkip());
    }

    public static class BasicDefinition extends FieldDefinition<String> {

        public BasicDefinition() {
        }

        @Override
        public Field<String, BasicDefinition> createField(FieldContext context, ValueCache valueCache) {
            throw new UnsupportedOperationException();
        }
    }
}
