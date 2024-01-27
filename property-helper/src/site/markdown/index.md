# property-helper Maven Plugin

Define, create and manipulate properties for the maven build cycle. This plugin can

- create properties to e.g. represent build versions, identifiers, switches etc.
- combine different type of fields into properties and export them to the maven build cycle
- manipulate existing properties

* [Plugin Configuration](general_config.html)
* [Using Maven build properties](using_build_properties.html)
* [Field definitions](fields.html)
* [Formatting, Regular Expressions and Transformers](formatting_regexp.html)
* [Property groups](property_groups.html)

The plugin has two goals:

- [property-helper:get](get-mojo.html) - load and save property files, define fields, combine fields to property groups and new properties, export these to the maven build
- [property-helper:inc](inc-mojo.html) - increment number field values by loading, manipulating and saving property files

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

