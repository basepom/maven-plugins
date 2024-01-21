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
import static com.google.common.base.Preconditions.checkState;

import org.basepom.mojo.propertyhelper.MacroField;
import org.basepom.mojo.propertyhelper.PropertyElement;
import org.basepom.mojo.propertyhelper.PropertyElementContext;
import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class MacroDefinition
    extends AbstractDefinition<MacroDefinition> {

    /**
     * Macro type. Field injected by Maven.
     */
    String macroType = null;

    /**
     * Class for this macro. Field injected by Maven.
     */
    String macroClass = null;

    /**
     * Macro specific properties. Field injected by Maven.
     */
    Properties properties = new Properties();

    public MacroDefinition() {
        super();
    }

    public Optional<String> getMacroType() {
        return Optional.ofNullable(macroType);
    }

    @VisibleForTesting
    public MacroDefinition setMacroType(final String macroType) {
        this.macroType = checkNotNull(macroType, "macroType is null");
        return this;
    }

    public Optional<String> getMacroClass() {
        return Optional.ofNullable(macroClass);
    }

    @VisibleForTesting
    public MacroDefinition setMacroClass(final String macroClass) {
        this.macroClass = checkNotNull(macroClass, "macroClass is null");
        return this;
    }

    public Map<String, String> getProperties() {
        return ImmutableMap.copyOf(Maps.fromProperties(properties));
    }

    @VisibleForTesting
    public MacroDefinition setProperties(final Properties properties) {
        this.properties = new Properties(checkNotNull(properties, "properties is null"));
        return this;
    }

    @Override
    public void check() {
        super.check();

        checkState(macroClass != null || macroType != null, "neither macro class nor macro type is defined!");
    }

    @Override
    public PropertyElement createPropertyElement(PropertyElementContext context, ValueCache valueCache) throws IOException {
        checkNotNull(context, "context is null");
        checkNotNull(valueCache, "valueCache is null");

        check();

        final ValueProvider macroValue = valueCache.getValueProvider(this);
        return new MacroField(this, macroValue, context);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MacroDefinition.class.getSimpleName() + "[", "]")
            .add("macroType='" + macroType + "'")
            .add("macroClass='" + macroClass + "'")
            .add("properties=" + properties)
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
        MacroDefinition that = (MacroDefinition) o;
        return Objects.equals(macroType, that.macroType) && Objects.equals(macroClass, that.macroClass) && Objects.equals(properties,
            that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), macroType, macroClass, properties);
    }
}
