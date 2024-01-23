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

import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setOnMissingProperty;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setValue;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.uuidDefinition;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.ValueProvider.PropertyBackedValueAdapter;
import org.basepom.mojo.propertyhelper.ValueProvider.SingleValueProvider;
import org.basepom.mojo.propertyhelper.definitions.UuidDefinition;
import org.basepom.mojo.propertyhelper.fields.UuidField;

import java.util.Properties;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestUuidField {

    @Test
    public void testSimple() {
        final UUID uuid = UUID.randomUUID();
        final UuidDefinition uuidFieldDefinition = uuidDefinition("hello");
        setValue(uuidFieldDefinition, uuid.toString());

        uuidFieldDefinition.check();

        final UuidField uf1 = UuidField.forTesting(uuidFieldDefinition, ValueProvider.NULL_PROVIDER);
        Assertions.assertEquals(uuid.toString(), uf1.getValue());
    }

    @Test
    public void testSimpleProperty() {
        final UUID uuid = UUID.randomUUID();
        final UuidDefinition uuidDefinition = uuidDefinition("hello");

        uuidDefinition.check();

        final Properties props = new Properties();
        props.setProperty("hello", uuid.toString());
        final UuidField uf1 = UuidField.forTesting(uuidDefinition, new PropertyBackedValueAdapter(props, uuidDefinition.getId()));
        Assertions.assertEquals(uuid.toString(), uf1.getValue());
    }

    @Test
    public void testSimplePropertyWithDefault() {
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();

        final UuidDefinition uuidDefinition = uuidDefinition("hello");
        setValue(uuidDefinition, uuid1.toString());

        uuidDefinition.check();

        final Properties props = new Properties();
        props.setProperty("hello", uuid2.toString());
        final UuidField uf1 = UuidField.forTesting(uuidDefinition, new PropertyBackedValueAdapter(props, uuidDefinition.getId()));
        Assertions.assertEquals(uuid2.toString(), uf1.getValue());
    }

    @Test
    public void testNoProperty() {
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();

        final UuidDefinition uuidDefinition = uuidDefinition("hello");
        setValue(uuidDefinition, uuid1.toString());

        uuidDefinition.check();

        final Properties props = new Properties();
        props.setProperty("hello2", uuid2.toString());
        final UuidField uf1 = UuidField.forTesting(uuidDefinition, new PropertyBackedValueAdapter(props, uuidDefinition.getId()));
        Assertions.assertEquals(uuid1.toString(), uf1.getValue());
    }

    @Test
    public void testNothing() {
        final UUID uuid = UUID.randomUUID();

        final UuidDefinition uuidDefinition = uuidDefinition("hello");

        uuidDefinition.check();

        final ValueProvider provider = new SingleValueProvider();
        provider.setValue(uuid.toString());
        final UuidField uf1 = UuidField.forTesting(uuidDefinition, provider);
        Assertions.assertEquals(uuid.toString(), uf1.getValue());
    }

    @Test
    public void testMissingProperty() {
        ValueCache valueCache = new ValueCache();

        assertThrows(IllegalStateException.class, () -> {
            final UuidDefinition uuidDefinition = uuidDefinition("hello");
            setOnMissingProperty(uuidDefinition, "fail");

            uuidDefinition.check();

            final ValueProvider provider = valueCache.findCurrentValueProvider(ImmutableMap.of(), uuidDefinition);

            final UuidField uf1 = UuidField.forTesting(uuidDefinition, provider);
            Assertions.assertEquals("", uf1.getValue());
        });
    }
}
