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

package org.basepom.mojo.dependencyscope;

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.apache.maven.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;

public class DependencyViolation {

    private final TraversalContext source;
    private final Dependency dependency;

    public DependencyViolation(TraversalContext source, Dependency dependency) {
        this.source = source;
        this.dependency = dependency;
    }

    public TraversalContext getSource() {
        return source;
    }

    public List<String> getPath() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        source.path().stream().map(Artifact::toString).forEach(builder::add);
        builder.add(dependency.getArtifact() + ":" + dependency.getScope());

        return builder.build();
    }

    public Dependency getDependency() {
        return dependency;
    }
}
