# property-helper Maven Plugin

Define, create and manipulate properties for the maven build cycle. This plugin can

- create properties to e.g. represent build versions, identifiers, switches etc.
- combine different type of fields into properties and export them to the maven build cycle
- manipulate existing properties

The plugin has two goals:

- `property-helper:get` - load and save property files, define fields, combine fields to property groups and new properties, export these to the maven build
- `property-helper:inc` - increment number field values by loading, manipulating and saving property files

## Configuration

The plugin defines different fields that are combined to form properties:

- `string` fields - provides a string field
- `number` fields - provide a structured number field
- `date` fields - provide a formatted date field
- `uuid` fields - provide a uuid value field
- `macro` fields - custom code to provide specific fields

Each field can be exported directly as a property or combined in a property group.

A property group can be activated for release or snapshot builds and aggregates fields into a single property that gets exposed.

### Maven plugin configuration example

```xml
<configuration>
    <propertyGroups>
        <propertyGroup>
            <id>build-tag</id>
            <properties>
                <property>
                    <name>build.tag</name>
                    <value>@{build-tag}-@{build-number}-@{build-date}</value>
                </property>
            </properties>
        </propertyGroup>
    </propertyGroups>
    <dates>
        <date>
            <id>build-date</id>
            <timezone>UTC</timezone>
            <format>yyyyMMdd_HHmmss</format>
        </date>
    </dates>
    <uuids>
        <uuid>
            <id>build-tag</id>
        </uuid>
    </uuids>
    <numbers>
        <number>
            <id>build-number</id>
            <propertyNameInFile>build.number</propertyNameInFile>
            <propertyFile>${user.home}/build.properties</propertyFile>
            <onMissingFile>create</onMissingFile>
            <onMissingFileProperty>create</onMissingFileProperty>
            <initialValue>1</initialValue>
        </number>
    </numbers>
</configuration>
```

This configuration defines three fields, `build-date`, `build-tag` and `build-number`. They are combined to form a property `build.tag` which can be referred to
in following build steps as `${build.tag}`.

The `build-date` field is a date field. As there is no value given or loaded from a file, it uses the current time as its value. By setting the timezone
to `UTC`, this timezone will be used.

`build-tag` is a UUID value. As no seed value is given and the field is also not loaded from a file, it creates a random UUID.

Finally, `build-number` is a number field. It is backed by a property in a property file `build.properties` (which e.g. lives in the home directory of the CI
user). Its value is loaded from the `build.number` property in the file (and if the file or the property do not exist, it will create those).

All three values combined form the `build.tag` property which is exported as a property.

## configuration reference

The following configuration reference is valid for both goals, `get` and `inc`. They only differ in the default value for the `persist` attribute: The `get`
will not save or create any
property file by default and must be explicitly configured. The `inc` goal will by default save any modified or new values to property files.

### plugin configuration

| configuration attribute | allowed values                      | required | default value                           | function                                                                       |
|-------------------------|-------------------------------------|----------|-----------------------------------------|--------------------------------------------------------------------------------|
| skip                    | `true`, `false`                     | no       | `false`                                 | if true, skip plugin execution                                                 |
| persist                 | `true`, `false`                     | no       | "get" goal: `false`, "inc" goal: `true` | if true, persist all referenced file properties                                |
| onDuplicateField        | `ignore`, `warn`, `fail`            | no       | `fail`                                  | action in case a field name is used multiple times                             |
| activeGroups            | nested list of property group names | no       | (none)                                  | which property groups to activate. If empty, all property groups are activated |

In addition to these configuration attributes, the plugin also supports `<strings>`, `<numbers>`, `<dates>`, `<uuids>` and `<macros>` which are defined in
detail below. Each of these attributes
defines fields

### Using existing maven build properties

The property helper plugin can reference existing maven properties when defining fields. The most common way to do so is the familiar `${...}` syntax. E.g. the
following example defines
a field that is set to the groupId and artifactId of the project:

```xml
<strings>
    <string>
        <id>group_artifact</id>
        <values>
            <value>${project.groupId}-${project.artifactId}</value>
        </values>
        <export>true</export>
    </string>
</strings>
```

The property helper plugin supports all defined build properties and the project properties prefixed with `project`.

Due to the way the Maven POM is evaluated and `${...}` variables are evaluated, only build properties that are known at the beginning of the Maven lifecycle are
available through this syntax.

