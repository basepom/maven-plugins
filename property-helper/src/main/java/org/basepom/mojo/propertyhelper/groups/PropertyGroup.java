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

import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.IgnoreWarnFail;
import org.basepom.mojo.propertyhelper.definitions.PropertyDefinition;
import org.basepom.mojo.propertyhelper.definitions.PropertyGroupDefinition;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class PropertyGroup {

    private final PropertyGroupDefinition propertyGroupDefinition;
    private final FieldContext context;

    public PropertyGroup(PropertyGroupDefinition propertyGroupDefinition, FieldContext context) {
        this.propertyGroupDefinition = propertyGroupDefinition;
        this.context = context;
    }

    public String getId() {
        return propertyGroupDefinition.getId();
    }

    public boolean checkActive(boolean isSnapshot) {
        return (propertyGroupDefinition.isActiveOnRelease() && !isSnapshot)
            || (propertyGroupDefinition.isActiveOnSnapshot() && isSnapshot);
    }

    public IgnoreWarnFail getOnDuplicateProperty() {
        return propertyGroupDefinition.getOnDuplicateProperty();
    }

    public String getPropertyValue(final PropertyDefinition propertyDefinition, final Map<String, String> propElements) {

        return Optional.ofNullable(propertyDefinition.getValue())
            .map(context.getInterpolatorFactory().interpolate(propertyDefinition.getName(), propertyGroupDefinition.getOnMissingField(), propElements))
            .map(context.getTransformerRegistry().applyTransformers(propertyDefinition.getTransformers()))
            .orElse("");
    }

    public Set<PropertyResult> createProperties(final Map<String, String> values) {
        return propertyGroupDefinition.getPropertyDefinitions().stream()
            .map(propertyDefinition -> new PropertyResult(propertyDefinition.getName(),
                getPropertyValue(propertyDefinition, values)))
            .collect(ImmutableSet.toImmutableSet());
    }
}
