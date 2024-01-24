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
import org.basepom.mojo.propertyhelper.definitions.PropertyGroupDefinition;
import org.basepom.mojo.propertyhelper.definitions.StringDefinition;
import org.basepom.mojo.propertyhelper.definitions.UuidDefinition;
import org.basepom.mojo.propertyhelper.fields.NumberField;
import org.basepom.mojo.propertyhelper.groups.PropertyGroup;
import org.basepom.mojo.propertyhelper.groups.PropertyResult;
import org.basepom.mojo.propertyhelper.macros.MacroType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

public abstract class AbstractPropertyHelperMojo extends AbstractMojo implements FieldContext {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    protected final ValueCache valueCache = new ValueCache();
    private IgnoreWarnFail onDuplicateField = IgnoreWarnFail.FAIL;

    /**
     * Defines the action to take if a field is defined multiple times (e.g. as a number and a string).
     * <br>
     * Options are
     * <ul>
     *     <li><code>ignore</code> - ignore multiple definitions silently, retain just the first one found</li>
     *     <li><code>warn</code> - like ignore, but also log a warning message</li>
     *     <li><code>fail</code> - fail the build with an exception</li>
     * </ul>
     */
    @Parameter(defaultValue = "fail", alias = "onDuplicateProperty")
    public void setOnDuplicateField(String onDuplicateField) {
        this.onDuplicateField = IgnoreWarnFail.forString(onDuplicateField);
    }

    private List<String> activeGroups = List.of();

    /**
     * The property groups to activate. If none are given, all property groups are activated.
     * <pre>{@code
     * <activeGroups>
     *     <activeGroup>group1</activeGroup>
     *     <activeGroup>group2</activeGroup>
     *     ...
     * </activeGroups>
     * }</pre>
     */
    @Parameter
    public void setActiveGroups(String... activeGroups) {
        this.activeGroups = Arrays.asList(activeGroups);
    }

    /**
     * Define property groups. A property group contains one or more property definitions. Property groups are active by default
     * unless they are explicitly listed with {@code <activeGroups>...</activeGroups}.
     * <pre>{@code
     * <propertyGroups>
     *     <propertyGroup>
     *         <id>...</id>
     *         <activeOnRelease>true|false</activeOnRelease>
     *         <activeOnSnapshot>true|false</activeOnSnapshot>
     *         <onDuplicateProperty>ignore|warn|fail</onDuplicateProperty>
     *         <onMissingProperty>ignore|warn|fail</onMissingProperty>
     *         <properties>
     *             <property>
     *                 <name></name>
     *                 <value></value>
     *                 <transformers>...</transformers>
     *             </property>
     *         </properties>
     *     </propertyGroup>
     *     ...
     * </propertyGroups>
     * }</pre>
     */
    @Parameter
    public void setPropertyGroups(PropertyGroupDefinition... propertyGroups) {
        this.propertyGroupDefinitions = Arrays.asList(propertyGroups);
    }

    private List<PropertyGroupDefinition> propertyGroupDefinitions = List.of();

    /**
     * Number property definitions.
     *
     * <pre>{@code
     * <numbers>
     *     <number>
     *         <id>...</id>
     *         <skip>true|false</skip>
     *         <export>true|false</export>
     *
     *         <initialValue></initialValue>
     *         <format></format>
     *         <fieldNumber></fieldNumber>
     *         <increment></increment>
     *         <transformers>...</transformers>
     *
     *         <propertyFile></propertyFile>
     *         <propertyNameInFile></propertyNameInFile>
     *         <onMissingFile>ignore|warn|fail|create</onMissingFile>
     *         <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
     *         <onMissingProperty>ignore|warn|fail</onMissingProperty>
     *     </number>
     *     ...
     * </numbers>
     * }</pre>
     */
    @Parameter
    public void setNumbers(NumberDefinition... numberDefinitions) {
        this.numberDefinitions = Arrays.asList(numberDefinitions);
    }

