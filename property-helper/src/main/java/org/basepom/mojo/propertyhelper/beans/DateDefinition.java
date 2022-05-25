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

package org.basepom.mojo.propertyhelper.beans;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;

public class DateDefinition
        extends AbstractDefinition<DateDefinition> {

    /**
     * Timezone for this date. Field injected by Maven.
     */
    String timezone = null;

    /**
     * Value for this date. Field injected by Maven.
     */
    Long value = null;

    public DateDefinition() {
    }

    public Optional<String> getTimezone() {
        return Optional.ofNullable(timezone);
    }

    @VisibleForTesting
    public DateDefinition setTimezone(final String timezone) {
        this.timezone = checkNotNull(timezone, "timezone is null");
        return this;
    }

    public Optional<Long> getValue() {
        return Optional.ofNullable(value);
    }

    @VisibleForTesting
    public DateDefinition setValue(final Long value) {
        this.value = checkNotNull(value, "value is null");
        return this;
    }
}
