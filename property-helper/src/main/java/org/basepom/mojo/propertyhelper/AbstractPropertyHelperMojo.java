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

package org.basepom.mojo.propertyhelper;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.basepom.mojo.propertyhelper.beans.DateDefinition;
import org.basepom.mojo.propertyhelper.beans.IgnoreWarnFail;
import org.basepom.mojo.propertyhelper.beans.MacroDefinition;
import org.basepom.mojo.propertyhelper.beans.NumberDefinition;
import org.basepom.mojo.propertyhelper.beans.PropertyGroup;
import org.basepom.mojo.propertyhelper.beans.StringDefinition;
import org.basepom.mojo.propertyhelper.beans.UuidDefinition;
import org.basepom.mojo.propertyhelper.util.Log;

import java.io.File;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import javax.annotation.CheckForNull;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * Base code for all the mojos.
 */
public abstract class AbstractPropertyHelperMojo
        extends AbstractMojo
        implements Contextualizable {

    protected static final Log LOG = Log.findLog();
    protected final ValueCache valueCache = new ValueCache();
    private final Map<String, String> values = Maps.newHashMap();
    /**
     * Defines the action to take if a property is present multiple times.
     */
    @Parameter(defaultValue = "fail")
    String onDuplicateProperty = "fail";
    /**
     * Defines the action to take if a referenced property is missing.
     */
    @Parameter(defaultValue = "fail")
    String onMissingProperty = "fail";
    /**
     * List of the property group ids to activate for a plugin execution.
     */
    @Parameter
    String[] activeGroups = new String[0];
    /**
     * List of available property groups. A property group contains one or more property definitions and must be activated with activeGroups.
     */
    @Parameter
    PropertyGroup[] propertyGroups = new PropertyGroup[0];
    /**
     * Number property definitions.
     */
    @Parameter
    NumberDefinition[] numbers = new NumberDefinition[0];
    /**
     * String property definitions.
     */
    @Parameter
    StringDefinition[] strings = new StringDefinition[0];
    /**
     * Date property definitions.
     */
    @Parameter
    DateDefinition[] dates = new DateDefinition[0];
    /**
     * Macro definitions.
     */
    @Parameter
    MacroDefinition[] macros = new MacroDefinition[0];
    /**
     * Uuid definitions.
     */
    @Parameter
    UuidDefinition[] uuids = new UuidDefinition[0];
    /**
     * The maven project (effective pom).
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    MavenProject project;
    @Parameter(defaultValue = "${settings}", readonly = true)
    Settings settings;
    @Parameter(required = true, readonly = true, defaultValue = "${project.basedir}")
    File basedir;
    /**
     * If set to true, goal execution is skipped.
     */
    @Parameter(defaultValue = "false")
    boolean skip;
    private List<NumberField> numberFields = null;

    private PlexusContainer container = null;

    private boolean isSnapshot;

    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException {
        isSnapshot = project.getArtifact().isSnapshot();
        LOG.debug("Project is a %s.", isSnapshot ? "snapshot" : "release");
        LOG.trace("%s on duplicate, %s on missing", onDuplicateProperty, onMissingProperty);

        try {
            if (skip) {
                LOG.debug("Skipping execution!");
            } else {
                doExecute();
            }
        } catch (Exception e) {
            Throwables.throwIfInstanceOf(e, MojoExecutionException.class);
            Throwables.throwIfInstanceOf(e, MojoFailureException.class);
            Throwables.throwIfUnchecked(e);
            throw new MojoExecutionException("While running mojo: ", e);
        } finally {
            LOG.debug("Ended %s mojo run!", this.getClass().getSimpleName());
        }
    }

    public MavenProject getProject() {
        checkNotNull(project, "project is null");
        return project;
    }

    public Settings getSettings() {
        checkNotNull(settings, "settings is null");
        return settings;
    }

    public File getBasedir() {
        checkNotNull(basedir, "basedir is null");
        return basedir;
    }

    public PlexusContainer getContainer() {
        checkNotNull(container, "container is null");
        return container;
    }

    @CheckForNull
    public List<NumberField> getNumbers() {
        return numberFields;
    }

    @Override
    public void contextualize(final Context context)
            throws ContextException {
        this.container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

    /**
     * Subclasses need to implement this method.
     */
    protected abstract void doExecute()
            throws Exception;

    protected void loadPropertyElements()
            throws Exception {
        final Builder<PropertyElement> propertyElements = ImmutableList.builder();

        numberFields = NumberField.createNumbers(valueCache, numbers);
        propertyElements.addAll(numberFields);
        propertyElements.addAll(StringField.createStrings(valueCache, strings));
        propertyElements.addAll(DateField.createDates(valueCache, dates));
        propertyElements.addAll(MacroField.createMacros(valueCache, macros, this));
        propertyElements.addAll(UuidField.createUuids(valueCache, uuids));

        for (final PropertyElement pe : propertyElements.build()) {
            final Optional<String> value = pe.getPropertyValue();
            values.put(pe.getPropertyName(), value.orElse(null));

            if (pe.isExport()) {
                final String result = value.orElse("");
                project.getProperties().setProperty(pe.getPropertyName(), result);
                LOG.debug("Exporting Property name: %s, value: %s", pe.getPropertyName(), result);
            } else {
                LOG.debug("Property name: %s, value: %s", pe.getPropertyName(),
                        value.orElse("<null>"));
            }
        }

        // Now generate the property groups.
        final ImmutableMap.Builder<String, Entry<PropertyGroup, List<PropertyElement>>> builder = ImmutableMap.builder();

        final Set<String> propertyNames = Sets.newHashSet();

        if (propertyGroups != null) {
            for (final PropertyGroup propertyGroup : propertyGroups) {
                final List<PropertyElement> propertyFields = PropertyField.createProperties(project.getModel(), values, propertyGroup);
                builder.put(propertyGroup.getId(), new SimpleImmutableEntry<>(propertyGroup, propertyFields));
            }
        }

        final Map<String, Entry<PropertyGroup, List<PropertyElement>>> propertyPairs = builder.build();

        if (activeGroups != null) {
            for (final String activeGroup : activeGroups) {
                final Entry<PropertyGroup, List<PropertyElement>> propertyElement = propertyPairs.get(activeGroup);
                checkState(propertyElement != null, "activated group '%s' does not exist", activeGroup);

                final PropertyGroup propertyGroup = propertyElement.getKey();
                if ((propertyGroup.isActiveOnRelease() && !isSnapshot) || (propertyGroup.isActiveOnSnapshot() && isSnapshot)) {
                    for (final PropertyElement pe : propertyElement.getValue()) {
                        final Optional<String> value = pe.getPropertyValue();
                        final String propertyName = pe.getPropertyName();
                        IgnoreWarnFail.checkState(propertyGroup.getOnDuplicateProperty(), !propertyNames.contains(propertyName),
                                "property name '" + propertyName + "'");
                        propertyNames.add(propertyName);

                        project.getProperties().setProperty(propertyName, value.orElse(""));
                    }
                } else {
                    LOG.debug("Skipping property group %s: Snapshot: %b, activeOnSnapshot: %b, activeOnRelease: %b", activeGroup, isSnapshot,
                            propertyGroup.isActiveOnSnapshot(), propertyGroup.isActiveOnRelease());
                }
            }
        }
    }
}
