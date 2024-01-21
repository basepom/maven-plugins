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

import org.basepom.mojo.propertyhelper.macros.MacroType;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public interface PropertyElementContext {
    PropertyElementContext EMPTY_CONTEXT = new PropertyElementContext() {};

    default Map<String, MacroType> getMacros() {
        return Collections.emptyMap();
    }

    default Properties getProperties() {
        return new Properties();
    }
}