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

import java.util.List;
import java.util.Objects;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

public class StringDefinition
        extends AbstractDefinition<StringDefinition> {

    /**
     * Values for this string. Field injected by Maven.
     */
    List<String> values = ImmutableList.of();

    /**
     * Whether a blank string is a valid value. Field injected by Maven.
     */
    boolean blankIsValid = true;

    /**
     * Default action on missing value. Field injected by Maven.
     */
    IgnoreWarnFail onMissingValue = IgnoreWarnFail.FAIL;

    public List<String> getValues() {
        return values;
    }

    @VisibleForTesting
    public StringDefinition setValues(final List<String> values) {
        checkNotNull(values, "values is null");
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (String value : values) {
            builder.add(Objects.requireNonNullElse(value, ""));
        }
        this.values = builder.build();
        return this;
    }

    public boolean isBlankIsValid() {
        return blankIsValid;
    }

    @VisibleForTesting
    public StringDefinition setBlankIsValid(final boolean blankIsValid) {
        this.blankIsValid = blankIsValid;
        return this;
    }

    public IgnoreWarnFail getOnMissingValue() {
        return onMissingValue;
    }

    @VisibleForTesting
    public StringDefinition setOnMissingValue(final String onMissingValue) {
        checkNotNull(onMissingValue, "onMissingValue is null");
        this.onMissingValue = IgnoreWarnFail.forString(onMissingValue);
        return this;
    }
}
