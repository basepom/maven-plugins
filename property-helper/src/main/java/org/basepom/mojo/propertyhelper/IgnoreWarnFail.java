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
import static java.lang.String.format;

import java.util.Locale;

import com.google.common.flogger.FluentLogger;

public enum IgnoreWarnFail {
    IGNORE, WARN, FAIL;

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    public static IgnoreWarnFail forString(final String value) {
        checkNotNull(value, "the value can not be null");
        return Enum.valueOf(IgnoreWarnFail.class, value.toUpperCase(Locale.getDefault()));
    }

    /**
     * Reacts on a given thing existing or not existing.
     * <p>
     * IGNORE: Do nothing. WARN: Display a warning message if the thing does not exist, otherwise do nothing. FAIL: Throws an exception if the thing does not
     * exist.
     * <p>
     * Returns true if the thing should be create, false otherwise.
     */
    public static void checkState(final IgnoreWarnFail iwf, final boolean exists, final String thing) {
        if (exists) {
            return;
        }

        switch (iwf) {
            case IGNORE:
                return;
            case WARN:
                LOG.atWarning().log("'%s' does not exist!", thing);
                break;
            case FAIL:
                throw new IllegalStateException(format("'%s' does not exist!", thing));
            default:
                throw new IllegalStateException("Unknown state: " + iwf);
        }
    }
}
