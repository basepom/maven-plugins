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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.SelectorUtils;

public class DependencyManagementAnalyzer {

    private final MavenProject project;
    private final RequireManagement requireManagement;
    private final Consumer<String> violationLogger;
    private boolean dependencyVersionMismatchError = false;
    private boolean unmanagedDependencyError = false;
    private boolean unmanagedPluginError = false;
    private boolean pluginVersionMismatchError = false;
    private boolean dependencyExclusionsError = false;
    private boolean dependencyVersionDisallowedError = false;
    private final List<String> errorMessages = new ArrayList<>();

    public DependencyManagementAnalyzer(MavenProject project, RequireManagement requireManagement, Consumer<String> violationLogger) {
        this.project = project;
        this.requireManagement = requireManagement;
        this.violationLogger = violationLogger;
    }

    public boolean analyze() {
        boolean success = checkDependencyManagement();
        // don't combine with previous line, we don't want short-circuit evaluation
        success &= checkPluginManagement();

        if (!errorMessages.isEmpty()) {
            errorMessages.forEach(message -> {
                logViolation("------------------------------------------------------------------------");
                logViolation(message);
            });
        }

        return success;
    }

    private boolean checkDependencyManagement() {
        Map<String, Dependency> managedDependencies = getManagedDependenciesAsMap();
        Map<String, Dependency> originalDependencies = getOriginalDependenciesAsMap();

        boolean success = true;
        for (Dependency projectDependency : project.getDependencies()) {
            String dependencyKey = projectDependency.getManagementKey();
            RequireManagementConfig config = getEffectiveRequireManagementConfig(dependencyKey);
            Dependency managedDependency = managedDependencies.get(dependencyKey);
            Dependency originalDependency = originalDependencies.get(dependencyKey);

            if (managedDependency != null) {
                String projectVersion = projectDependency.getVersion();
                String managedVersion = managedDependency.getVersion();

                if (!projectVersion.equals(managedVersion)) {
                    String errorFormat = "Version mismatch for %s, managed version %s does not match version %s declared in module's pom";
                    String additionalMessage = "If you intended to use the version specified in dependency management, "
                            + "please remove the version from this module's dependency declaration.";
                    logViolation(String.format(errorFormat, dependencyKey, managedVersion, projectVersion));
                    logViolation(additionalMessage);
                    dependencyVersionMismatchError = true;
                    success = false;
                } else if (originalDependency != null) {
                    if (!config.allowVersions() && originalDependency.getVersion() != null) {
                        logViolation(String.format("Version tag must be removed for managed dependency %s", dependencyKey));
                        dependencyVersionDisallowedError = true;
                        success = false;
                    }

                    if (!config.allowExclusions() && !originalDependency.getExclusions().isEmpty()) {
                        logViolation(String.format("Exclusions must be removed for managed dependency %s", dependencyKey));
                        dependencyExclusionsError = true;
                        success = false;
                    }
                }
            } else if (config.requireDependencyManagement()) {
                logViolation(String.format("Dependency %s is not managed", dependencyKey));
                unmanagedDependencyError = true;
                success = false;
            }
        }

        if (dependencyVersionMismatchError && requireManagement.dependencyVersionMismatchMessage() != null) {
            errorMessages.add("Found versions mismatches in managed dependencies:");
            errorMessages.add(requireManagement.dependencyVersionMismatchMessage());
        }
        if (dependencyVersionDisallowedError && requireManagement.dependencyVersionDisallowedMessage() != null) {
            errorMessages.add("Found version in managed dependencies:");
            errorMessages.add(requireManagement.dependencyVersionDisallowedMessage());
        }
        if (dependencyExclusionsError && requireManagement.dependencyExclusionsMessage() != null) {
            errorMessages.add("Found exclusions in managed dependencies:");
            errorMessages.add(requireManagement.dependencyExclusionsMessage());
        }
        if (unmanagedDependencyError && requireManagement.unmanagedDependencyMessage() != null) {
            errorMessages.add("Found unmanaged dependencies:");
            errorMessages.add(requireManagement.unmanagedDependencyMessage());
        }
        return success;
    }

    private boolean checkPluginManagement() {
        Map<String, Plugin> managedPlugins = getManagedPluginsAsMap();

        boolean success = true;
        for (Plugin projectPlugin : project.getBuildPlugins()) {
            Plugin managedPlugin = managedPlugins.get(projectPlugin.getKey());
            RequireManagementConfig config = getEffectiveRequireManagementConfig(projectPlugin.getKey());

            if (managedPlugin != null) {
                String projectVersion = projectPlugin.getVersion();
                String managedVersion = managedPlugin.getVersion();

                if (!projectVersion.equals(managedVersion)) {
                    String errorFormat = "Version mismatch for plugin %s, managed version %s does not match version %s declared in module's pom";
                    String additionalMessage = "If you intended to use the version specified in dependency management, "
                            + "please remove the version from this module's plugin dependency declaration.";
                    logViolation(String.format(errorFormat, projectPlugin.getKey(), managedVersion, projectVersion));
                    logViolation(additionalMessage);
                    pluginVersionMismatchError = true;
                    success = false;
                }
            } else if (config.requirePluginManagement()) {
                logViolation(String.format("Plugin %s is not managed", projectPlugin.getKey()));
                unmanagedPluginError = true;
                success = false;
            }
        }
        if (pluginVersionMismatchError && requireManagement.pluginVersionMismatchMessage() != null) {
            errorMessages.add("Found version mismatches in plugins:");
            errorMessages.add(requireManagement.pluginVersionMismatchMessage());
        }
        if (unmanagedPluginError && requireManagement.unmanagedPluginMessage() != null) {
            errorMessages.add("Found unmanaged plugins:");
            errorMessages.add(requireManagement.unmanagedPluginMessage());
        }

        return success;
    }

    private Map<String, Plugin> getManagedPluginsAsMap() {
        if (project.getPluginManagement() == null) {
            return Collections.emptyMap();
        } else {
            return project.getPluginManagement().getPluginsAsMap();
        }
    }

    private Map<String, Dependency> getManagedDependenciesAsMap() {
        if (project.getDependencyManagement() == null || project.getDependencyManagement().getDependencies() == null) {
            return Collections.emptyMap();
        } else {
            return asMap(project.getDependencyManagement().getDependencies());
        }
    }

    private Map<String, Dependency> getOriginalDependenciesAsMap() {
        if (project.getOriginalModel() == null || project.getOriginalModel().getDependencies() == null) {
            return Collections.emptyMap();
        } else {
            return asMap(project.getOriginalModel().getDependencies());
        }
    }

    private RequireManagementConfig getEffectiveRequireManagementConfig(String key) {
        if (key.indexOf(':') != key.lastIndexOf(':')) {
            key = key.substring(0, key.indexOf(':', key.indexOf(':') + 1));
        }
        for (RequireManagementOverride override : requireManagement.getOverrides()) {
            for (String pattern : override.getPatterns()) {
                if (SelectorUtils.match(pattern, key)) {
                    return override.toRequireManagementConfig(requireManagement);
                }
            }
        }
        return requireManagement;
    }

    private static Map<String, Dependency> asMap(List<Dependency> dependencies) {
        Map<String, Dependency> dependencyMap = new HashMap<>();
        for (Dependency dependency : dependencies) {
            dependencyMap.put(dependency.getManagementKey(), dependency);
        }

        return dependencyMap;
    }

    private void logViolation(String message) {
        violationLogger.accept(message);
    }
}
