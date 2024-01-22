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

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.basepom.mojo.propertyhelper.IgnoreWarnFail.checkIgnoreWarnFailState;

import org.basepom.mojo.propertyhelper.definitions.DateDefinition;
import org.basepom.mojo.propertyhelper.definitions.FieldDefinition;
import org.basepom.mojo.propertyhelper.definitions.MacroDefinition;
import org.basepom.mojo.propertyhelper.definitions.NumberDefinition;
import org.basepom.mojo.propertyhelper.definitions.StringDefinition;
import org.basepom.mojo.propertyhelper.definitions.UuidDefinition;
import org.basepom.mojo.propertyhelper.fields.NumberField;
import org.basepom.mojo.propertyhelper.groups.PropertyGroup;
import org.basepom.mojo.propertyhelper.macros.MacroType;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import javax.inject.Inject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.flogger.FluentLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

public abstract class AbstractPropertyHelperMojo extends AbstractMojo implements PropertyElementContext {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    protected final ValueCache valueCache = new ValueCache(this);
    private IgnoreWarnFail onDuplicateField = IgnoreWarnFail.FAIL;

    /**
     * Defines the action to take if a field is defined multiple times (e.g. as a number and a string).
     */
    @Parameter(defaultValue = "fail", alias = "onDuplicateProperty")
    public void setOnDuplicateField(String onDuplicateField) {
        this.onDuplicateField = IgnoreWarnFail.forString(onDuplicateField);
    }

    /**
     * List of the property group ids to activate for a plugin execution. If no groups are defined, all groups are active.
     */
    private List<String> activeGroups = List.of();

    @Parameter
    public void setActiveGroups(String... activeGroups) {
        this.activeGroups = Arrays.asList(activeGroups);
    }

    /**
     * List of available property groups. A property group contains one or more property definitions and must be activated with activeGroups.
     */
    @Parameter
    public void setPropertyGroups(PropertyGroup... propertyGroups) {
        this.propertyGroups = Arrays.asList(propertyGroups);
    }

    private List<PropertyGroup> propertyGroups = List.of();

    /**
     * Number property definitions.
     */
    @Parameter
    public void setNumbers(NumberDefinition... numberDefinitions) {
        this.numberDefinitions = Arrays.asList(numberDefinitions);
    }

    private List<NumberDefinition> numberDefinitions = List.of();

    /**
     * String property definitions.
     */
    @Parameter
    public void setStrings(StringDefinition... stringDefinitions) {
        this.stringDefinitions = Arrays.asList(stringDefinitions);
    }

    private List<StringDefinition> stringDefinitions = List.of();

    /**
     * Date property definitions.
     */
    @Parameter
    public void setDates(DateDefinition... dateDefinitions) {
        this.dateDefinitions = Arrays.asList(dateDefinitions);
    }

    private List<DateDefinition> dateDefinitions = List.of();

    /**
     * Macro definitions.
     */
    @Parameter
    public void setMacros(MacroDefinition... macroDefinitions) {
        this.macroDefinitions = Arrays.asList(macroDefinitions);
    }

    private List<MacroDefinition> macroDefinitions = List.of();

    /**
     * Uuid definitions.
     */
    @Parameter
    public void setUuids(UuidDefinition... uuidDefinitions) {
        this.uuidDefinitions = Arrays.asList(uuidDefinitions);
    }

    private List<UuidDefinition> uuidDefinitions = List.of();

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


    @Inject
    public void setMacroMap(Map<String, MacroType> macroMap) {
        this.macroMap = ImmutableMap.copyOf(macroMap);
    }

    private Map<String, MacroType> macroMap = Map.of();

    // internal mojo state.
    private boolean isSnapshot;
    private InterpolatorFactory interpolatorFactory;

    private Map<String, FieldDefinition> fieldDefinitions = Map.of();
    private List<NumberField> numberFields = List.of();
    private Map<String, String> values = Map.of();

    @Override
    public void execute() throws MojoExecutionException {
        this.isSnapshot = project.getArtifact().isSnapshot();
        this.interpolatorFactory = new InterpolatorFactory(project.getModel());

        LOG.atFine().log("Current build is a %s project.", isSnapshot ? "snapshot" : "release");
        LOG.atFiner().log("On duplicate field definitions: %s", onDuplicateField);

        try {
            if (skip) {
                LOG.atFine().log("skipping plugin execution!");
            } else {
                doExecute();
            }
        } catch (IOException e) {
            throw new MojoExecutionException("While running mojo: ", e);
        }
    }

    @Override
    public MavenProject getProject() {
        return project;
    }

