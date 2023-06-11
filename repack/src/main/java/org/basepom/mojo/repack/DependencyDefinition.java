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
import static com.google.common.base.Preconditions.checkState;
import static org.basepom.mojo.repack.Wildcard.wildcardMatch;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.CheckForNull;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.maven.artifact.Artifact;

final class DependencyDefinition {

    private final String artifactId;
    private final String groupId;
    private final String type;
    @CheckForNull
    private final String classifier;

    DependencyDefinition(final String value) {
        checkNotNull(value, "value is null");

        List<String> elements = Splitter.on(':').trimResults().splitToList(value);
        checkState(!elements.isEmpty(), "Dependency reference requires at least a group id!");

        String groupId = elements.get(0);

        this.groupId = groupId.isEmpty() ? "*" : groupId;

        this.artifactId = elements.size() > 1 && !elements.get(1).isEmpty() ? elements.get(1) : "*";
        String type = elements.size() > 2 && !elements.get(2).isEmpty() ? elements.get(2) : "jar";
        String classifier = elements.size() > 3 && !elements.get(3).isEmpty() ? elements.get(3) : null;

        if ("test-jar".equals(type)) {
            this.type = "jar";
            this.classifier = MoreObjects.firstNonNull(classifier, "tests");
        } else {
            this.type = type;
            this.classifier = classifier;
        }
    }

    DependencyDefinition(final Artifact artifact) {
        checkNotNull(artifact, "artifact is null");

        this.artifactId = checkNotNull(artifact.getArtifactId(), "artifactId for artifact '%s' is null", artifact);
        this.groupId = checkNotNull(artifact.getGroupId(), "groupId for artifact '%s' is null", artifact);

        final String type = artifact.getType();
        final String classifier = artifact.getClassifier();
        if ("test-jar".equals(type)) {
            this.classifier = MoreObjects.firstNonNull(classifier, "tests");
            this.type = "jar";
        } else {
            this.type = MoreObjects.firstNonNull(type, "jar");
            this.classifier = classifier;
        }
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getType() {
        return type;
    }

    public Optional<String> getClassifier() {
        return Optional.ofNullable(classifier);
    }

    public boolean matches(final Artifact artifact) {
        return matches(new DependencyDefinition(artifact));
    }

    public boolean matches(final DependencyDefinition other) {

        if (!wildcardMatch(getGroupId(), other.getGroupId())) {
            return false;
        }

        if (!wildcardMatch(getArtifactId(), other.getArtifactId())) {
            return false;
        }

        if (!Objects.equals(getType(), other.getType())) {
            return false;
        }

        // If a classifier is present, try to match the other classifier,
        // otherwise, if no classifier is present, it matches all classifiers from the other DependencyDefinition.
        return getClassifier()
            .map(cl -> Objects.equals(cl, other.getClassifier().orElse(null)))
            .orElse(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DependencyDefinition that = (DependencyDefinition) o;
        return artifactId.equals(that.artifactId) && groupId.equals(that.groupId) && type.equals(that.type) && Objects.equals(classifier, that.classifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, groupId, type, classifier);
    }

    @Override
    public String toString() {
        final ImmutableList.Builder<String> builder = ImmutableList.builder();

        builder.add(getGroupId());
        builder.add(getArtifactId());

        builder.add(getType());
        builder.add(getClassifier().orElse("<any>"));
        return Joiner.on(':').join(builder.build());
    }
}
