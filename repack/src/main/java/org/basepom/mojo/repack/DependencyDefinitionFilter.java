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

package org.basepom.mojo.repack;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.artifact.filter.collection.AbstractArtifactsFilter;

final class DependencyDefinitionFilter extends AbstractArtifactsFilter {

    private final Set<DependencyDefinition> dependencies;
    private final boolean include;

    DependencyDefinitionFilter(Set<DependencyDefinition> dependencies, boolean include) {
        this.dependencies = checkNotNull(dependencies, "dependencies is null");
        this.include = include;
    }

    @Override
    public Set<Artifact> filter(Set<Artifact> artifacts) {
        ImmutableSet.Builder<Artifact> builder = ImmutableSet.builder();

        for (Artifact artifact : artifacts) {
            for (DependencyDefinition dependency : dependencies) {
                // inclusion filter and match -> included
                // exclusion filter and no match -> included
                if (include == dependency.matches(artifact)) {
                    builder.add(artifact);
                    break; // only add it once.
                } else {
                    Reporter.addExcluded(artifact, include ? "included" : "excluded");
                }
            }
        }
        return builder.build();
    }
}
