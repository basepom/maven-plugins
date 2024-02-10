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

import java.io.File;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

/**
 * All accessible values from the Maven build system for a Field.
 */
public interface FieldContext {

    /** Returns a fixed instance for testing. Do not use outside test code. */
    static FieldContext forTesting() {
        return forTesting(new SecureRandom());
    }

    /** Returns a fixed instance for testing. The Random instance can be set to simulate reproducible builds. */
    static FieldContext forTesting(Random random) {
        return new FieldContext() {

            @Override
            public Map<String, MacroType> getMacros() {
                return Collections.emptyMap();
            }

            @Override
            public Properties getProjectProperties() {
                return new Properties();
            }

            @Override
            public MavenProject getProject() {
                return new MavenProject();
            }

            @Override
            public Settings getSettings() {
                return new Settings();
            }

            @Override
            public File getBasedir() {
                throw new UnsupportedOperationException();
            }

            @Override
            public InterpolatorFactory getInterpolatorFactory() {
                return InterpolatorFactory.forTesting();
            }

            @Override
            public TransformerRegistry getTransformerRegistry() {
                return TransformerRegistry.INSTANCE;
            }

            @Override
            public Random getRandom() {
                return random;
            }
        };
    }

    /**
     * Returns a map with all known macros. Key value is the macro hint as given by the plexus component annotation.
     * @return A map with all known macros.
     */
    Map<String, MacroType> getMacros();

    /**
     * Return the maven project properties.
     * @return A properties object for the project properties.
     */
    Properties getProjectProperties();

    /**
     * Returns a reference to the {@link MavenProject}.
     * @return A {@link MavenProject} object
     */
    MavenProject getProject();

    /**
     * Returns the current maven {@link Settings} object
     * @return A {@link Settings} object
     */
    Settings getSettings();

    /**
     * Returns the base dir for this maven build execution.
     *
     * @return A {@link File} object.
     */
    File getBasedir();

    /**
     * Returns the {@link InterpolatorFactory} that can interpolate "late resolution" properties.
     * @return An {@link InterpolatorFactory} reference.
     */
    InterpolatorFactory getInterpolatorFactory();

    /**
     * Returns a reference to the {@link TransformerRegistry}.
     * @return A {@link TransformerRegistry} object.
     */
    TransformerRegistry getTransformerRegistry();

    /**
     * Returns a {@link java.security.SecureRandom} instance for generating random values.
     */
    Random getRandom();
}
