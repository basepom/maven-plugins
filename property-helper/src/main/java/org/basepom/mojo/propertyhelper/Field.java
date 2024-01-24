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


import org.basepom.mojo.propertyhelper.definitions.FieldDefinition;

import java.util.Map;
import java.util.Optional;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Describe all the fields.
 */
public abstract class Field<T, U extends FieldDefinition<T>> {

    protected final U fieldDefinition;
    private final InterpolatorFactory interpolatorFactory;
    private final TransformerRegistry transformerRegistry;


    protected Field(U fieldDefinition, FieldContext context) {
        this.fieldDefinition = fieldDefinition;
        this.interpolatorFactory = context.getInterpolatorFactory();
        this.transformerRegistry = context.getTransformerRegistry();
    }

    /**
     * The name of the field.
     */
    public abstract String getFieldName();

    /**
     * The value of the field. {@link Optional#empty()} can be returned if the value is not defined.
     */
    public abstract String getValue() throws MojoExecutionException;

    protected String formatResult(T value) {

        return Optional.ofNullable(value)
            .map(fieldDefinition.formatResult())
            .map(interpolatorFactory.interpolate(getFieldName(), IgnoreWarnFail.FAIL, Map.of()))
            .map(transformerRegistry.applyTransformers(fieldDefinition.getTransformers()))
            .orElse("");
    }

    /**
     * True if the value of this element should be exposed as a maven property.
     */
    public boolean isExposeAsProperty() {
        return fieldDefinition.isExport();
    }
}
