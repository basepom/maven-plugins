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

import org.basepom.mojo.propertyhelper.InterpolatorFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPropertyGroup {

    private final InterpolatorFactory interpolatorFactory = new InterpolatorFactory(null);

    @Test
    public void testConstant() throws Exception {
        final Map<String, String> props = ImmutableMap.of("hello", "world");

        final PropertyGroup pg = new PropertyGroup()
                .setId("hello")
                .setProperties(props);

        final List<String> propNames = Lists.newArrayList(pg.getPropertyNames());
        Assertions.assertEquals(1, propNames.size());
        Assertions.assertEquals("hello", propNames.get(0));

        final String propValue = pg.getPropertyValue(interpolatorFactory, "hello", Collections.emptyMap());
        Assertions.assertEquals("world", propValue);
    }

    @Test
    public void testRenderSingle() throws Exception {
        final Map<String, String> props = ImmutableMap.of("hello", "#{world}");

        final PropertyGroup pg = new PropertyGroup()
                .setId("hello")
                .setProperties(props);

        final List<String> propNames = Lists.newArrayList(pg.getPropertyNames());
        Assertions.assertEquals(1, propNames.size());
        Assertions.assertEquals("hello", propNames.get(0));

        final String propValue = pg.getPropertyValue(interpolatorFactory, "hello", ImmutableMap.of("world", "pizza"));
        Assertions.assertEquals("pizza", propValue);
    }

    @Test
    public void testRenderEmptyFail() {
        assertThrows(IllegalStateException.class, () -> {
            final Map<String, String> props = ImmutableMap.of("hello", "#{world}");

            final PropertyGroup pg = new PropertyGroup()
                    .setId("hello")
                    .setProperties(props);

            final List<String> propNames = Lists.newArrayList(pg.getPropertyNames());
            Assertions.assertEquals(1, propNames.size());
            Assertions.assertEquals("hello", propNames.get(0));

            final String propValue = pg.getPropertyValue(interpolatorFactory, "hello", Collections.emptyMap());
            Assertions.assertEquals("", propValue);
        });
    }

    @Test
    public void testRenderEmptyOk() throws Exception {
        final Map<String, String> props = ImmutableMap.of("hello", "nice-#{world}-hat");

        final PropertyGroup pg = new PropertyGroup()
                .setId("hello")
                .setProperties(props)
                .setOnMissingProperty("ignore");

        final List<String> propNames = Lists.newArrayList(pg.getPropertyNames());
        Assertions.assertEquals(1, propNames.size());
        Assertions.assertEquals("hello", propNames.get(0));

        final String propValue = pg.getPropertyValue(interpolatorFactory, "hello", Collections.emptyMap());
        Assertions.assertEquals("nice--hat", propValue);
    }

    @Test
    public void testRenderIsReluctant() throws Exception {
        final Map<String, String> props = ImmutableMap.of("hello", "nice-#{first}-#{world}-hat");

        final PropertyGroup pg = new PropertyGroup()
                .setId("hello")
                .setProperties(props)
                .setOnMissingProperty("ignore");

        final List<String> propNames = Lists.newArrayList(pg.getPropertyNames());
        Assertions.assertEquals(1, propNames.size());
        Assertions.assertEquals("hello", propNames.get(0));

        final String propValue = pg.getPropertyValue(interpolatorFactory, "hello", Collections.emptyMap());
        Assertions.assertEquals("nice---hat", propValue);
    }

    @Test
    public void testRenderFriendOfAFriend() throws Exception {
        final Map<String, String> props = ImmutableMap.of("hello", "nice-#{whatWorld}-#{world}-hat");

        final PropertyGroup pg = new PropertyGroup()
                .setId("hello")
                .setProperties(props)
                .setOnMissingProperty("ignore");

        final List<String> propNames = Lists.newArrayList(pg.getPropertyNames());
        Assertions.assertEquals(1, propNames.size());
        Assertions.assertEquals("hello", propNames.get(0));

        final String propValue = pg.getPropertyValue(interpolatorFactory, "hello",
                ImmutableMap.of("whatWorld", "#{first}", "first", "decadent", "world", "rome"));
        Assertions.assertEquals("nice-decadent-rome-hat", propValue);
    }


    @Test
    public void testRenderDotsAreCool() throws Exception {
        final Map<String, String> props = ImmutableMap.of("hello", "nice-#{foo.bar.world}-hat");

        final PropertyGroup pg = new PropertyGroup()
                .setId("hello")
                .setProperties(props)
                .setOnMissingProperty("ignore");

        final List<String> propNames = Lists.newArrayList(pg.getPropertyNames());
        Assertions.assertEquals(1, propNames.size());
        Assertions.assertEquals("hello", propNames.get(0));

        final String propValue = pg.getPropertyValue(interpolatorFactory, "hello", ImmutableMap.of("foo.bar.world", "strange"));
        Assertions.assertEquals("nice-strange-hat", propValue);
    }

}
