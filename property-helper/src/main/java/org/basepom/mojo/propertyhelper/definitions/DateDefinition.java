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

package org.basepom.mojo.propertyhelper.definitions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.basepom.mojo.propertyhelper.Field;
import org.basepom.mojo.propertyhelper.PropertyElementContext;
import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.fields.DateField;

import java.io.IOException;
import java.util.Optional;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;

public class DateDefinition extends FieldDefinition {

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

    @VisibleForTesting
    DateDefinition(String id) {
        super(id);
    }

    public Optional<String> getTimezone() {
        return Optional.ofNullable(timezone);
    }

    public Optional<Long> getValue() {
        return Optional.ofNullable(value);
    }

    @Override
    public Field createPropertyElement(PropertyElementContext context, ValueCache valueCache) throws IOException {
        checkNotNull(context, "context is null");
        checkNotNull(valueCache, "valueCache is null");

        check();

        final ValueProvider dateValue = valueCache.getValueProvider(this);
        return new DateField(this, dateValue);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DateDefinition.class.getSimpleName() + "[", "]")
            .add("timezone='" + timezone + "'")
            .add("value=" + value)
            .add(super.toString())
            .toString();
    }
}
