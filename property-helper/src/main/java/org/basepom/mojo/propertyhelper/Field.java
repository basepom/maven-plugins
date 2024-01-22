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


import java.io.IOException;
import java.util.Optional;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Describe all the fields.
 */
public interface Field {

    /**
     * The name of the field.
     */
    String getFieldName();

    /**
     * The value of the field. {@link Optional#empty()} can be returned if the value is not defined.
     */
    String getValue() throws MojoExecutionException, IOException;

    /**
     * True if the value of this element should be exposed as a maven property.
     */
    boolean isExposeAsProperty();
}
