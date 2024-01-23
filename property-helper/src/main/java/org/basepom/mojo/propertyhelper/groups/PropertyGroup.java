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
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.lang.String.format;

import org.basepom.mojo.propertyhelper.IgnoreWarnFail;
import org.basepom.mojo.propertyhelper.InterpolatorFactory;
import org.basepom.mojo.propertyhelper.TransformerRegistry;
import org.basepom.mojo.propertyhelper.definitions.PropertyDefinition;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.interpolation.InterpolationException;

public class PropertyGroup {

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
    private IgnoreWarnFail onMissingProperty = IgnoreWarnFail.FAIL;

    public PropertyGroup setOnMissingProperty(String onMissingProperty) {
        this.onMissingProperty = IgnoreWarnFail.forString(onMissingProperty);
        return this;
    }

    /**
     * Property definitions in this group.
     */
    Set<PropertyDefinition> propertyDefinitions = Set.of();

    // called by maven
    public PropertyGroup setProperties(PropertyDefinition... propertyDefinitions) {
        this.propertyDefinitions = ImmutableSet.copyOf(Arrays.asList(propertyDefinitions));

        this.propertyDefinitions.forEach(PropertyDefinition::check);

        return this;
    }

    public PropertyGroup() {
    }

    @VisibleForTesting
    PropertyGroup(String id) {
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

    public IgnoreWarnFail getOnMissingProperty() {
        return onMissingProperty;
    }

    public Map<String, String> getProperties() {
        return propertyDefinitions.stream()
            .collect(toImmutableMap(PropertyDefinition::getName, PropertyDefinition::getValue));
    }

    @VisibleForTesting
    PropertyGroup setProperties(final Map<String, String> properties) {
        this.propertyDefinitions = properties.entrySet()
            .stream()
            .map(e -> new PropertyDefinition(e.getKey(), e.getValue()))
            .collect(toImmutableSet());
        return this;
    }

    public Set<String> getPropertyNames() {
        return propertyDefinitions.stream()
            .map(PropertyDefinition::getName)
            .collect(toImmutableSet());
    }

    public String getPropertyValue(final InterpolatorFactory interpolatorFactory, final String propertyName, final Map<String, String> propElements)
        throws IOException, InterpolationException {

        ImmutableMap<String, PropertyDefinition> definitionMap = Maps.uniqueIndex(propertyDefinitions, PropertyDefinition::getName);
        final PropertyDefinition propertyDefinition = definitionMap.get(propertyName);
        checkNotNull(propertyDefinition, "property definition '%s' does not exist!", propertyName);

        return Optional.ofNullable(propertyDefinition.getValue())
            .map(interpolatorFactory.interpolate(propertyDefinition.getName(), onMissingProperty, propElements))
            .map(TransformerRegistry.INSTANCE.applyTransformers(propertyDefinition.getTransformers()))
            .orElse("");
    }

    public List<PropertyField> createFields(final Map<String, String> values, InterpolatorFactory interpolatorFactory)
        throws MojoExecutionException, IOException {
        checkNotNull(values, "values is null");

        final ImmutableList.Builder<PropertyField> result = ImmutableList.builder();
        final Map<String, String> properties = getProperties();

        for (String name : properties.keySet()) {
            try {
                final String value = getPropertyValue(interpolatorFactory, name, values);
                result.add(new PropertyField(name, value));
            } catch (InterpolationException e) {
                throw new MojoExecutionException(format("Could not interpolate '%s", name), e);
            }
        }

        return result.build();
    }
}
