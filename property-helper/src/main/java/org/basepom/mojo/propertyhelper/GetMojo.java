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

import com.google.common.flogger.FluentLogger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Fetches the defined numbers and add properties.
 */
@Mojo(name = "get", threadSafe = true)
public final class GetMojo extends AbstractPropertyHelperMojo {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    /**
     * If set to true, all fields that have a {@code <propertyFile>} and a {@code <propertyNameInFile>} configuration
     * parameter are persisted to disk.
     */
    @Parameter(defaultValue = "false")
    boolean persist = false;

    @Override
    protected void doExecute() throws MojoExecutionException, IOException {
        LOG.atFine().log("Running property-helper:get");

        createFieldDefinitions();
        createFields();
        createGroups();

        if (persist) {
            LOG.atFine().log("Persisting value cache");
            // Now dump the value cache back to the files if necessary.
            valueCache.persist();
        }
    }
}
