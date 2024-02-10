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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class RandomUtilTest {

    @Test
    public void testCreateRandomFromSeedString() {
        String seedString = UUID.randomUUID().toString();

        var secureRandom1 = RandomUtil.createRandomFromSeed(seedString);
        var secureRandom2 = RandomUtil.createRandomFromSeed(seedString);

        assertNotNull(secureRandom1);
        assertNotNull(secureRandom2);

        //Verify that two different SecureRandom instances are created for each call with null parameter
        assertNotSame(secureRandom1, secureRandom2);

        long value1 = secureRandom1.nextLong();
        long value2 = secureRandom2.nextLong();
        assertThat(value1).isEqualTo(value2);
    }

    /**
     * Tests `createRandomFromSeedString` method of `RandomUtil` class.
     * When given a null, this method also generates a SecureRandom instance.
     * This method specifically validates the nonnullness and the uniqueness of the returned SecureRandom instance.
     */
    @Test
    public void testCreateRandomFromNull() {
        var secureRandom1 = RandomUtil.createRandomFromSeed(null);
        var secureRandom2 = RandomUtil.createRandomFromSeed(null);

        assertNotNull(secureRandom1);
        assertNotNull(secureRandom2);

        //Verify that two different SecureRandom instances are created for each call with null parameter
        assertNotSame(secureRandom1, secureRandom2);

        assertThat(secureRandom1.nextLong()).isNotEqualTo(secureRandom2.nextLong());
    }
}
