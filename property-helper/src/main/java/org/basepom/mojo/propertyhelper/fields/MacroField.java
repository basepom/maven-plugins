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

package org.basepom.mojo.propertyhelper.fields;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import org.basepom.mojo.propertyhelper.Field;
import org.basepom.mojo.propertyhelper.PropertyElementContext;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.definitions.MacroDefinition;
import org.basepom.mojo.propertyhelper.macros.MacroType;

import java.util.Optional;

import org.apache.maven.plugin.MojoExecutionException;

public class MacroField
    implements Field {

    private final MacroDefinition macroDefinition;
    private final ValueProvider valueProvider;
    private final PropertyElementContext context;

    public MacroField(final MacroDefinition macroDefinition,
        final ValueProvider valueProvider,
        final PropertyElementContext context) {
        this.macroDefinition = macroDefinition;
        this.valueProvider = valueProvider;
        this.context = context;
    }

    @Override
    public String getFieldName() {
        return macroDefinition.getId();
    }

    @Override
    public String getValue() throws MojoExecutionException {
        final Optional<String> type = macroDefinition.getMacroType();
        final MacroType macroType;

        try {
            if (type.isPresent()) {
                macroType = context.getMacros().get(type.get());
                checkState(macroType != null, "Could not locate macro '%s'", type.get());
            } else {
                final Optional<String> macroClassName = macroDefinition.getMacroClass();
                checkState(macroClassName.isPresent(), "No definition for macro '%s' found!", macroDefinition.getId());
                final Class<? extends MacroType> macroClass = (Class<? extends MacroType>) Class.forName(macroClassName.get());
                macroType = macroClass.getDeclaredConstructor().newInstance();
            }

            return macroType.getValue(macroDefinition, valueProvider, context)
                .map(macroDefinition::formatResult)
                .orElse("");

        } catch (ReflectiveOperationException e) {
            throw new MojoExecutionException(format("Could not instantiate '%s'", macroDefinition), e);
        }
    }

    @Override
    public boolean isExposeAsProperty() {
        return macroDefinition.isExport();
    }

    @Override
    public String toString() {
        try {
            return getValue();
        } catch (Exception e) {
            return "<unset>";
        }
    }
}
