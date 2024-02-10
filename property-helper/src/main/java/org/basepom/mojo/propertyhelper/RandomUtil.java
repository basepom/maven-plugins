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

import java.security.SecureRandom;
import java.util.Random;

import com.google.common.base.Strings;

public final class RandomUtil {

    private RandomUtil() {
        throw new AssertionError("RandomUtil can not be instantiated");
    }

    public static Random createRandomFromSeed(String seedValue) {
        String seedString = Strings.nullToEmpty(seedValue);

        if (!seedString.isEmpty()) {
            long value = 0L;
            for (char c : seedString.toCharArray()) {
                value ^= c;
                value <<= 4L;
            }
            return new Random(value);
        } else {
            return new SecureRandom();
        }
    }
}
