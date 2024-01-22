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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;
import java.util.function.Supplier;

import com.google.common.flogger.FluentLogger;

public enum IgnoreWarnFail {
    IGNORE, WARN, FAIL;

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    public static IgnoreWarnFail forString(final String value) {
        checkNotNull(value, "the value can not be null");
        return Enum.valueOf(IgnoreWarnFail.class, value.toUpperCase(Locale.getDefault()));
    }

    /**
     * Ensure that a given element exists. If it does not exist, react based on the {@link IgnoreWarnFail} attribute:
     * <ul>
     *     <li>IGNORE - do nothing</li>
     *     <li>WARN - warn that an element does not exist</li>
     *     <li>FAIL - throw an exception</li>
     * </ul>
     *
     * @param check Should be true
     * @param iwf    What to do
     * @return True if the thing exists, false otherwise
     */
    public static boolean checkIgnoreWarnFailState(final boolean check, final IgnoreWarnFail iwf,
        final Supplier<String> checkMessage, final Supplier<String> errorMessage) {

        if (check) {
            LOG.atFine().log(checkMessage.get());
            return true;
        }

        switch (iwf) {
            case IGNORE:
                LOG.atFine().log(errorMessage.get());
                break;
            case WARN:
                LOG.atWarning().log(errorMessage.get());
                break;
            case FAIL:
                throw new IllegalStateException(errorMessage.get());
            default:
                throw new IllegalStateException("Unknown state: " + iwf);
        }

        return false;
    }
}
