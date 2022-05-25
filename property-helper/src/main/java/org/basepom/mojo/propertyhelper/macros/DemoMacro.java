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

package org.basepom.mojo.propertyhelper.macros;

import static com.google.common.base.Preconditions.checkNotNull;

import org.basepom.mojo.propertyhelper.AbstractPropertyHelperMojo;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.beans.MacroDefinition;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.codehaus.plexus.component.annotations.Component;

@Component(role = MacroType.class, hint = "demo")
public class DemoMacro
        implements MacroType {

    @Override
    public Optional<String> getValue(@Nonnull final MacroDefinition macroDefinition,
            @Nonnull final ValueProvider valueProvider,
            @Nonnull final AbstractPropertyHelperMojo mojo) {
        checkNotNull(mojo, "mojo is null");
        checkNotNull(valueProvider, "valueProvider is null");

        final String type = Objects.requireNonNullElse(macroDefinition.getProperties().get("type"), "static");
        if ("static".equals(type)) {
            return Optional.of("static-value");
        } else if ("property".equals(type)) {
            return valueProvider.getValue();
        } else {
            return Optional.ofNullable(macroDefinition.getProperties().get("value"));
        }
    }
}
