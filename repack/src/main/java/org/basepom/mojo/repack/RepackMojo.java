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
import static com.google.common.collect.ImmutableSet.toImmutableSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import org.springframework.boot.loader.tools.Layers;
import org.springframework.boot.loader.tools.LayoutFactory;
import org.springframework.boot.loader.tools.Libraries;
import org.springframework.boot.loader.tools.Repackager;

/**
 * Repack archives for execution using {@literal java -jar}. Can also be used to repack a jar with nested dependencies by using <code>layout=NONE</code>.
 */
@Mojo(name = "repack", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
public final class RepackMojo extends AbstractMojo {

    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

    private static final PluginLog LOG = new PluginLog(RepackMojo.class);

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    MavenSession session;

    @Component
    MavenProjectHelper projectHelper;

    /**
     * The name of the main class. If not specified the first compiled class found that contains a {@code main} method will be used.
     */
    @Parameter(property = "repack.main-class")
    String mainClass = null;

    /**
     * Collection of artifact definitions to include.
     */
    private Set<DependencyDefinition> includedDependencies = ImmutableSet.of();

    // called by maven
    @Parameter(alias = "includes")
    public void setIncludedDependencies(final String... includedDependencies) {
        checkNotNull(includedDependencies, "includedDependencies is null");

        this.includedDependencies = Arrays.stream(includedDependencies)
                .map(DependencyDefinition::new)
                .collect(toImmutableSet());
    }

    /**
     * Collection of artifact definitions to exclude.
     */
    private Set<DependencyDefinition> excludedDependencies = ImmutableSet.of();

    // called by maven
    @Parameter(alias = "excludedDependencies")
    public void setExcludedDependencies(final String... excludedDependencies) {
        checkNotNull(excludedDependencies, "excludedDependencies is null");

        this.excludedDependencies = Arrays.stream(excludedDependencies)
                .map(DependencyDefinition::new)
                .collect(toImmutableSet());
    }

    /**
     * Include system scoped dependencies.
     */
    @Parameter(defaultValue = "false", property = "repack.include-system-scope")
    boolean includeSystemScope = false;

    /**
     * Include provided scoped dependencies.
     */
    @Parameter(defaultValue = "false", property = "repack.include-provided-scope")
    boolean includeProvidedScope = false;

    /**
     * Include optional dependencies
     */
    @Parameter(defaultValue = "false", property = "repack.include-optional")
    boolean includeOptional = false;

    /**
     * Directory containing the generated archive.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "repack.output-directory")
    File outputDirectory;

    /**
     * Name of the generated archive.
     */
    @Parameter(defaultValue = "${project.build.finalName}", property = "repack.final-name")
    String finalName;

    /**
     * Skip the execution.
     */
    @Parameter(defaultValue = "false", property = "repack.skip")
    boolean skip = false;

    /**
     * Silence all non-output and non-error messages.
     */
    @Parameter(defaultValue = "false", property = "repack.quiet")
    boolean quiet = false;

    /**
     * Do a summary report.
     */
    @Parameter(defaultValue = "true", property = "repack.report")
    boolean report = true;

    /**
     * Classifier to add to the repacked archive. Use the blank string to replace the main artifact.
     */
    @Parameter(defaultValue = "repacked", property = "repack.classifier")
    String repackClassifier = "repacked";

    /**
     * Attach the repacked archive to the build cycle.
     */
    @Parameter(defaultValue = "true", property = "repack.attach-artifact")
    boolean attachRepackedArtifact = true;

    /**
     * A list of the libraries that must be unpacked at runtime (do not work within the fat jar).
     */
    private Set<DependencyDefinition> runtimeUnpackedDependencies = ImmutableSet.of();

    // called by maven
    @Parameter
    public void setRuntimeUnpackedDependencies(final String... runtimeUnpackedDependencies) {
        checkNotNull(runtimeUnpackedDependencies, "runtimeUnpackDependencies is null");

        this.runtimeUnpackedDependencies = Arrays.stream(runtimeUnpackedDependencies)
                .map(DependencyDefinition::new)
                .collect(toImmutableSet());
    }

    /**
     * A list of optional libraries that should be included even if optional dependencies are not included by default.
     */
    private Set<DependencyDefinition> optionalDependencies = ImmutableSet.of();

    // called by maven
    @Parameter
    public void setOptionalDependencies(final String... optionalDependencies) {
        checkNotNull(optionalDependencies, "optionalDependencies is null");

        this.optionalDependencies = Arrays.stream(optionalDependencies)
                .map(DependencyDefinition::new)
                .collect(toImmutableSet());
    }

    /**
     * Timestamp for reproducible output archive entries, either formatted as ISO 8601 (<code>yyyy-MM-dd'T'HH:mm:ssXXX</code>) or an {@code int} representing
     * seconds since the epoch.
     */
    @Parameter(defaultValue = "${project.build.outputTimestamp}", property = "repack.output-timestamp")
    String outputTimestamp;

    /**
     * The type of archive (which corresponds to how the dependencies are laid out inside it). Possible values are {@code JAR}, {@code WAR}, {@code ZIP},
     * {@code DIR}, {@code NONE}. Defaults to {@code JAR}.
     */
    @Parameter(defaultValue = "JAR", property = "repack.layout")
    LayoutType layout = LayoutType.JAR;

    /**
     * The layout factory that will be used to create the executable archive if no explicit layout is set. Alternative layouts implementations can be provided
     * by 3rd parties.
     */
    @Parameter
    LayoutFactory layoutFactory = null;


    @Override
    public void execute() throws MojoExecutionException {

        if (skip) {
            LOG.report(quiet, "Skipping plugin execution");
            return;
        }

        if ("pom".equals(project.getPackaging())) {
            LOG.report(quiet, "Ignoring POM project");
            return;
        }

        checkState(this.outputDirectory != null, "output directory was unset!");
        checkState(this.outputDirectory.exists(), "output directory '%s' does not exist!", this.outputDirectory.getAbsolutePath());

        if (Strings.nullToEmpty(finalName).isBlank()) {
            this.finalName = project.getArtifactId() + '-' + project.getVersion();
            LOG.report(quiet, "Final name unset, falling back to %s", this.finalName);
        }

        if (Strings.nullToEmpty(repackClassifier).isBlank()) {
            if (Strings.nullToEmpty(project.getArtifact().getClassifier()).isBlank()) {
                LOG.report(quiet, "Repacked archive will replace main artifact");
            } else {
                LOG.report(quiet, "Repacked archive will have no classifier, main artifact has classifier '%s'", project.getArtifact().getClassifier());
            }
        } else {
            if (repackClassifier.equals(project.getArtifact().getClassifier())) {
                LOG.report(quiet, "Repacked archive will replace main artifact using classifier '%s'", repackClassifier);
            } else {
                LOG.report(quiet, "Repacked archive will use classifier '%s', main artifact has %s", repackClassifier,
                        project.getArtifact().getClassifier() == null ? "no classifier" : "classifier '" + project.getArtifact().getClassifier() + "'");
            }
        }

        try {
            Artifact source = project.getArtifact();

            Repackager repackager = new Repackager(source.getFile());

            if (mainClass != null && !mainClass.isEmpty()) {
                repackager.setMainClass(mainClass);
            } else {
                repackager.addMainClassTimeoutWarningListener((duration, mainMethod) ->
                        LOG.warn("Searching for the main class is taking some time, "
                                + "consider using the mainClass configuration parameter."));
            }

            if (layoutFactory != null) {
                LOG.report(quiet, "Using %s Layout Factory to repack the %s artifact.", layoutFactory.getClass().getSimpleName(), project.getArtifact());
                repackager.setLayoutFactory(layoutFactory);
            } else if (layout != null) {
                LOG.report(quiet, "Using %s Layout to repack the %s artifact.", layout, project.getArtifact());
                repackager.setLayout(layout.layout());
            } else {
                LOG.warn("Neither Layout Factory nor Layout defined, resulting archive may be non-functional.");
            }

            repackager.setLayers(Layers.IMPLICIT);
            // tools need spring framework dependencies which are not guaranteed to be there. So turn this off.
            repackager.setIncludeRelevantJarModeJars(false);

            File targetFile = getTargetFile();
            Libraries libraries = getLibraries();
            FileTime outputFileTimestamp = parseOutputTimestamp();

            repackager.repackage(targetFile, libraries, null, outputFileTimestamp);

            boolean repackReplacesSource = source.getFile().equals(targetFile);

            if (attachRepackedArtifact) {
                if (repackReplacesSource) {
                    source.setFile(targetFile);
                } else {
                    projectHelper.attachArtifact(project, project.getPackaging(), Strings.emptyToNull(repackClassifier), targetFile);
                }
            } else if (repackReplacesSource && repackager.getBackupFile().exists()) {
                source.setFile(repackager.getBackupFile());
            } else if (!repackClassifier.isEmpty()) {
                LOG.report(quiet, "Created repacked archive %s with classifier %s!", targetFile, repackClassifier);
            }

            if (report) {
                Reporter.report(quiet, source, repackClassifier);
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private File getTargetFile() {
        StringBuilder targetFileName = new StringBuilder();

        targetFileName.append(finalName);

        if (!repackClassifier.isEmpty()) {
            targetFileName.append('-').append(repackClassifier);
        }

        targetFileName.append('.').append(project.getArtifact().getArtifactHandler().getExtension());

        return new File(outputDirectory, targetFileName.toString());
    }

    /**
     * Return {@link Libraries} that the packager can use.
     */
    private Libraries getLibraries() throws MojoExecutionException {

        try {
            Set<Artifact> artifacts = ImmutableSet.copyOf(project.getArtifacts());
            Set<Artifact> includedArtifacts = ImmutableSet.copyOf(buildFilters().filter(artifacts));
            return new ArtifactsLibraries(quiet, artifacts, includedArtifacts, session.getProjects(), runtimeUnpackedDependencies);
        } catch (ArtifactFilterException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private FilterArtifacts buildFilters() {

        FilterArtifacts filters = new FilterArtifacts();

        // remove all system scope artifacts
        if (!includeSystemScope) {
            filters.addFilter(new ScopeExclusionFilter(Artifact.SCOPE_SYSTEM));
        }

        // remove all provided scope artifacts
        if (!includeProvidedScope) {
            filters.addFilter(new ScopeExclusionFilter(Artifact.SCOPE_PROVIDED));
        }

        // if optionals are not included by default, filter out anything that is not included
        // through a matcher
        if (!includeOptional) {
            filters.addFilter(new OptionalArtifactFilter(optionalDependencies));
        }

        // add includes filter. If no includes are given, don't add a filter (everything is included)
        if (!includedDependencies.isEmpty()) {
            // an explicit include list given.
            filters.addFilter(new DependencyDefinitionFilter(includedDependencies, true));
        }

        // add excludes filter. If no excludes are given, don't add a filter (nothing gets excluded)
        if (!excludedDependencies.isEmpty()) {
            filters.addFilter(new DependencyDefinitionFilter(excludedDependencies, false));
        }

        return filters;
    }

    private FileTime parseOutputTimestamp() {
        // Maven ignore a single-character timestamp as it is "useful to override a full
        // value during pom inheritance"
        if (outputTimestamp == null || outputTimestamp.length() < 2) {
            return null;
        }

        long timestamp;

        try {
            timestamp = Long.parseLong(outputTimestamp);
        } catch (NumberFormatException ex) {
            timestamp = OffsetDateTime.parse(outputTimestamp).toInstant().getEpochSecond();
        }

        return FileTime.from(timestamp, TimeUnit.SECONDS);
    }
}
