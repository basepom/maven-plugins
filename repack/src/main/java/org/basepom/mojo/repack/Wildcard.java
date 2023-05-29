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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Basic glob matcher that supports '?' and '*'. Does not support char escaping in the pattern or direct char matches.
 * <p>
 * Inspired by wildmat.c
 */
final class Wildcard {

    private Wildcard() {
        throw new AssertionError("Wildcard can not be instantiated");
    }

    static boolean wildcardMatch(String pattern, String value) {
        checkNotNull(pattern, "pattern is null");
        checkNotNull(value, "value is null");

        // empty pattern only matches empty value
        if (pattern.isEmpty()) {
            return value.isEmpty();
        }

        // just wildcard is a quick check
        if (pattern.equals("*")) {
            return true;
        }

        if (value.isEmpty()) {
            return false;
        }

        return doGlobMatch(pattern, value);
    }

    static boolean doGlobMatch(String pattern, String value) {
        int valueIndex = 0;
        int patternIndex = 0;
        int patternLength = pattern.length();
        int valueLength = value.length();

        for (; patternIndex < patternLength; valueIndex++, patternIndex++) {
            char patternChar = pattern.charAt(patternIndex);

            // if the value ends but there is anything but a wildcard left,
            // it is not a match.
            if (valueIndex == valueLength && patternChar != '*') {
                return false;
            }

            switch (patternChar) {
                case '*':
                    // coalesce multiple stars
                    do {
                        // last character
                        if (patternIndex + 1 == patternLength) {
                            return true;
                        }
                        patternIndex++;
                        // coalesce multiple stars
                    } while (pattern.charAt(patternIndex) == '*');

                    for (; valueIndex < valueLength; valueIndex++) {
                        boolean matched = doGlobMatch(value.substring(valueIndex), pattern.substring(patternIndex));
                        if (matched) {
                            return true;
                        }
                    }
                    return false;
                case '?':
                    continue; // for(
                default:
                    if (value.charAt(valueIndex) != patternChar) {
                        return false;
                    }
            }
        }

        return valueIndex == valueLength;
    }
}
