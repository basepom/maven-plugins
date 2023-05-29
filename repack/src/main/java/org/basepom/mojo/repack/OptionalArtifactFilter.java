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

final class OptionalArtifactFilter extends AbstractArtifactsFilter {

    private final Set<DependencyDefinition> includeDependencies;

    OptionalArtifactFilter(Set<DependencyDefinition> includedDependencies) {
        this.includeDependencies = checkNotNull(includedDependencies, "includedDependencies is null");
    }

    @Override
    public Set<Artifact> filter(Set<Artifact> artifacts) {
        ImmutableSet.Builder<Artifact> builder = ImmutableSet.builder();

        Artifacts:
        for (Artifact artifact : artifacts) {
            if (!artifact.isOptional()) {
                // not optional: included
                builder.add(artifact);
            } else {
                // search for a matcher that brings the dependency in
                for (DependencyDefinition includedDependency : includeDependencies) {
                    if (includedDependency.matches(artifact)) {
                        builder.add(artifact);
                        Reporter.addOptional(artifact);
                        continue Artifacts;
                    }
                }
                Reporter.addExcluded(artifact, "optional");
            }
        }

        return builder.build();
    }
}
