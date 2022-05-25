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

package org.basepom.mojo.propertyhelper.beans;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestAbstractDefinition {

    @Test
    public void testValidId() {
        final BasicDefinition ad = new BasicDefinition();

        ad.setId("hello");
        ad.check();
    }

    @Test
    public void testUnsetId() {
        assertThrows(IllegalStateException.class, () -> {
            final BasicDefinition ad = new BasicDefinition();
            ad.check();
        });
    }

    @Test
    public void testBlankId() {
        assertThrows(IllegalStateException.class, () -> {
            final BasicDefinition ad = new BasicDefinition();
            ad.check();
        });
    }

    @Test
    public void testDefaults() {
        final String id = UUID.randomUUID().toString();
        final BasicDefinition ad = new BasicDefinition();
        ad.setId(id);
        ad.check();
        Assertions.assertEquals(id, ad.getId());
        Assertions.assertFalse(ad.isSkip());
    }

    public static class BasicDefinition extends AbstractDefinition<BasicDefinition> {

        public BasicDefinition() {
        }
    }
}
