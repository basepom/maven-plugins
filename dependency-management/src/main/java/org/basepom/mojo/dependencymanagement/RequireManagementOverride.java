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

package org.basepom.mojo.dependencymanagement;

import java.util.ArrayList;
import java.util.List;

public class RequireManagementOverride {

    private List<String> patterns = new ArrayList<>();
    private Boolean dependencies = null;
    private Boolean plugins = null;
    private Boolean allowVersions = null;
    private Boolean allowExclusions = null;

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public void setDependencies(boolean dependencies) {
        this.dependencies = dependencies;
    }

    public void setPlugins(boolean plugins) {
        this.plugins = plugins;
    }

    public void setAllowVersions(boolean allowVersions) {
        this.allowVersions = allowVersions;
    }

    public void setAllowExclusions(boolean allowExclusions) {
        this.allowExclusions = allowExclusions;
    }

    public RequireManagementConfig toRequireManagementConfig(final RequireManagement requireManagement) {
        return new RequireManagementConfig() {
            @Override
            public boolean requireDependencyManagement() {
                return fallbackIfNull(dependencies, requireManagement.requireDependencyManagement());
            }

            @Override
            public boolean requirePluginManagement() {
                return fallbackIfNull(plugins, requireManagement.requirePluginManagement());
            }

            @Override
            public boolean allowVersions() {
                return fallbackIfNull(allowVersions, requireManagement.allowVersions());
            }

            @Override
            public boolean allowExclusions() {
                return fallbackIfNull(allowExclusions, requireManagement.allowExclusions());
            }

            @Override
            public String unmanagedDependencyMessage() {
                return requireManagement.unmanagedDependencyMessage();
            }

            @Override
            public String dependencyVersionMismatchMessage() {
                return requireManagement.dependencyVersionMismatchMessage();
            }

            @Override
            public String unmanagedPluginMessage() {
                return requireManagement.unmanagedPluginMessage();
            }

            @Override
            public String pluginVersionMismatchMessage() {
                return requireManagement.pluginVersionMismatchMessage();
            }

            @Override
            public String dependencyExclusionsMessage() {
                return requireManagement.dependencyExclusionsMessage();
            }

            @Override
            public String dependencyVersionDisallowedMessage() {
                return requireManagement.dependencyVersionDisallowedMessage();
            }
        };
    }

    private static boolean fallbackIfNull(Boolean b1, boolean b2) {
        return b1 != null ? b1 : b2;
    }
}
