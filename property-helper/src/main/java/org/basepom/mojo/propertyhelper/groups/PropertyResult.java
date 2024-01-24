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

package org.basepom.mojo.propertyhelper.groups;

import static com.google.common.base.Preconditions.checkNotNull;

public class PropertyResult {

    private final String propertyName;
    private final String propertyValue;

    PropertyResult(final String propertyName, final String propertyValue) {
        this.propertyName = checkNotNull(propertyName, "propertyName is null");
        this.propertyValue = checkNotNull(propertyValue, "propertyValue is null");
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
}
