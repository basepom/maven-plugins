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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "analyze", requiresDependencyCollection = ResolutionScope.TEST, threadSafe = true)
public class DependencyManagementMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    public MavenProject project;

    @Parameter
    public RequireManagement requireManagement = new RequireManagement();

    @Parameter(defaultValue = "false")
    public boolean fail;

    @Parameter(defaultValue = "false")
    public boolean skip;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping plugin execution");
            return;
        }

        boolean success = new DependencyManagementAnalyzer(project, requireManagement, fail ? getLog()::error : getLog()::warn).analyze();
        if (success) {
            getLog().info("No dependency management issues found");
        } else if (fail) {
            throw new MojoExecutionException("Dependency management issues found");
        }
    }
}
