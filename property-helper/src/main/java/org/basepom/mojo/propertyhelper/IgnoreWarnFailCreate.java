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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Locale;
import java.util.function.Supplier;

import com.google.common.flogger.FluentLogger;

public enum IgnoreWarnFailCreate {
    IGNORE, WARN, FAIL, CREATE;

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    public static IgnoreWarnFailCreate forString(final String value) {
        checkArgument(value != null, "the value can not be null");
        return Enum.valueOf(IgnoreWarnFailCreate.class, value.toUpperCase(Locale.getDefault()));
    }

    public static boolean checkIgnoreWarnFailCreateState(final boolean check, final IgnoreWarnFailCreate iwfc,
        final Supplier<String> checkMessage, final Supplier<String> errorMessage) {

        if (check) {
            LOG.atFine().log(checkMessage.get());
            return false;
        }

        switch (iwfc) {
            case IGNORE:
                return false;
            case WARN:
                LOG.atWarning().log(errorMessage.get());
                return false;
            case FAIL:
                throw new IllegalStateException(errorMessage.get());
            case CREATE:
                LOG.atFine().log(errorMessage.get());
                return true;
            default:
                throw new IllegalStateException("Unknown state: " + iwfc);
        }
    }
}