Any build properties that are defined and exported by other plugins (e.g. build information from plugins like the git-commit-id) must be accessed by using
the `@{...}` syntax for "late binding" properties that are evaluated when the plugin executes.

The following example accesses the `git.closest.tag.name` property which is created by the `git-commit-id` plugin to define a field:

```xml
<plugins>
    <plugin>
        <groupId>io.github.git-commit-id</groupId>
        <artifactId>git-commit-id-maven-plugin</artifactId>
        <executions>
            <execution>
                <phase>initialize</phase>
                <goals>
                    <goal>revision</goal>
                </goals>
            </execution>
        </executions>
    </plugin>

    <plugin>
        <groupId>org.basepom.maven</groupId>
        <artifactId>property-helper-maven-plugin</artifactId>
        <executions>
            <execution>
                <id>test-git-id</id>
                <phase>initialize</phase>
                <goals>
                    <goal>get</goal>
                </goals>
                <configuration>
                    <strings>
                        <string>
                            <id>current_git_tag</id>
                            <values>
                                <value>@{git.closest.tag.name}</value>
                            </values>
                            <export>true</export>
                        </string>
                    </strings>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

### common field properties

All field types support the following attributes. For `initialValue` and `format`, more details are with the field types below.

| configuration attribute | allowed values                       | required | default value | function                                                                  |
|-------------------------|--------------------------------------|----------|---------------|---------------------------------------------------------------------------|
| id                      | any string                           | yes      | -             | Sets the name of the field                                                |
| skip                    | boolean (`true`,`false`)             | no       | `false`       | skip this field definition completely if set to `true`                    |
| export                  | boolean (`true`,`false`)             | no       | `false`       | export the field as a build property to the maven build cycle             |
| propertyFile            | string (filename)                    | no       | (none)        | defines a property file to associate the field with a specific property   |
| propertyNameInFile      | string (property name)               | no       | value of `id` | defines the property to assciate the field with                           |
| onMissingFile           | `ignore`, `warn`, `fail`, `create`   | no       | `fail`        | action in case the property file is missing                               |
| onMissingFileProperty   | `ignore`, `warn`, `fail`, `create`   | no       | `fail`        | action in case the property file exists but does not contain the property |
| initialValue            | field-dependent (must be legal)      | no       | (none)        | The initial value when creating a property                                |
| format                  | formatting information for the field | no       | (none)        | Formatting description for the field. Field specific                      |
| regexp                  | apply a regexp to the field          | no       | (none)        | Apply a regular expression to the field                                   |
| transformers            | comma-separated list                 | no       | (none)        | Define transformers for the field value                                   |

### property backed fields

The main functionality for the property helper plugin is to load and manipulate values from property files. Any field that is defined in the property helper
plugin can
be backed by a property in a property file and is loaded when a goal is executed. Any modified field will be saved if the `persist` attribute is set to true.

Example for a property backed string field:

```xml
<strings>
    <string>
        <id>property_field</id>
        <propertyFile>some.properties</propertyFile>
        <propertyNameInFile>field-value</propertyNameInFile>
        <onMissingFile>fail</onMissingFile>
        <onMissingFileProperty>create</onMissingFileProperty>
        <initialValue>TEST</initialValue>
    </string>
</strings>
```

The string field `property_field` will contain the value of the `field-value` property in the `some.properties` file if the file exists contains the property.
If the file is missing, the build will fail, if the file exists but does not contain the `field-value` property, it will create it and give it an initial
value of `TEST`.

To back a field with a property, the `propertyFile` attribute must be defined. If the `propertyNameInFile` attribute is not defined, the value of `id` is used.

The `onMissingFile` attribute supports the values `ignore`, `warn`, `fail` and `create`.

- `ignore` - do nothing if the file does not exist. The field will get the value from the `initialValue` field if it exists or left empty if it does not.
- `warn` - like ignore, but also log a warning to the maven output
- `fail` - fail the build with an exception
- `create` - create the configuration file

the `onMissingFileProperty` also supports the values `ignore`, `warn`, `fail` and `create`.

- `ignore` - do nothing if the property does not exist. The field will get the value from the `initialValue` field if it exists or left empty if it does not.
- `warn` - like ignore, but also log a warning to the maven output
- `fail` - fail the build with an exception
- `create` - create the property in the configuration file and use the `initialValue` field as the value if it exists, otherwise use the empty string.

### string fields

String fields are the most straightforward field type. They represent a text field that can be added to a property group or exposed directly to the maven build
properties.

String fields can derive their value from different sources:

- a file backed property attribute. If such an attribute is defined (see above), it takes highest precedence.
- value attributes. Multiple value attributes can be given and the first one defined will be used.

```xml
<strings>
    <string>
        <id>build_info</id>
        <blankIsValid>false</blankIsValid>
        <values>
            <value>${ci.build-information}</value>
            <value>UNDEFINED</value>
        </values>
        <export>true</export>
    </string>
