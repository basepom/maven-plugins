## Property groups definition

For simple field definitions or properties, a field definition is sufficient. However, for more advanced use cases, such as activation based on build conditions
or more complex properties that combine multiple field types, property groups need to be used.

Example:

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

Similar to the different field types, property definitions as part of a property group can contain direct (`${ ... }`) property references or "late binding"
property references using `@{...}`.

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

The "late binding" property references (`@{...}`) in a property definition can reference all defined fields independent of whether they are exposed as a maven
build property or not.

## Property group activation

By default, all defined property groups are always active. However, each property group provides attributes to control activation for release (non-SNAPSHOT) and
snapshot builds:

```xml

<configuration>
    <propertyGroups>
        <propertyGroup>
            <id>release-tag</id>
            <activateOnSnapshot>false</activateOnSnapshot>
            <properties>
                <property>
                    <name>release.tag</name>
                    <value>RELEASE</value>
                </property>
            </properties>
        </propertyGroup>
        <propertyGroup>
            <id>snapshot-tag</id>
            <activateOnRelease>false</activateOnRelease>
            <properties>
                <property>
                    <name>release.tag</name>
                    <value>@{release.id}</value>
                </property>
            </properties>
        </propertyGroup>
    </propertyGroups>
    <uuids>
        <uuid>
            <id>release.id</id>
        </uuid>
    </uuids>
</configuration>
```

This configuration provides a property `release.tag` for each build. If the build is a release build, the value will be `RELEASE`, otherwise it will be a UUID
value.

While the two property groups define the same property (`release.tag`), they do not clash because one is only active when a release is built and the other only
when a snapshot is built.

For more complex use cases, it is also possible to activate specific groups explicitly using the `activeGroups` attribute:

```xml

<project>
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.basepom.maven</groupId>
                    <artifactId>property-helper-maven-plugin</artifactId>
                    <configuration>
                        <activeGroups />
                        <propertyGroups>
                            <propertyGroup>
                                <id>release-tag</id>
                                <properties>
                                    <property>
                                        <name>release.tag</name>
                                        <value>RELEASE</value>
                                    </property>
                                </properties>
                            </propertyGroup>
                            <propertyGroup>
                                <id>snapshot-tag</id>
                                <properties>
                                    <property>
                                        <name>release.tag</name>
                                        <value>@{release.id}</value>
                                    </property>
                                </properties>
                            </propertyGroup>
                        </propertyGroups>
                        <uuids>
                            <uuid>
                                <id>release.id</id>
                            </uuid>
                        </uuids>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
    <profiles>
        <profile>
            <id>release</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.basepom.maven</groupId>
                        <artifactId>property-helper-maven-plugin</artifactId>
                        <configuration>
                            <activeGroups>
                                <activeGroup>release-tag</activeGroup>
                            </activeGroups>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>
        <profile>
            <id>snapshot</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.basepom.maven</groupId>
                        <artifactId>property-helper-maven-plugin</artifactId>
                        <configuration>
                            <activeGroups>
                                <activeGroup>snapshot-tag</activeGroup>
                            </activeGroups>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
</project>
```

In this example, multiple groups are defined as part of the plugin management section and the actual plugin execution is controlled by setting
the needed active group in a maven profile. This allows consolidation of configuration in one place and then group activation as needed.

Note that there is a difference between no `activeGroups` attribute present and empty `activeGroup` attribute:

By default (no `activeGroups` attribute present), all groups are active.

Adding an empty `<activeGroup />` attribute to the configuration turns off all groups (no property group is active).
