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

package org.basepom.mojo.propertyhelper.definitions;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.IgnoreWarnFail;
import org.basepom.mojo.propertyhelper.groups.PropertyGroup;

import java.util.Arrays;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;

public class PropertyGroupDefinition {

    /**
     * Property group id.
     */
    String id;

    /**
     * Activate the group if the current project version does not contain SNAPSHOT. Field injected by Maven.
     */
    boolean activeOnRelease = true;

    /**
     * Activate the group if the current project version contains SNAPSHOT. Field injected by Maven.
     */
    boolean activeOnSnapshot = true;

    /**
     * Action if this property group defines a duplicate property. Field injected by Maven.
     */
    private IgnoreWarnFail onDuplicateProperty = IgnoreWarnFail.FAIL;

    public void setOnDuplicateProperty(String onDuplicateProperty) {
        this.onDuplicateProperty = IgnoreWarnFail.forString(onDuplicateProperty);
    }

    /**
     * Action if any property from that group could not be defined. Field injected by Maven.
     */
    private IgnoreWarnFail onMissingField = IgnoreWarnFail.FAIL;

    public PropertyGroupDefinition setOnMissingField(String onMissingField) {
        this.onMissingField = IgnoreWarnFail.forString(onMissingField);
        return this;
    }

    /**
     * Property definitions in this group.
     */
    Set<PropertyDefinition> propertyDefinitions = Set.of();

    // called by maven
    public PropertyGroupDefinition setProperties(PropertyDefinition... propertyDefinitions) {
        this.propertyDefinitions = ImmutableSet.copyOf(Arrays.asList(propertyDefinitions));

        this.propertyDefinitions.forEach(PropertyDefinition::check);

        return this;
    }

    public PropertyGroupDefinition() {
    }

    @VisibleForTesting
    PropertyGroupDefinition(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isActiveOnRelease() {
        return activeOnRelease;
    }

    public boolean isActiveOnSnapshot() {
        return activeOnSnapshot;
    }

    public IgnoreWarnFail getOnDuplicateProperty() {
        return onDuplicateProperty;
    }

    public IgnoreWarnFail getOnMissingField() {
        return onMissingField;
    }

    public Set<PropertyDefinition> getPropertyDefinitions() {
        return propertyDefinitions;
    }

    public PropertyGroup createGroup(FieldContext context) {
        return new PropertyGroup(this, context);
    }

    public Set<String> getPropertyNames() {
        return propertyDefinitions.stream()
            .map(PropertyDefinition::getName)
            .collect(toImmutableSet());
    }
}