</strings>
```

This defines a property `build_info` that will use the passed in value of the `ci.build-information` system property (e.g. by executing the build with
`-Dci.build-information=...`). If this field is unset (therefore empty), the field will have the value of `UNDEFINED`. Setting the `blankIsValid` attribute to
`false` ignores the unset `${ci.build-information}` property. Without this attribute set, the `build_info` property would be the empty string.

In addition to the common field properties, a string field has the following additional properties:

| configuration attribute | allowed values                                | required | default value | function                                                                                                      |
|-------------------------|-----------------------------------------------|----------|---------------|---------------------------------------------------------------------------------------------------------------|
| values                  | List of strings, nested as `value` attributes | no       | (none)        | defines one or more values that will be used to set the value of this field if no backing property is defined |
| blankIsValid            | `true`, `false`                               | no       | `true`        | Skips blank values when evaluating the final value of this field.                                             |
| onMissingValue          | `fail`, `warn`, `ignore`                      | no       | `fail`        | action in case the field has no defined value (neither property value nor initial value nor value definition) |

### number fields

The main use for number fields is to manage build and version numbers in the plugin. Any number field can be backed by a property in a configuration file.
In addition to this, number fields can refer to numeric sections within a property.

Simple example: A number field backed by a property:

```xml
<numbers>
    <number>
        <id>build-number</id>
        <export>true</export>
        <propertyFile>build.properties</propertyFile>
        <onMissingFile>create</onMissingFile>
        <onMissingFileProperty>create</onMissingFileProperty>
        <initialValue>0</initialValue>
    </number>
</numbers>
```

Running the `get` goal with this configuration will create a field `build-number` that is backed by a property of the same name in the `build.properties` file.
If the file or the property do not exist, they will be created and set to the value `0`.

Running the `inc` goal with the same configuration will increase the value of the field by one (default increment) and write the value back to the file on disk.
Subsequent executions will yield a sequence of numbers.

For more complex numbers such a version numbers, a field can be mapped to a numeric property piece:

The version number `1.2.3-alpha.1` will be split into a sequence of fields where every numeric/non-numeric change separates a
field: `['1', '.', '2', '.', '3', '-alpha.', '1' ]`. The version number is split into seven fields, four numeric fields and three non-numeric fields. The
property helper plugin will do this behind the scenes and reconstruct the string when a property is saved.

A numeric field can now be mapped onto part of a property. This is an example where three properties (major, minor and patch) are created:

```xml
<numbers>
    <number>
        <id>major</id>
        <export>true</export>
        <propertyFile>build.properties</propertyFile>
        <propertyNameInFile>build.version</propertyNameInFile>
        <onMissingFile>create</onMissingFile>
        <onMissingFileProperty>create</onMissingFileProperty>
        <initialValue>0.0.0</initialValue>
        <fieldNumber>0</fieldNumber>
        <increment>${major.increment}</increment>
    </number>
    <number>
        <id>minor</id>
        <export>true</export>
        <propertyFile>build.properties</propertyFile>
        <propertyNameInFile>build.version</propertyNameInFile>
        <fieldNumber>1</fieldNumber>
        <increment>${minor.increment}</increment>
    </number>
    <number>
        <id>patch</id>
        <export>true</export>
        <propertyFile>build.properties</propertyFile>
        <propertyNameInFile>build.version</propertyNameInFile>
        <fieldNumber>2</fieldNumber>
        <increment>${patch.increment}</increment>
    </number>