    private List<NumberDefinition> numberDefinitions = List.of();

    /**
     * String property definitions.
     *
     * <pre>{@code
     * <strings>
     *     <string>
     *         <id>...</id>
     *         <skip>true|false</skip>
     *         <export>true|false</export>
     *
     *         <initialValue>...</initialValue>
     *         <format></format>
     *         <values>
     *             <value>...</value>
     *         </values>
     *         <blankIsValid>true|false</blankIsValid>
     *         <onMissingValue>ignore|warn|fail</onMissingValue
     *         <transformers>...</transformers>
     *
     *         <propertyFile></propertyFile>
     *         <propertyNameInFile></propertyNameInFile>
     *         <onMissingFile>ignore|warn|fail|create</onMissingFile>
     *         <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
     *         <onMissingProperty>ignore|warn|fail</onMissingProperty>
     *     </string>
     * </strings>
     * }</pre>
     */
    @Parameter
    public void setStrings(StringDefinition... stringDefinitions) {
        this.stringDefinitions = Arrays.asList(stringDefinitions);
    }

    private List<StringDefinition> stringDefinitions = List.of();

    /**
     * Date property definitions.
     *
     * <pre>{@code
     * <dates>
     *     <date>
     *         <id>...</id>
     *         <skip>true|false</skip>
     *         <export>true|false</export>
     *
     *         <initialValue></initialValue>
     *         <value></value>
     *         <format></format>
     *         <timezone></timezone>
     *         <transformers>...</transformers>
     *
     *         <propertyFile></propertyFile>
     *         <propertyNameInFile></propertyNameInFile>
     *         <onMissingFile>ignore|warn|fail|create</onMissingFile>
     *         <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
     *         <onMissingProperty>ignore|warn|fail</onMissingProperty>
     *     </date>
     * </dates>
     * }</pre>
     */
    @Parameter
    public void setDates(DateDefinition... dateDefinitions) {
        this.dateDefinitions = Arrays.asList(dateDefinitions);
    }

    private List<DateDefinition> dateDefinitions = List.of();

    /**
     * Macro definitions.
     *
     * <pre>{@code
     * <macros>
     *     <macro>
     *         <id>...</id>
     *         <skip>true|false</skip>
     *         <export>true|false</export>
     *
     *         <macroType></macroType>
     *         <macroClass></macroClass>
     *         <properties>
     *             <property></property>
     *         </properties>
     *
     *         <initialValue></initialValue>
     *         <format></format>
     *         <transformers>...</transformers>
     *
     *         <propertyFile></propertyFile>
     *         <propertyNameInFile></propertyNameInFile>
     *         <onMissingFile>ignore|warn|fail|create</onMissingFile>
     *         <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
     *         <onMissingProperty>ignore|warn|fail</onMissingProperty>
     *     </macro>
     * </macros>
     * }</pre>
     */
    @Parameter
    public void setMacros(MacroDefinition... macroDefinitions) {
        this.macroDefinitions = Arrays.asList(macroDefinitions);
    }

    private List<MacroDefinition> macroDefinitions = List.of();

    /**
     * Uuid definitions.
     *
     * <pre>{@code
     * <uuids>
     *     <uuid>
     *         <id>...</id>
     *         <skip>true|false</skip>
     *         <export>true|false</export>
     *
     *         <initialValue></initialValue>
     *         <value></value>
     *         <format></format>
     *         <transformers>...</transformers>
     *
     *         <propertyFile></propertyFile>
     *         <propertyNameInFile></propertyNameInFile>
     *         <onMissingFile>ignore|warn|fail|create</onMissingFile>
     *         <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
     *         <onMissingProperty>ignore|warn|fail</onMissingProperty>
     *     </uuid>
     * </uuids>
     * }</pre>
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
    private TransformerRegistry transformerRegistry;

    private Map<String, FieldDefinition<?>> fieldDefinitions = Map.of();
    private List<NumberField> numberFields = List.of();
    private Map<String, String> values = Map.of();

    @Override
    public void execute() throws MojoExecutionException {
        this.isSnapshot = project.getArtifact().isSnapshot();
        this.interpolatorFactory = new InterpolatorFactory(project.getModel());
        this.transformerRegistry = TransformerRegistry.INSTANCE;

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

    @Override
    public InterpolatorFactory getInterpolatorFactory() {
        return interpolatorFactory;
    }

    @Override
    public TransformerRegistry getTransformerRegistry() {
        return transformerRegistry;
    }

    protected List<NumberField> getNumbers() {
        return numberFields;
    }

    /**
     * Subclasses need to implement this method.
     */
    protected abstract void doExecute() throws IOException, MojoExecutionException;

