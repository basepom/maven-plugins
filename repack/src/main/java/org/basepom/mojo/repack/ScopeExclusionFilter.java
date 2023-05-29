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

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.artifact.filter.collection.AbstractArtifactsFilter;

final class ScopeExclusionFilter extends AbstractArtifactsFilter {

    private final String scope;

    ScopeExclusionFilter(String scope) {
        this.scope = scope;
    }

    @Override
    public Set<Artifact> filter(Set<Artifact> artifacts) {
        ImmutableSet.Builder<Artifact> builder = ImmutableSet.builder();

        for (Artifact artifact : artifacts) {
            if (scope.equals(artifact.getScope())) {
                Reporter.addExcluded(artifact, scope);
            } else {
                builder.add(artifact);
            }
        }

        return builder.build();
    }
}