</numbers>
```

each of these number fields maps a single part of the full version number (`build.version` in the `build.properties` file) onto a number field. E.g. for a
property `build.version=3.14.15`, the `major` field will be `3`, the `minor` field will be `14` and the `patch` field will be `15`.

Each of the three fields uses a property (`major.increment`, `minor.increment` and `patch.increment`) which should be set to 0. To increment e.g. the patch
level of the version, run

```bash
% mvn -Dpatch.increment=1 propery-helper:inc
```

in the project. This will load the properties file, increment the patch field of the `build.version` property and store it back on disk.

In addition to the common field properties, a number field has the following additional properties:

| configuration attribute | allowed values   | required | default value | function                                                                                                                                                                                                 |
|-------------------------|------------------|----------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| fieldNumber             | positive integer | no       | -             | Selects a component of the underlying property as the number. The property is split along numeric/non-numeric boundaries and enumerated from left (leftmost field is 0). Only numeric fields are counted |
| increment               | integer          | no       | 1             | The numeric amount by which a field is changed when calling the `inc` goal. Can be any integer number                                                                                                    |

In addition, unlike all other field types, the default value for a numeric field is not undefined but `0`.


### date fields

Date fields contain date information such as build dates, timestamps etc. Date fields depend heavily on formatting. If no format is given, the date is written
in [ISO_DATE_TIME](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/format/DateTimeFormatter.html#ISO_DATE_TIME) with timezone format.

The following example creates a timestamp in the default format and exports it as a build property:

```xml
<dates>
    <date>
        <id>simple</id>
        <export>true</export>
    </date>
</dates>
```

When loading a date field from a backing property, the value in the property can be a long value (milliseconds since the epoch, `1970-01-01T00:00:00.000Z`) or
a formatted date and time value that will be read with a parser that uses the same format definition as the formatter.

Date fields also support a `value` attributes, which is a long value (milliseconds since the epoch).

When formatting a date (which also defines the date parser), it supports [all letters and symbols](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns) that [DateTimeFormatter.ofPattern()](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/format/DateTimeFormatter.html#ofPattern(java.lang.String)) supports.

In addition to the common field properties, a date field has the following additional properties:

| configuration attribute | allowed values                                                                                                                                | required | default value       | function                                                   |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|----------|---------------------|------------------------------------------------------------|
| timezone                | any value supported by [ZoneId.of()](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/ZoneId.html#of(java.lang.String)) | no       | system timezone     | Sets the timezone for the date field                       |
| value                   | long value                                                                                                                                    | no       | current system time | Sets the value for the field if not loaded from a property |


### uuid fields

An UUID field contains a unique identifier that can be used as e.g. build identifier, as id for a software package or some other unique id.

In addition to the common field properties, a uuid field has the following additional properties:

| configuration attribute | allowed values          | required | default value     | function                                                   |
|-------------------------|-------------------------|----------|-------------------|------------------------------------------------------------|
| value                   | uuid 36 character value | no       | random UUID value | Sets the value for the field if not loaded from a property |

### macro fields

In addition to all the field types above (string, number, date, uuid), a macro field executes custom code and provides the value as a field.

In this example, a macro "TestRunCount" has been packaged in the `test-support-1.0.jar`:

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>property-helper-maven-plugin</artifactId>
    <dependencies>
        <dependency>
            <groupId>some.company</groupId>
            <artifactId>test-support</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
    <configuration>
        <macros>
            <macro>
                <id>test-run-count</id>
                <macroType>TestRunCount</macroType>
                <export>true</export>
            </macro>
        </macros>
    </configuration>
</plugin>
```

The code must implement the `org.basepom.mojo.propertyhelper.macros.MacroType` interface and use the `@Component(role = MacroType.class, hint = "TestRunCount")` Plexus
annotation.

Alternatively, it can also use the `macroClass` attribute and does not need to use the Plexus annotation. The code must still implement the `org.basepom.mojo.propertyhelper.macros.MacroType` interface. Biggest drawback of this configuration is that the implementation class is exposed to the build system.

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>property-helper-maven-plugin</artifactId>
    <dependencies>
        <dependency>
            <groupId>some.company</groupId>
            <artifactId>test-support</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
    <configuration>
        <macros>
            <macro>
                <id>test-run-count</id>
                <macroClass>com.some.company.test.TestRunCountImplementation</macroClass>
                <export>true</export>
            </macro>
        </macros>
    </configuration>