    private void addDefinitions(ImmutableMap.Builder<String, FieldDefinition<?>> builder, List<? extends FieldDefinition<?>> newDefinitions) {
        Map<String, FieldDefinition<?>> existingDefinitions = builder.build();

        for (FieldDefinition<?> definition : newDefinitions) {
            String propertyName = definition.getId();

            if (checkIgnoreWarnFailState(!existingDefinitions.containsKey(propertyName), onDuplicateField,
                () -> format("field definition '%s' does not exist", propertyName),
                () -> format("field definition '%s' already exists!", propertyName))) {
                builder.put(propertyName, definition);
            }
        }
    }

    protected void createFieldDefinitions() {

        ImmutableMap.Builder<String, FieldDefinition<?>> builder = ImmutableMap.builder();
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

        for (FieldDefinition<?> definition : fieldDefinitions.values()) {
            Field<?, ?> field = definition.createField(this, valueCache);

            if (field instanceof NumberField) {
                numberFields.add((NumberField) field);
            }

            var fieldValue = field.getValue();
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
    public void createGroups() {
        var groupMapBuilder = ImmutableMap.<String, PropertyGroup>builder();
        var resultMapBuilder = ImmutableMap.<String, Set<PropertyResult>>builder();

        Set<String> exportedFields = fieldDefinitions.values().stream()
            .filter(FieldDefinition::isExport)
            .map(FieldDefinition::getId).collect(ImmutableSet.toImmutableSet());

        Set<String> propertyNames = new LinkedHashSet<>(exportedFields);

        propertyGroupDefinitions.forEach(propertyGroupDefinition -> {
            PropertyGroup propertyGroup = propertyGroupDefinition.createGroup(this);
            Set<PropertyResult> propertyResults = propertyGroup.createProperties(values);
            groupMapBuilder.put(propertyGroup.getId(), propertyGroup);
            resultMapBuilder.put(propertyGroup.getId(), propertyResults);
        });

        var groupMap = groupMapBuilder.build();
        var resultMap = resultMapBuilder.build();

        var groupsToAdd = this.activeGroups.isEmpty()
            ? groupMap.keySet()
            : this.activeGroups;

        for (String groupToAdd : groupsToAdd) {

            var activeGroup = groupMap.get(groupToAdd);
            checkState(activeGroup != null, "activated group '%s' does not exist", groupToAdd);
            var activeResult = resultMap.get(groupToAdd);
            checkState(activeResult != null, "activated group '%s' has no result", groupToAdd);

            if (activeGroup.checkActive(isSnapshot)) {
                for (PropertyResult propertyResult : activeResult) {
                    String propertyName = propertyResult.getPropertyName();

                    if (checkIgnoreWarnFailState(!propertyNames.contains(propertyName), activeGroup.getOnDuplicateProperty(),
                        () -> format("property '%s' is not exposed", propertyName),
                        () -> format("property '%s' is already exposed!", propertyName))) {

                        project.getProperties().setProperty(propertyName, propertyResult.getPropertyValue());
                    }
                }
            } else {
                LOG.atFine().log("Skipping property group %s, not active", activeGroup);
            }
        }
    }
}