    @Override
    public File getBasedir() {
        return basedir;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public Map<String, MacroType> getMacros() {
        return macroMap;
    }

    @Override
    public Properties getProperties() {
        return project.getProperties();
    }

    protected List<NumberField> getNumbers() {
        return numberFields;
    }

    /**
     * Subclasses need to implement this method.
     */
    protected abstract void doExecute() throws IOException, MojoExecutionException;

    private void addDefinitions(ImmutableMap.Builder<String, FieldDefinition> builder, List<? extends FieldDefinition> newDefinitions) {
        Map<String, FieldDefinition> existingDefinitions = builder.build();

        for (FieldDefinition definition : newDefinitions) {
            final String propertyName = definition.getId();

            if (checkIgnoreWarnFailState(!existingDefinitions.containsKey(propertyName), onDuplicateField,
                () -> format("field definition '%s' does not exist", propertyName),
                () -> format("field definition '%s' already exists!", propertyName))) {
                builder.put(propertyName, definition);
            }
        }
    }

    protected void createFieldDefinitions() {

        final ImmutableMap.Builder<String, FieldDefinition> builder = ImmutableMap.builder();
        addDefinitions(builder, numberDefinitions);
        addDefinitions(builder, stringDefinitions);
        addDefinitions(builder, macroDefinitions);
        addDefinitions(builder, dateDefinitions);
        addDefinitions(builder, uuidDefinitions);

        this.fieldDefinitions = builder.build();
    }

    protected void createFields() throws MojoExecutionException, IOException {
        ImmutableList.Builder<NumberField> numberFields = ImmutableList.builder();

        var builder = ImmutableMap.<String, String>builder();

        for (FieldDefinition definition : fieldDefinitions.values()) {
            Field field = definition.createPropertyElement(this, valueCache);

            if (field instanceof NumberField) {
                numberFields.add((NumberField) field);
            }

            final var fieldValue = field.getValue();

            builder.put(field.getFieldName(), fieldValue);

            if (field.isExposeAsProperty()) {
                project.getProperties().setProperty(field.getFieldName(), fieldValue);
                LOG.atFine().log("Exporting Property name: %s, value: %s", field.getFieldName(), fieldValue);
            } else {
                LOG.atFine().log("Property name: %s, value: %s", field.getFieldName(), fieldValue);
            }
        }

        this.numberFields = numberFields.build();
        this.values = builder.build();
    }

    // generates the property groups.
    @SuppressFBWarnings(value = "WMI_WRONG_MAP_ITERATOR")
    public void createGroups() throws MojoExecutionException, IOException {
        final ImmutableMap.Builder<String, Entry<PropertyGroup, List<Field>>> propertyGroupBuilder = ImmutableMap.builder();

        Set<String> exportedFields = fieldDefinitions.values().stream()
            .filter(FieldDefinition::isExport)
            .map(FieldDefinition::getId).collect(ImmutableSet.toImmutableSet());

        final Set<String> propertyNames = new LinkedHashSet<>(exportedFields);

        for (final PropertyGroup propertyGroup : propertyGroups) {
            final List<Field> propertyFields = propertyGroup.createFields(values, interpolatorFactory);
            propertyGroupBuilder.put(propertyGroup.getId(), new SimpleImmutableEntry<>(propertyGroup, propertyFields));
        }

        final Map<String, Entry<PropertyGroup, List<Field>>> propertyPairs = propertyGroupBuilder.build();

        var groupsToAdd = !this.activeGroups.isEmpty() ? this.activeGroups : propertyPairs.keySet();

        for (final String groupToAdd : groupsToAdd) {
            final var activeGroup = propertyPairs.get(groupToAdd);
            checkState(activeGroup != null, "activated group '%s' does not exist", activeGroup);

            final PropertyGroup propertyGroup = activeGroup.getKey();

            if ((propertyGroup.isActiveOnRelease() && !isSnapshot) || (propertyGroup.isActiveOnSnapshot() && isSnapshot)) {
                for (final Field field : activeGroup.getValue()) {
                    final String fieldName = field.getFieldName();

                    if (checkIgnoreWarnFailState(!propertyNames.contains(fieldName), propertyGroup.getOnDuplicateProperty(),
                        () -> format("property '%s' is not exposed", fieldName),
                        () -> format("property '%s' is already exposed!", fieldName))) {

                        project.getProperties().setProperty(fieldName, field.getValue());
                    }
                }
            } else {
                LOG.atFine().log("Skipping property group %s: Snapshot: %b, activeOnSnapshot: %b, activeOnRelease: %b", activeGroup, isSnapshot,
                    propertyGroup.isActiveOnSnapshot(), propertyGroup.isActiveOnRelease());
            }
        }
    }
}