</plugin>
```

The Macro field type is still experimental and might change from version to version.

In addition to the common field properties, a macro field has the following additional properties:

| configuration attribute | allowed values                            | required | default value | function                                                                                                                                       |
|-------------------------|-------------------------------------------|----------|---------------|------------------------------------------------------------------------------------------------------------------------------------------------|
 | macroType               | string, must be a valid Plexus identifier | no       | (none)        | provides a "component hint" to look up the specific macro from the plugin classpath. If this attribute is present, the `macroClass` is ignored |
| macroClass              | string, must be a valid Java class name   | no       | (none)        | provides a class name to load. Is only used if `macroType` is not present                                                                      |
| properties              | set of key/value property pairs           | no       | (none)        | provides a set of macro specific properties to configure the specific invocation or the macro itself                                           |


### field formatting

String, number, uuid and macro fields support the `format` attribute to format its output. Date fields use specific formatting as described above.

The `format` attribute is used to with [String#format()](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html#format(java.lang.String,java.lang.Object...)) and most be formatted accordingly. The value field must contain a single `%s` placeholder and can use [all formatting characters](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Formatter.html#syntax) related to this placeholder.

Note that for all fields (even number fields), the placeholder must be `%s` for a string!


### field regular expressions

All fields support a `regexp` attribute that can be used to apply a regular expression to the output of the field. Regular expressions are applied after formatting and before transformations.

A regular expression *must* match all characters of a field (start with `^` and end with `$`) and contain at least one capture group.

This example captures the numeric part from a build tag by using a regular expression:

```xml
<strings>
    <string>
        <id>version_from_tag</id>
        <values>
            <value>${ci.build-tag}</value>
        </values>
        <regexp>^build-tag-(\d+)$</regexp>
    </string>
</strings>
```

### property groups

For simple field definitions or properties, a field definition is sufficient. However, for more advanced use cases, such as activation based on build conditions or more complex properties that combine multiple field types, property groups need to be used.

From the initial example:

```xml
<configuration>
    <propertyGroups>
        <propertyGroup>
            <id>build-tag</id>
            <properties>
                <property>
                    <name>build.tag</name>
                    <value>@{build-tag}-@{build-number}-@{build-date}</value>
                </property>
            </properties>
        </propertyGroup>
    </propertyGroups>
    <dates>
        <date>
            <id>build-date</id>
            <timezone>UTC</timezone>
            <format>yyyyMMdd_HHmmss</format>
        </date>
    </dates>
    <uuids>
        <uuid>
            <id>build-tag</id>
        </uuid>
    </uuids>
    <numbers>
        <number>
            <id>build-number</id>
            <propertyNameInFile>build.number</propertyNameInFile>
            <propertyFile>${user.home}/build.properties</propertyFile>
            <onMissingFile>create</onMissingFile>
            <onMissingFileProperty>create</onMissingFileProperty>
            <initialValue>1</initialValue>
        </number>
    </numbers>
