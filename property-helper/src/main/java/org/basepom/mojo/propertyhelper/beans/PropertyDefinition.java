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

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

public class PropertyDefinition {

    /**
     * Name. Field injected by maven.
     */
    String name = null;

    /**
     * Value. Field injected by maven.
     */
    String value = null;

    /**
     * Transformers. Field injected by maven.
     */
    String transformers = "";

    public PropertyDefinition() {
    }

    public PropertyDefinition(final String name, final String value) {
        this.name = checkNotNull(name, "name is null");
        this.value = checkNotNull(value, "value is null");
        this.transformers = "";
    }

    public static Function<PropertyDefinition, String> getNameFunction() {
        return propertyDefinition -> {
            checkNotNull(propertyDefinition, "propertyDefinition is null");
            return propertyDefinition.getName();
        };
    }

    public static Function<PropertyDefinition, String> getValueFunction() {
        return propertyDefinition -> {
            checkNotNull(propertyDefinition, "propertyDefinition is null");
            return propertyDefinition.getValue();
        };
    }

    public String getName() {
        checkNotNull(name, "name is null");
        return name;
    }

    public String getValue() {
        checkNotNull(value, "value is null");
        return value;
    }

    public String getTransformers() {
        return transformers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertyDefinition that = (PropertyDefinition) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value) && Objects.equals(transformers,
            that.transformers);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PropertyDefinition.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .add("value='" + value + "'")
            .add("transformers='" + transformers + "'")
            .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, transformers);
    }
}


