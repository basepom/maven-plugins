# Using existing maven build properties

The property helper plugin can reference existing maven properties when defining fields.

The most common way to do so is the familiar `${...}` syntax. E.g. the following example defines a field that is set to the groupId and artifactId of the project:

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

Due to the way the Maven POM is evaluated and `${...}` variables are evaluated, build properties that are known at the beginning of the Maven lifecycle are
immediately evaluated and if e.g. a plugin later changes their value, the plugin will not pick them up.

Any build properties that are defined and exported by other plugins (e.g. build information from plugins like the git-commit-id) should be accessed using
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
