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
import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.definitions.MacroDefinition;
import org.basepom.mojo.propertyhelper.macros.MacroType;

import java.util.Optional;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.MojoExecutionException;

public final class MacroField extends Field<String, MacroDefinition> {

    private final ValueProvider valueProvider;
    private final FieldContext context;

    @VisibleForTesting
    public static MacroField forTesting(MacroDefinition dateDefinition, ValueProvider valueProvider, FieldContext fieldContext) {
        return new MacroField(dateDefinition, valueProvider, fieldContext);
    }

    public MacroField(final MacroDefinition macroDefinition, final ValueProvider valueProvider, final FieldContext context) {
        super(macroDefinition, context);

        this.valueProvider = valueProvider;
        this.context = context;
    }

    @Override
    public String getFieldName() {
        return fieldDefinition.getId();
    }

    @Override
    public String getValue() throws MojoExecutionException {
        final Optional<String> type = fieldDefinition.getMacroType();
        final MacroType macroType;

        try {
            if (type.isPresent()) {
                macroType = context.getMacros().get(type.get());
                checkState(macroType != null, "Could not locate macro '%s'", type.get());
            } else {
                final Optional<String> macroClassName = fieldDefinition.getMacroClass();
                checkState(macroClassName.isPresent(), "No definition for macro '%s' found!", fieldDefinition.getId());
                final Class<? extends MacroType> macroClass = (Class<? extends MacroType>) Class.forName(macroClassName.get());
                macroType = macroClass.getDeclaredConstructor().newInstance();
            }

            return formatResult(macroType.getValue(fieldDefinition, valueProvider, context).orElse(null));

        } catch (ReflectiveOperationException e) {
            throw new MojoExecutionException(format("Could not instantiate '%s'", fieldDefinition), e);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MacroField.class.getSimpleName() + "[", "]")
            .add("valueProvider=" + valueProvider)
            .add("context=" + context)
            .toString();
    }
}
