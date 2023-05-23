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
import static java.lang.String.format;

import org.basepom.mojo.propertyhelper.beans.PropertyGroup;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.interpolation.InterpolationException;

public class PropertyField
        implements PropertyElement {

    private final String propertyName;
    private final String propertyValue;

    PropertyField(final String propertyName, final String propertyValue) {
        this.propertyName = checkNotNull(propertyName, "propertyName is null");
        this.propertyValue = checkNotNull(propertyValue, "propertyValue is null");
    }

    public static List<PropertyElement> createProperties(final Model model, final Map<String, String> values, final PropertyGroup propertyGroup)
            throws MojoExecutionException, IOException {
        checkNotNull(model, "model is null");
        checkNotNull(values, "values is null");
        checkNotNull(propertyGroup, "propertyGroup is null");

        final InterpolatorFactory interpolatorFactory = new InterpolatorFactory(model);

        final Builder<PropertyElement> result = ImmutableList.builder();
        final Map<String, String> properties = propertyGroup.getProperties();

        for (String name : properties.keySet()) {
            try {
                final String value = propertyGroup.getPropertyValue(interpolatorFactory, name, values);
                result.add(new PropertyField(name, value));
            } catch (InterpolationException e) {
                throw new MojoExecutionException(format("Could not interpolate '%s", model), e);
            }
        }
        return result.build();
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public Optional<String> getPropertyValue() {
        return Optional.of(propertyValue);
    }

    @Override
    public boolean isExport() {
        return true;
    }
}
