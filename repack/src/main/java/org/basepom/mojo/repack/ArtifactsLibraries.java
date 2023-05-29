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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.springframework.boot.loader.tools.Libraries;
import org.springframework.boot.loader.tools.Library;
import org.springframework.boot.loader.tools.LibraryCallback;
import org.springframework.boot.loader.tools.LibraryCoordinates;
import org.springframework.boot.loader.tools.LibraryScope;

/**
 * {@link Libraries} backed by Maven {@link Artifact}s.
 */
final class ArtifactsLibraries implements Libraries {

    private static final PluginLog LOG = new PluginLog(ArtifactsLibraries.class);

    private static final Map<String, LibraryScope> SCOPES = ImmutableMap.of(
            Artifact.SCOPE_COMPILE, LibraryScope.COMPILE,
            Artifact.SCOPE_RUNTIME, LibraryScope.RUNTIME,
            Artifact.SCOPE_PROVIDED, LibraryScope.PROVIDED,
            Artifact.SCOPE_SYSTEM, LibraryScope.PROVIDED);

    private final boolean quiet;
    private final Set<Artifact> artifacts;
    private final Set<Artifact> includedArtifacts;
    private final Collection<MavenProject> localProjects;
    private final Set<DependencyDefinition> runtimeUnpackedDependencies;
    private final Set<String> duplicates = new HashSet<>();

    ArtifactsLibraries(boolean quiet,
            Set<Artifact> artifacts,
            Set<Artifact> includedArtifacts,
            Collection<MavenProject> localProjects,
            Set<DependencyDefinition> runtimeUnpackedDependencies) {
        this.quiet = quiet;
        this.artifacts = checkNotNull(artifacts, "artifacts is null");
        this.includedArtifacts = checkNotNull(includedArtifacts, "includedArtifacts is null");
        this.localProjects = checkNotNull(localProjects, "localProjects is null");
        this.runtimeUnpackedDependencies = checkNotNull(runtimeUnpackedDependencies, "runtimeUnpackedDependencies is null");
    }

    @Override
    public void doWithLibraries(LibraryCallback callback) throws IOException {

        for (Artifact artifact : artifacts) {
            String name = createFileName(artifact);
            File file = artifact.getFile();

            LibraryScope scope = SCOPES.get(artifact.getScope());

            if (scope == null) {
                LOG.report(quiet, "Ignoring Dependency %s, scope is %s", artifact, artifact.getScope());
                Reporter.addExcluded(artifact, "scope");
                continue;
            }

            if (file == null) {
                LOG.report(quiet, "Ignoring Dependency %s, no file found!", artifact);
                Reporter.addExcluded(artifact, "nofile");
                continue;
            }

            if (duplicates.contains(name)) {
                LOG.warn("Ignoring Dependency %s, ignoring multiple inclusions!", artifact);
                continue;
            }

            duplicates.add(name);

            LibraryCoordinates coordinates = new ArtifactLibraryCoordinates(artifact);
            boolean runtimeUnpacked = isRuntimeUnpacked(artifact);
            if (runtimeUnpacked) {
                Reporter.addRuntimeUnpacked(artifact);
            }

            boolean local = isLocal(artifact);
            boolean included = includedArtifacts.contains(artifact);

            if (included) {
                Reporter.addIncluded(artifact);
            }

            callback.library(new Library(name, file, scope, coordinates, runtimeUnpacked, local, included));
        }
    }

    private boolean isRuntimeUnpacked(Artifact artifact) {
        for (DependencyDefinition runtimeUnpackedDependency : runtimeUnpackedDependencies) {
            if (runtimeUnpackedDependency.matches(artifact)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLocal(Artifact artifact) {
        for (MavenProject localProject : localProjects) {
            if (localProject.getArtifact().equals(artifact)) {
                return true;
            }
            for (Artifact attachedArtifact : localProject.getAttachedArtifacts()) {
                if (attachedArtifact.equals(artifact)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String createFileName(Artifact artifact) {
        StringBuilder sb = new StringBuilder();
        sb.append(artifact.getGroupId()).append('-');
        sb.append(artifact.getArtifactId()).append('-');
        sb.append(artifact.getBaseVersion());

        String classifier = artifact.getClassifier();
        if (classifier != null) {
            sb.append('-').append(classifier);
        }
        sb.append('.').append(artifact.getArtifactHandler().getExtension());

        return sb.toString();
    }

    /**
     * {@link LibraryCoordinates} backed by a Maven {@link Artifact}.
     */
    private static class ArtifactLibraryCoordinates implements LibraryCoordinates {

        private final Artifact artifact;

        ArtifactLibraryCoordinates(Artifact artifact) {
            this.artifact = checkNotNull(artifact, "artifact is null");
        }

        @Override
        public String getGroupId() {
            return artifact.getGroupId();
        }

        @Override
        public String getArtifactId() {
            return artifact.getArtifactId();
        }

        @Override
        public String getVersion() {
            return artifact.getBaseVersion();
        }

        @Override
        public String toString() {
            return artifact.toString();
        }
    }
}
