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
package org.basepom.mojo.repack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class DependencyDefinitionTest {

    @Test
    public void testParsing() {
        // just group id
        DependencyDefinition dependencyDefinition = new DependencyDefinition("foo");
        assertEquals("foo", dependencyDefinition.getGroupId());
        assertEquals("*", dependencyDefinition.getArtifactId());
        assertEquals("jar", dependencyDefinition.getType());
        assertFalse(dependencyDefinition.getClassifier().isPresent());

        // group id and artifact id
        dependencyDefinition = new DependencyDefinition("foo:bar");
        assertEquals("foo", dependencyDefinition.getGroupId());
        assertEquals("bar", dependencyDefinition.getArtifactId());
        assertEquals("jar", dependencyDefinition.getType());
        assertFalse(dependencyDefinition.getClassifier().isPresent());

        // group id and type
        dependencyDefinition = new DependencyDefinition("foo::war");
        assertEquals("foo", dependencyDefinition.getGroupId());
        assertEquals("*", dependencyDefinition.getArtifactId());
        assertEquals("war", dependencyDefinition.getType());
        assertFalse(dependencyDefinition.getClassifier().isPresent());

        // group id and classifier
        dependencyDefinition = new DependencyDefinition("foo:::tests");
        assertEquals("foo", dependencyDefinition.getGroupId());
        assertEquals("*", dependencyDefinition.getArtifactId());
        assertEquals("jar", dependencyDefinition.getType());
        assertTrue(dependencyDefinition.getClassifier().isPresent());
        assertEquals("tests", dependencyDefinition.getClassifier().get());

        // group id and artifact id and type
        dependencyDefinition = new DependencyDefinition("foo:bar:war");
        assertEquals("foo", dependencyDefinition.getGroupId());
        assertEquals("bar", dependencyDefinition.getArtifactId());
        assertEquals("war", dependencyDefinition.getType());
        assertFalse(dependencyDefinition.getClassifier().isPresent());

        // group id and artifact id and classifier
        dependencyDefinition = new DependencyDefinition("foo:bar::tests");
        assertEquals("foo", dependencyDefinition.getGroupId());
        assertEquals("bar", dependencyDefinition.getArtifactId());
        assertEquals("jar", dependencyDefinition.getType());
        assertTrue(dependencyDefinition.getClassifier().isPresent());
        assertEquals("tests", dependencyDefinition.getClassifier().get());

        // group id and artifact id and type and classifier
        dependencyDefinition = new DependencyDefinition("foo:bar:war:tests");
        assertEquals("foo", dependencyDefinition.getGroupId());
        assertEquals("bar", dependencyDefinition.getArtifactId());
        assertEquals("war", dependencyDefinition.getType());
        assertTrue(dependencyDefinition.getClassifier().isPresent());
        assertEquals("tests", dependencyDefinition.getClassifier().get());
    }

    @Test
    public void testWildcardMatches() {
        // wildcard matcher  *:*:jar:<anything>
        DependencyDefinition matcher = new DependencyDefinition("*");
        assertEquals("*", matcher.getGroupId());
        assertEquals("*", matcher.getArtifactId());

        assertTrue(matcher.matches(new DependencyDefinition("foo:bar")));
        assertTrue(matcher.matches(new DependencyDefinition("com.baz:blo")));
        assertTrue(matcher.matches(new DependencyDefinition("foo:bar:jar:tests")));
        // a war is not a jar
        assertFalse(matcher.matches(new DependencyDefinition("foo:bar:war:tests")));

        // com.foo:*
        matcher = new DependencyDefinition("com.foo:*");
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:baz")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:bla.foo")));
        assertFalse(matcher.matches(new DependencyDefinition("com.foobar:bar")));
        assertFalse(matcher.matches(new DependencyDefinition("com.foo.bar:bar")));
        assertFalse(matcher.matches(new DependencyDefinition("org.foo.bar:bar")));

        // com.foo*:*
        matcher = new DependencyDefinition("com.foo*:*");
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:baz")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:bla.foo")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foobar:bar")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foo.bar:bar")));
        assertFalse(matcher.matches(new DependencyDefinition("org.foo.bar:bar")));

        // com.foo:bar*
        matcher = new DependencyDefinition("com.foo:bar*");
        assertFalse(matcher.matches(new DependencyDefinition("com.foo:baz")));
        assertFalse(matcher.matches(new DependencyDefinition("com.foo:bla.foo")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:bar")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:bar-foo")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:barfoo")));
    }

    @Test
    public void testClassifiers() {
        // matcher com.foo:bar:jar:tests
        DependencyDefinition matcher = new DependencyDefinition("com.foo:bar::tests");

        assertTrue(matcher.matches(new DependencyDefinition("com.foo:bar::tests")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:bar:jar:tests")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:bar:test-jar")));

        // matches has a classifier, jar has not
        assertFalse(matcher.matches(new DependencyDefinition("com.foo:bar")));

        // matcher has different classifier than jar
        assertFalse(matcher.matches(new DependencyDefinition("com.foo:bar::sources")));


        // *:*:jar:<any>
        matcher = new DependencyDefinition("*");
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:bar::tests")));
        assertTrue(matcher.matches(new DependencyDefinition("com.foo:bar::sources")));

    }
}
