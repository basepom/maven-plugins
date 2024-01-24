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

package org.basepom.mojo.propertyhelper.groups;

import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.propertyDefinition;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.propertyGroupDefinition;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setOnMissingProperty;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setProperties;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.definitions.PropertyDefinition;
import org.basepom.mojo.propertyhelper.definitions.PropertyGroupDefinition;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPropertyGroup {
    @Test
    public void testConstant() {
        final PropertyGroupDefinition propertyGroupDefinition = propertyGroupDefinition("hello-group");
        final PropertyDefinition propertyDefinition = propertyDefinition("hello", "world");
        setProperties(propertyGroupDefinition, propertyDefinition);

        PropertyGroup propertyGroup = propertyGroupDefinition.createGroup(FieldContext.forTesting());

        final List<String> propertyNames = Lists.newArrayList(propertyGroupDefinition.getPropertyNames());
        Assertions.assertEquals(1, propertyNames.size());
        Assertions.assertEquals("hello", propertyNames.get(0));

        final String propertyValue = propertyGroup.getPropertyValue(propertyDefinition, Collections.emptyMap());
        Assertions.assertEquals("world", propertyValue);
    }

    @Test
    public void testRenderSingle() {
        final PropertyGroupDefinition propertyGroupDefinition = propertyGroupDefinition("hello-group");
        final PropertyDefinition propertyDefinition = propertyDefinition("hello", "#{world}");
        setProperties(propertyGroupDefinition, propertyDefinition);

        PropertyGroup propertyGroup = propertyGroupDefinition.createGroup(FieldContext.forTesting());

        final List<String> propertyNames = Lists.newArrayList(propertyGroupDefinition.getPropertyNames());
        Assertions.assertEquals(1, propertyNames.size());
        Assertions.assertEquals("hello", propertyNames.get(0));

        final String propertyValue = propertyGroup.getPropertyValue(propertyDefinition, ImmutableMap.of("world", "pizza"));
        Assertions.assertEquals("pizza", propertyValue);
    }

    @Test
    public void testRenderEmptyFail() {
        assertThrows(IllegalStateException.class, () -> {
            final PropertyGroupDefinition propertyGroupDefinition = propertyGroupDefinition("hello-group");
            final PropertyDefinition propertyDefinition = propertyDefinition("hello", "#{world}");
            setProperties(propertyGroupDefinition, propertyDefinition);

            PropertyGroup propertyGroup = propertyGroupDefinition.createGroup(FieldContext.forTesting());

            final List<String> propertyNames = Lists.newArrayList(propertyGroupDefinition.getPropertyNames());
            Assertions.assertEquals(1, propertyNames.size());
            Assertions.assertEquals("hello", propertyNames.get(0));

            final String propertyValue = propertyGroup.getPropertyValue(propertyDefinition, Collections.emptyMap());
            Assertions.assertEquals("", propertyValue);
        });
    }

    @Test
    public void testRenderEmptyOk() {
        final PropertyGroupDefinition propertyGroupDefinition = propertyGroupDefinition("hello-group");
        final PropertyDefinition propertyDefinition = propertyDefinition("hello", "nice-#{world}-hat");
        setProperties(propertyGroupDefinition, propertyDefinition);
        setOnMissingProperty(propertyGroupDefinition, "ignore");

        PropertyGroup propertyGroup = propertyGroupDefinition.createGroup(FieldContext.forTesting());

        final List<String> propertyNames = Lists.newArrayList(propertyGroupDefinition.getPropertyNames());
        Assertions.assertEquals(1, propertyNames.size());
        Assertions.assertEquals("hello", propertyNames.get(0));

        final String propertyValue = propertyGroup.getPropertyValue(propertyDefinition, Collections.emptyMap());
        Assertions.assertEquals("nice--hat", propertyValue);
    }

    @Test
    public void testRenderIsReluctant() {
        final PropertyGroupDefinition propertyGroupDefinition = propertyGroupDefinition("hello-group");
        final PropertyDefinition propertyDefinition = propertyDefinition("hello", "nice-#{first}-#{world}-hat");
        setProperties(propertyGroupDefinition, propertyDefinition);
        setOnMissingProperty(propertyGroupDefinition, "ignore");

        PropertyGroup propertyGroup = propertyGroupDefinition.createGroup(FieldContext.forTesting());

        final List<String> propertyNames = Lists.newArrayList(propertyGroupDefinition.getPropertyNames());
        Assertions.assertEquals(1, propertyNames.size());
        Assertions.assertEquals("hello", propertyNames.get(0));

        final String propertyValue = propertyGroup.getPropertyValue(propertyDefinition, Collections.emptyMap());
        Assertions.assertEquals("nice---hat", propertyValue);
    }

    @Test
    public void testRenderFriendOfAFriend() throws Exception {
        final PropertyGroupDefinition propertyGroupDefinition = propertyGroupDefinition("hello-group");
        final PropertyDefinition propertyDefinition = propertyDefinition("hello", "nice-#{whatWorld}-#{world}-hat");
        setProperties(propertyGroupDefinition, propertyDefinition);
        setOnMissingProperty(propertyGroupDefinition, "ignore");

        PropertyGroup propertyGroup = propertyGroupDefinition.createGroup(FieldContext.forTesting());

        final List<String> propertyNames = Lists.newArrayList(propertyGroupDefinition.getPropertyNames());
        Assertions.assertEquals(1, propertyNames.size());
        Assertions.assertEquals("hello", propertyNames.get(0));

        final String propertyValue = propertyGroup.getPropertyValue(propertyDefinition,
            ImmutableMap.of("whatWorld", "#{first}", "first", "decadent", "world", "rome"));
        Assertions.assertEquals("nice-decadent-rome-hat", propertyValue);
    }


    @Test
    public void testRenderDotsAreCool() {
        final PropertyGroupDefinition propertyGroupDefinition = propertyGroupDefinition("hello-group");
        final PropertyDefinition propertyDefinition = propertyDefinition("hello", "nice-#{foo.bar.world}-hat");
        setProperties(propertyGroupDefinition, propertyDefinition);
        setOnMissingProperty(propertyGroupDefinition, "ignore");

        PropertyGroup propertyGroup = propertyGroupDefinition.createGroup(FieldContext.forTesting());

        final List<String> propertyNames = Lists.newArrayList(propertyGroupDefinition.getPropertyNames());
        Assertions.assertEquals(1, propertyNames.size());
        Assertions.assertEquals("hello", propertyNames.get(0));

        final String propertyValue = propertyGroup.getPropertyValue(propertyDefinition, ImmutableMap.of("foo.bar.world", "strange"));
        Assertions.assertEquals("nice-strange-hat", propertyValue);
    }

}
