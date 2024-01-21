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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.basepom.mojo.propertyhelper.ValueProvider.PropertyProvider;
import org.basepom.mojo.propertyhelper.ValueProvider.StaticValueProvider;
import org.basepom.mojo.propertyhelper.beans.UuidDefinition;

import java.util.Properties;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestUuidField {

    @Test
    public void testSimple() {
        final UUID uuid = UUID.randomUUID();
        final UuidDefinition f1 = new UuidDefinition()
                .setId("hello")
                .setValue(uuid.toString());

        f1.check();

        final UuidField uf1 = new UuidField(f1, ValueProvider.NULL_PROVIDER);
        Assertions.assertEquals(uuid.toString(), uf1.getPropertyValue().get());
    }

    @Test
    public void testSimpleProperty() {
        final UUID uuid = UUID.randomUUID();
        final UuidDefinition f1 = new UuidDefinition()
                .setId("hello");

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello", uuid.toString());
        final UuidField uf1 = new UuidField(f1, new PropertyProvider(props, f1.getPropertyName()));
        Assertions.assertEquals(uuid.toString(), uf1.getPropertyValue().get());
    }

    @Test
    public void testSimplePropertyWithDefault() {
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();

        final UuidDefinition f1 = new UuidDefinition()
                .setId("hello")
                .setValue(uuid1.toString());

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello", uuid2.toString());
        final UuidField uf1 = new UuidField(f1, new PropertyProvider(props, f1.getPropertyName()));
        Assertions.assertEquals(uuid2.toString(), uf1.getPropertyValue().get());
    }

    @Test
    public void testNoProperty() {
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();

        final UuidDefinition f1 = new UuidDefinition()
                .setId("hello")
                .setValue(uuid1.toString());

        f1.check();

        final Properties props = new Properties();
        props.setProperty("hello2", uuid2.toString());
        final UuidField uf1 = new UuidField(f1, new PropertyProvider(props, f1.getPropertyName()));
        Assertions.assertEquals(uuid1.toString(), uf1.getPropertyValue().get());
    }

    public void testNothing() {
        final UUID uuid = UUID.randomUUID();

        final UuidDefinition f1 = new UuidDefinition()
                .setId("hello");

        f1.check();

        final ValueProvider provider = new StaticValueProvider();
        provider.setValue(uuid.toString());
        final UuidField uf1 = new UuidField(f1, provider);
        Assertions.assertEquals(uuid.toString(), uf1.getPropertyValue().get());
    }

    @Test
    public void testMissingProperty() {
        ValueCache valueCache = ValueCache.forTesting();

        assertThrows(IllegalStateException.class, () -> {
            final UuidDefinition f1 = new UuidDefinition()
                    .setId("hello")
                    .setOnMissingProperty("fail");

            f1.check();

            final ValueProvider provider = valueCache.findCurrentValueProvider(ImmutableMap.<String, String>of(), f1);

            final UuidField uf1 = new UuidField(f1, provider);
            Assertions.assertFalse(uf1.getPropertyValue().isPresent());
        });
    }
}
