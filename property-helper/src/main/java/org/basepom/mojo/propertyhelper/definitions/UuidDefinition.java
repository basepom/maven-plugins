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
import org.basepom.mojo.propertyhelper.fields.UuidField;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

import com.google.common.annotations.VisibleForTesting;

public class UuidDefinition extends FieldDefinition {

    public UuidDefinition() {}

    @VisibleForTesting
    UuidDefinition(String id) {
        super(id);
    }

    /**
     * Value for this uuid. Field injected by Maven.
     */
    String value = null;

    public Optional<UUID> getValue() {
        return Optional.ofNullable(value).map(UUID::fromString);
    }

    @Override
    public Field createPropertyElement(PropertyElementContext context, ValueCache valueCache) throws IOException {
        checkNotNull(context, "context is null");
        checkNotNull(valueCache, "valueCache is null");

        check();

        final ValueProvider uuidValue = valueCache.getValueProvider(this);
        return new UuidField(this, uuidValue);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UuidDefinition.class.getSimpleName() + "[", "]")
            .add("value='" + value + "'")
            .add(super.toString())
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UuidDefinition that = (UuidDefinition) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
