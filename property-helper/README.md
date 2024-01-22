# property-helper Maven Plugin

Define, create and manipulate properties for the maven build cycle. This plugin can

- create properties to e.g. represent build versions, identifiers, switches etc.
- combine different type of fields into properties and export them to the maven build cycle
- manipulate existing properties

## Configuration

The plugin defines different elements that are combined to form properties:

- `string` fields  - provides a string component
- `number` fields - provide a structured number component
- `date` fields - provide a formatted date component
- `uuid` fields - provide a uuid value component
- `macro` fields - custom code to provide additional components

Each field can be exported directly as a property or combined in a property group. 

A property group can be activated for release or snapshot builds and aggregates fields into a single property that gets exposed.

Example:

```xml
<configuration>
    <propertyGroups>
        <propertyGroup>
            <id>build-tag</id>
            <properties>
                <property>
                    <name>build.tag</name>
                    <value>#{build-tag}-#{build-number}-#{build-date}</value>
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
            <onMissingProperty>create</onMissingProperty>
            <initialValue>1</initialValue>
        </number>
    </numbers>
</configuration>
```

This configuration defines three elements, `build-date`, `build-tag` and `build-number`. They are combined to form a property called `build.tag` which can be referred to in following build steps as `${build.tag}`. 

The `build-date` element is a date element. As there is no value given or loaded from a file, it uses the current time as its value. By setting the timezone to `UTC`, this timezone will be used.

`build-tag` is a UUID value. As no seed value is given and the field is also not loaded from a file, it creates a random UUID.

Finally, `build-number` is a number field. It is backed by a property in a property file `build.properties` (which e.g. lives in the home directory of the CI user). Its value is loaded from the `build.number` property in the file (and if the file or the property do not exist, it will create those).

All three values combined form the `build.tag` property which is exported as a property. 




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
                <onMissingProperty>ignore|warn|fail</onMissingProperty>
                <properties>
                    <property>
                        <name></name>
                        <value></value>
                        <transformers>...</transformers>
                    </property>
                </properties>
            </propertyGroup>
        </propertyGroups>
        <activeGroups>
            <activeGroup>...</activeGroup>
        </activeGroups>
        <dates>
            <date>
                <timezone></timezone>
                <value></value>

                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>
                <propertyNameInFile></propertyNameInFile>
                <propertyFile></propertyFile>
                <onMissingFile></onMissingFile>
                <onMissingProperty></onMissingProperty>
                <initialValue></initialValue>
                <format></format>
                <transformers>...</transformers>
            </date>
        </dates>
        <macros>
            <macro>
                <macroType></macroType>
                <macroClass></macroClass>
                <properties>
                    <property></property>
                </properties>

                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>
                <propertyNameInFile></propertyNameInFile>
                <propertyFile></propertyFile>
                <onMissingFile></onMissingFile>
                <onMissingProperty></onMissingProperty>
                <initialValue></initialValue>
                <format></format>
                <transformers>...</transformers>
            </macro>
        </macros>
        <numbers>
            <number>
                <fieldNumber></fieldNumber>
                <increment></increment>

                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>
                <propertyNameInFile></propertyNameInFile>
                <propertyFile></propertyFile>
                <onMissingFile></onMissingFile>
                <onMissingProperty></onMissingProperty>
                <initialValue></initialValue>
                <format></format>
                <transformers>...</transformers>

            </number>
        </numbers>
        <strings>
            <string>
                <values>
                    <value></value>
                </values>
                <blankIsValid>true|false</blankIsValid>
                <onMissingValue>ignore|warn|fail</onMissingValue

                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>
                <propertyNameInFile></propertyNameInFile>
                <propertyFile></propertyFile>
                <onMissingFile></onMissingFile>
                <onMissingProperty></onMissingProperty>
                <initialValue></initialValue>
                <format></format>
                <transformers>...</transformers>

            </string>
        </strings>
        <uuids>
            <uuid>
                <value></value>

                <id>...</id>
                <skip>true|false</skip>
                <export>true|false</export>
                <propertyNameInFile></propertyNameInFile>
                <propertyFile></propertyFile>
                <onMissingFile></onMissingFile>
                <onMissingProperty></onMissingProperty>
                <initialValue></initialValue>
                <format></format>
                <transformers>...</transformers>
            </uuid>
        </uuids>
    </configuration>
</plugin>
```
