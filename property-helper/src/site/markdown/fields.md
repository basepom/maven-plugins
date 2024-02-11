## Common field properties

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

## Property backed fields

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

## Field types

The property helper plugin supports five field types:

- `string` fields - provides a string field
- `number` fields - provide a structured number field
- `date` fields - provide a formatted date field
- `uuid` fields - provide a uuid value field
- `macro` fields - custom code to provide specific fields


### String fields

String fields are the most straightforward field type. They represent a text field that can be added to a property group or exposed directly to the maven build properties.

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

### Number fields

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


### Date fields

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


### UUID fields

A UUID field contains a unique identifier that can be used as e.g. build identifier, as id for a software package or some other unique id.

In addition to the common field properties, any uuid field has the following additional properties:

| configuration attribute | allowed values          | required | default value     | function                                                   |
|-------------------------|-------------------------|----------|-------------------|------------------------------------------------------------|
| value                   | uuid 36 character value | no       | random UUID value | Sets the value for the field if not loaded from a property |


When the `${project.build.outputTimestamp}` property is set, a reproducible build is requested. In that case, as long as the same configuration and the same timestamp is used, the random value for an UUID field will be the same.


### Macro fields

Unlike all the other field types above (`string`, `number`, `date`, `uuid`), a macro field does not define a specific structure but executes custom code and provides the result as a field value.

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
