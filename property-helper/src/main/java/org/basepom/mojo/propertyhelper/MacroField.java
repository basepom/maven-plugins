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
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import org.basepom.mojo.propertyhelper.beans.MacroDefinition;
import org.basepom.mojo.propertyhelper.macros.MacroType;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.apache.maven.plugin.MojoExecutionException;

public class MacroField
        implements PropertyElement {

    private final MacroDefinition macroDefinition;
    private final ValueProvider valueProvider;
    private final AbstractPropertyHelperMojo mojo;

    public MacroField(final MacroDefinition macroDefinition,
            final ValueProvider valueProvider,
            final AbstractPropertyHelperMojo mojo) {
        this.macroDefinition = macroDefinition;
        this.valueProvider = valueProvider;
        this.mojo = mojo;
    }

    public static List<MacroField> createMacros(final ValueCache valueCache,
            final MacroDefinition[] macroDefinitions,
            final AbstractPropertyHelperMojo mojo)
            throws IOException {
        checkNotNull(valueCache, "valueCache is null");
        checkNotNull(macroDefinitions, "macroDefinitions is null");
        checkNotNull(mojo, "mojo is null");

        final Builder<MacroField> result = ImmutableList.builder();

        for (MacroDefinition macroDefinition : macroDefinitions) {
            macroDefinition.check();
            final ValueProvider macroValue = valueCache.getValueProvider(macroDefinition);
            final MacroField macroField = new MacroField(macroDefinition, macroValue, mojo);
            result.add(macroField);
        }

        return result.build();
    }

    @Override
    public String getPropertyName() {
        return macroDefinition.getId();
    }

    @Override
    public Optional<String> getPropertyValue() throws MojoExecutionException {
        final Optional<String> type = macroDefinition.getMacroType();
        final MacroType macroType;

        try {
            if (type.isPresent()) {
                macroType = mojo.getMacros().get(type.get());
                checkState(macroType != null, "Could not locate macro '%s'", type.get());
            } else {
                final Optional<String> macroClassName = macroDefinition.getMacroClass();
                checkState(macroClassName.isPresent(), "No definition for macro '%s' found!", macroDefinition.getId());
                final Class<?> macroClass = Class.forName(macroClassName.get());
                macroType = (MacroType) macroClass.getDeclaredConstructor().newInstance();
            }

            Optional<String> result = macroType.getValue(macroDefinition, valueProvider, mojo);
            if (result.isPresent()) {
                return macroDefinition.formatResult(result.get());
            }
            return result;
        } catch (ReflectiveOperationException e) {
            throw new MojoExecutionException(format("Could not instantiate '%s'", macroDefinition), e);
        }
    }

    @Override
    public boolean isExport() {
        return macroDefinition.isExport();
    }

    @Override
    public String toString() {
        try {
            return getPropertyValue().orElse("");
        } catch (Exception e) {
            return "<unset>";
        }
    }
}
