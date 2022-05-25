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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class TestTransformerRegistry {

    @Test
    public void testLowercase() {
        final Function<String, String> transformer = TransformerRegistry.forName("lowercase");

        assertEquals(" hello, world! ", transformer.apply(" Hello, World! "));
    }

    @Test
    public void testUppercase() {
        final Function<String, String> transformer = TransformerRegistry.forName("uppercase");

        assertEquals(" HELLO, WORLD! ", transformer.apply(" Hello, World! "));
    }

    @Test
    public void testRemoveWhitespace() {
        final Function<String, String> transformer = TransformerRegistry.forName("remove_whitespace");

        assertEquals("Hello,World!", transformer.apply(" Hello, World! "));
    }

    @Test
    public void testUnderscoreForWhitespace() {
        final Function<String, String> transformer = TransformerRegistry.forName("underscore_for_whitespace");

        assertEquals("_Hello,_World!_", transformer.apply(" Hello, World! "));
    }

    @Test
    public void testDashForWhitespace() {
        final Function<String, String> transformer = TransformerRegistry.forName("dash_for_whitespace");

        assertEquals("-Hello,-World!-", transformer.apply(" Hello, World! "));
    }

    @Test
    public void testUseUnderscore() {
        final Function<String, String> transformer = TransformerRegistry.forName("use_underscore");

        assertEquals("_This:_Is_a_test!", transformer.apply(" This: Is_a-test!"));
    }

    @Test
    public void testUseDash() {
        final Function<String, String> transformer = TransformerRegistry.forName("use_dash");

        assertEquals("-This:-Is-a-test!", transformer.apply(" This: Is_a-test!"));
    }

    @Test
    public void testTrim() {
        final Function<String, String> transformer = TransformerRegistry.forName("trim");

        assertEquals("Hello, World!", transformer.apply(" Hello, World! "));
    }
}
