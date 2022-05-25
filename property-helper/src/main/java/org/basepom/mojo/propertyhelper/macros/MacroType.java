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

import org.basepom.mojo.propertyhelper.AbstractPropertyHelperMojo;
import org.basepom.mojo.propertyhelper.ValueProvider;
import org.basepom.mojo.propertyhelper.beans.MacroDefinition;

import java.util.Optional;

public interface MacroType {

    String ROLE = MacroType.class.getName();

    Optional<String> getValue(MacroDefinition macroDefinition,
            ValueProvider valueProvider,
            AbstractPropertyHelperMojo mojo)
            throws Exception;
}
