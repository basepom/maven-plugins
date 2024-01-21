# property-helper Maven Plugin

Define, create and manipulate properties for the maven build cycle. This plugin can

- create properties to e.g. represent build versions, identifiers, switches etc.
- combine different type of fields into properties and export them to the maven build cycle
- manipulate existing properties

## Configuration

The plugin defines different elements that are combined to form properties:

- `string` fields 



```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>property-helper-maven-plugin</artifactId>
    <configuration>
        <skip>true|false</skip>
        <persist>true|false</persist>
        <onDuplicateProperty>ignore|warn|fail</onDuplicateProperty>
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
                <propertyName></propertyName>
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
                <propertyName></propertyName>
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
                <propertyName></propertyName>
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
                <propertyName></propertyName>
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
                <propertyName></propertyName>
                <propertyFile></propertyFile>
                <onMissingFile></onMissingFile>
                <onMissingProperty></onMissingProperty>
                <initialValue></initialValue>
                <format></format>
                <transformers>...</transformers>
            </uuid>
        </uuids>
    </configuration>

```
