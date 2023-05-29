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

import static org.basepom.mojo.repack.Wildcard.wildcardMatch;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WildcardMatchingTest {

    @Test
    public void testEmptyPattern() {
        assertTrue(wildcardMatch("", ""));
        assertFalse(wildcardMatch("", "x"));
    }

    @Test
    public void testDirectMatches() {
        assertTrue(wildcardMatch("aaa", "aaa"));
        assertTrue(wildcardMatch("aab", "aab"));
        assertFalse(wildcardMatch("aab", "ab"));
        assertFalse(wildcardMatch("ab", "aab"));
        assertFalse(wildcardMatch("ab", "aba"));
    }

    @Test
    public void testSingleWildcards() {
        assertTrue(wildcardMatch("a?a", "aaa"));
        assertTrue(wildcardMatch("aa?", "aab"));
        assertFalse(wildcardMatch("a?b", "ab"));
        assertFalse(wildcardMatch("?b", "aab"));
        assertFalse(wildcardMatch("a?b", "aba"));
    }

    @Test
    public void testEndGlobs() {
        assertTrue(wildcardMatch("aa*", "aaa"));
        assertTrue(wildcardMatch("aab*", "aab"));
        assertFalse(wildcardMatch("aa*", "ab"));
        assertFalse(wildcardMatch("ab*", "aab"));
        assertTrue(wildcardMatch("ab*", "aba"));
    }

    @Test
    public void testMiddleGlobs() {
        assertTrue(wildcardMatch("aa*aa", "aaaa"));
        assertTrue(wildcardMatch("aa*aa", "aaaaa"));
        assertTrue(wildcardMatch("aa*aa", "aabaa"));
        assertTrue(wildcardMatch("ab*ba", "abcsometextherecba"));
        assertTrue(wildcardMatch("spring*works", "spring_summer_works_for_me_works"));

        assertFalse(wildcardMatch("ab*ba", "abbb"));
        assertFalse(wildcardMatch("ab*a", "absomelongtextthatendswithc"));
        assertFalse(wildcardMatch("*abcde", "sometext"));
    }

    @Test
    public void testLastChanceSaloon() {
        assertTrue(wildcardMatch("abc*", "abc"));
        assertTrue(wildcardMatch("abc****", "abc"));
        assertFalse(wildcardMatch("abc****d", "abc"));
        assertFalse(wildcardMatch("abc****?", "abc"));
    }
}
