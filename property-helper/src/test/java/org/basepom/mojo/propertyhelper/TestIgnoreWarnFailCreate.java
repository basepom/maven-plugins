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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.basepom.mojo.propertyhelper.IgnoreWarnFailCreate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestIgnoreWarnFailCreate {

    @Test
    public void testValid() {
        IgnoreWarnFailCreate value = IgnoreWarnFailCreate.forString("fail");
        assertSame(IgnoreWarnFailCreate.FAIL, value);
        value = IgnoreWarnFailCreate.forString("warn");
        assertSame(IgnoreWarnFailCreate.WARN, value);
        value = IgnoreWarnFailCreate.forString("ignore");
        assertSame(IgnoreWarnFailCreate.IGNORE, value);
        value = IgnoreWarnFailCreate.forString("create");
        assertSame(IgnoreWarnFailCreate.CREATE, value);
    }

    @Test
    public void testValidCases() {
        IgnoreWarnFailCreate value = IgnoreWarnFailCreate.forString("fail");
        assertSame(IgnoreWarnFailCreate.FAIL, value);
        value = IgnoreWarnFailCreate.forString("FAIL");
        assertSame(IgnoreWarnFailCreate.FAIL, value);
        value = IgnoreWarnFailCreate.forString("Fail");
        assertSame(IgnoreWarnFailCreate.FAIL, value);
        value = IgnoreWarnFailCreate.forString("FaIl");
        assertSame(IgnoreWarnFailCreate.FAIL, value);
    }

    @Test
    public void testBadValue() {
        assertThrows(IllegalArgumentException.class, () -> IgnoreWarnFailCreate.forString("foobar"));
    }

    @Test
    public void testNullValue() {
        assertThrows(IllegalArgumentException.class, () -> IgnoreWarnFailCreate.forString(null));
    }

    @Test
    public void testCheckState() {
        boolean value = IgnoreWarnFailCreate.checkState(IgnoreWarnFailCreate.FAIL, true, "");
        assertFalse(value);
        value = IgnoreWarnFailCreate.checkState(IgnoreWarnFailCreate.IGNORE, false, "");
        assertFalse(value);
        value = IgnoreWarnFailCreate.checkState(IgnoreWarnFailCreate.WARN, false, "");
        assertFalse(value);
        value = IgnoreWarnFailCreate.checkState(IgnoreWarnFailCreate.CREATE, false, "");
        Assertions.assertTrue(value);
    }

    @Test
    public void testCheckStateFail() {
        assertThrows(IllegalStateException.class, () -> IgnoreWarnFailCreate.checkState(IgnoreWarnFailCreate.FAIL, false, ""));
    }
}