</configuration>
```

This configuration defines a single property group `build-tag` with a single property `build.tag`, which is exposed to the maven build properties.

This property is defined as the combination of three separate fields, a date, a uuid and a persistent build number.

Similar to the different field types, property definitions as part of a property group can contain direct (`${ ... }`) property references or "late binding" property references using `@{...}`.

Property groups can be activated specifically if a build is a snapshot or release build.

A property group can have the following attributes:

| configuration attribute | allowed values               | required | default value | function                                                                         |
|-------------------------|------------------------------|----------|---------------|----------------------------------------------------------------------------------|
| id                      | any string                   | yes      | -             | Sets the name of the field                                                       |
| activeOnRelease         | `true`, `false`              | no       | `true`        | Whether to activate this property group if the current build is a release build  |
| activeOnSnapshot        | `true`, `false`              | no       | `true`        | Whether to activate this property group if the current build is a snapshot build |
| onDuplicateProperty     | `ignore`, `warn`, `fail`     | no       | `fail`        | Action when a property is defined multiple times                                 |
| onMissingField          | `ignore`, `warn`, `fail`     | no       | `fail`        | Action when a referenced field  is not defined                                   |
| properties              | list of property definitions | no       | (none)        | Property definitions that are part of this property group                        |

A property definition can have the following attributes:

| configuration attribute | allowed values       | required | default value | function                                                                                                                                                                 |
|-------------------------|----------------------|----------|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| name                    | any string           | yes      | -             | sets the name of the field. Unlike all other objects, this attribute is not called `id` to allow multiple definitions of the same property in different property groups. |
| value                   | any string           | yes      | -             | sets the value for the property field. This is usually a combination of placeholders (`${...}` and `@{...}`) and text.                                                   |
| transformers            | comma-separated list | no       | (none)        | Define transformers for the property value                                                                                                                               |

The "late binding" property references (`@{...}`) in a property definition can reference all defined fields independent of whether they are exposed as a maven build property or not.


### transformers

The property helper plugin defines a set of transformers that can be applied to field and property values. Transformers are configured as a comma-separated list
and will be executed left-to-right.

The following transformers are defined:

- `lowercase` - transform all alphabetic characters into their lowercase equivalent in the current locale
- `uppercase` - transform all alphabetic characters into their uppercase equivalent in the current locale
- `remove_whitespace` - remove all whitespace characters
- `underscore_for_whitespace` - replace all whitespace characters with `_`
- `dash_for_whitespace` - replace all whitespace characters with `-`
- `use_underscore` - replace all `-` characters with `_`
- `use_dash` - replace all `_` characters with `-`
- `trim` - remove all leading and trailing whitespace


### complete configuration reference

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>property-helper-maven-plugin</artifactId>
    <configuration>
        <skip>true|false</skip>

        <persist>true|false</persist>

        <onDuplicateField>ignore|warn|fail</onDuplicateField>

        <onMissingProperty>ignore|warn|fail</onMissingProperty>

        <propertyGroups>
            <propertyGroup>
                <id>...</id>
                <activeOnRelease>true|false</activeOnRelease>
                <activeOnSnapshot>true|false</activeOnSnapshot>
                <onDuplicateProperty>ignore|warn|fail</onDuplicateProperty>
                <onMissingField>ignore|warn|fail</onMissingField>
                <properties>
                    <property>
                        <name>...</name>
                        <value>...</value>
                        <transformers>...</transformers>
                    </property>
                    ...
                </properties>
            </propertyGroup>
            ...
        </propertyGroups>

        <activeGroups>
            <activeGroup>...</activeGroup>
            ...
        </activeGroups>

        <dates>
            <date>
                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>

                <value>...</value>
                <timezone>...</timezone>

                <format>...</format>
                <regexp>...</regexp>
                <transformers>...</transformers>

                <propertyFile>...</propertyFile>
                <propertyNameInFile>...</propertyNameInFile>
                <initialValue>...</initialValue>
                <onMissingFile>ignore|warn|fail|create</onMissingFile>
                <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
                <onMissingProperty>ignore|warn|fail</onMissingProperty>
            </date>
            ...
        </dates>

        <macros>
            <macro>
                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>

                <macroType>...</macroType>
                <macroClass>...</macroClass>
                <properties>
                    <some-name>some-value</some-name>
                    ...
                </properties>

                <format>...</format>
                <regexp>...</regexp>
                <transformers>...</transformers>

                <propertyFile>...</propertyFile>
                <propertyNameInFile>...</propertyNameInFile>
                <initialValue>...</initialValue>
                <onMissingFile>ignore|warn|fail|create</onMissingFile>
                <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
                <onMissingProperty>ignore|warn|fail</onMissingProperty>
            </macro>
            ...
        </macros>

        <numbers>
            <number>
                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>

                <fieldNumber>...</fieldNumber>
                <increment>...</increment>
                <format>...</format>
                <regexp>...</regexp>
                <transformers>...</transformers>

                <propertyFile>...</propertyFile>
                <propertyNameInFile>...</propertyNameInFile>
                <initialValue>...</initialValue>
                <onMissingFile>ignore|warn|fail|create</onMissingFile>
                <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
                <onMissingProperty>ignore|warn|fail</onMissingProperty>
            </number>
            ...
        </numbers>

        <strings>
            <string>
                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>

                <values>
                    <value>...</value>
                    ...
                </values>
                <blankIsValid>true|false</blankIsValid>
                <onMissingValue>ignore|warn|fail</onMissingValue>
                <format>...</format>
                <regexp>...</regexp>
                <transformers>...</transformers>

                <propertyFile>...</propertyFile>
                <propertyNameInFile>...</propertyNameInFile>
                <initialValue>...</initialValue>
                <onMissingFile>ignore|warn|fail|create</onMissingFile>
                <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
                <onMissingProperty>ignore|warn|fail</onMissingProperty>
            </string>
            ...
        </strings>

        <uuids>
            <uuid>
                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>

                <value>...</value>
                <format>...</format>
                <regexp>...</regexp>
                <transformers>...</transformers>

                <propertyFile>...</propertyFile>
                <propertyNameInFile>...</propertyNameInFile>
                <initialValue>...</initialValue>
                <onMissingFile>ignore|warn|fail|create</onMissingFile>
                <onMissingFileProperty>ignore|warn|fail|create</onMissingFileProperty>
                <onMissingProperty>ignore|warn|fail</onMissingProperty>
            </uuid>
            ...
        </uuids>
    </configuration>
</plugin>
```
