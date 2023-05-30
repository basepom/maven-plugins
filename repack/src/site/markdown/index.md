## Repack maven plugin

This plugin can repackage all dependencies and classes for a JVM application into a single executable.

## When to use this plugin

Create a single artifact that can be executed using the `java -jar` command. This plugin will take the dependencies, package them up and create a new "fat" jar that can be deployed as a single archive.

## How to use this plugin

The repack plugin can be just attached to the build lifecycle to package artifacts:

```xml
<plugins>
    <plugin>
        <groupId>org.basepom.maven</groupId>
        <artifactId>repack-maven-plugin</artifactId>
        <version>1.0.0</version>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>repack</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
```

Without any configuration, it will create a new artifact with the same name as the original artifact, use the `repacked` classifier and not add or modify any main class manifest entry.

### Optional configuration options

```xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.basepom.maven</groupId>
                <artifactId>repack-maven-plugin</artifactId>
                <version>1.0.0</version>
                <configuration>
                    <!-- common options -->
                    <skip>true|false</skip>
                    <quiet>true|false</quiet>
                    <outputDirectory>..</outputDirectory>
                    <mainClass>... class name containing main method ...</mainClass>
                    <report>true|false</report>
                    
                    <!-- control name of final artifact -->
                    <finalName>... final name of the artifact ...</finalName>
                    <repackClassifier>repacked</repackClassifier>
                    <attachRepackedArtifact>true|false</attachRepackedArtifact>
                    
                    <!-- controls content of the final artifact -->
                    <includeOptional>true|false</includeOptional>
                    <includeProvidedScope>true|false</includeProvidedScope>
                    <includeSystemScope>true|false</includeSystemScope>
                    
                    <!-- advanced settings -->
                    <outputTimestamp>yyyy-MM-dd'T'HH:mm:ssXXX</outputTimestamp>
                    <layout>JAR|WAR|ZIP|DIR|NONE</layout>
                    <layoutFactory>...</layoutFactory>
                    
                    <!-- include/exclude and control dependencies -->
                    <includedDependencies>
                        <includedDependency>..</includedDependency>
                        <includedDependency>..</includedDependency>
                    </includedDependencies>
                    <excludedDependencies>
                        <excludeDependency>...</excludeDependency>
                        <excludeDependency>...</excludeDependency>
                    </excludedDependencies>
                    <optionalDependencies>
                        <optionalDependency>..</optionalDependency>
                        <optionalDependency>..</optionalDependency>
                    </optionalDependencies>
                    <runtimeUnpackedDependencies>
                        <runtimeUnpackedDependency>..</runtimeUnpackedDependency>
                        <runtimeUnpackedDependency>..</runtimeUnpackedDependency>
                    </runtimeUnpackedDependencies>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

| Option                      | Type                                        | Default                            | Function                                                                                                                                                                                                                                                                                                                                                |
|-----------------------------|---------------------------------------------|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| attachRepackedArtifact      | boolean                                     | `true`                             | Attaches the artifact created by the plugin to the maven build lifecycle.                                                                                                                                                                                                                                                                               |
| excludedDependencies        | set of `excludedDependency` elements        | <empty>                            | List of dependencies that should be excluded from packaging into the final archive.                                                                                                                                                                                                                                                                     | 
| finalName                   | string                                      | `${project.build.finalName}`       | Sets the name of the final artifact.                                                                                                                                                                                                                                                                                                                    |
| includedDependencies        | set of `includedDependency` elements        | <empty>                            | List of dependencies that should be included into the final archive.                                                                                                                                                                                                                                                                                    | 
| includeOptional             | boolean                                     | `false`                            | If `true`, any dependency declared as `optional` is also packaged.                                                                                                                                                                                                                                                                                      | 
| includeProvidedScope        | boolean                                     | `false`                            | If `true`, any dependency declared in `provided` scope is also packaged.                                                                                                                                                                                                                                                                                | 
| includeSystemScope          | boolean                                     | `false`                            | If `true`, any dependency declared in `system` scope is also packaged.                                                                                                                                                                                                                                                                                  | 
| layout                      | one of `JAR`, `WAR`, `ZIP`, `DIR` or `NONE` | `JAR`                              | The layout of the final archive. Default is `JAR`.                                                                                                                                                                                                                                                                                                      | 
| layoutFactory               | class name (string)                         | <unset>                            | Use a custom layout factory to define the archive layout. This is an advanced option. See [the spring boot documentation](https://docs.spring.io/spring-boot/docs/2.6.15/maven-plugin/reference/htmlsingle/#packaging.examples.custom-layout) for details. Unlike the spring-boot plugin, setting a layout factory overrides any direct layout setting. | 
| mainClass                   | class name (string)                         | <unset>                            | The main class for the final artifact.                                                                                                                                                                                                                                                                                                                  | 
| optionalDependencies        | set of `optionalDependency` elements        | <empty>                            | List of optional dependencies that should be included, even if `includeOptional` is set to `false`.                                                                                                                                                                                                                                                     | 
| outputDirectory             | filesystem folder (string)                  | `${project.build.directory}`       | The folder into which the final artifact is written. Defaults to the build output directory.                                                                                                                                                                                                                                                            | 
| outputTimestamp             | timestamp value (string)                    | `${project.build.outputTimestamp}` | A timestamp for the final artifact that can be used to create reproducible builds. Must be formatted as an ISO8601 (`yyyy-MM-dd'T'HH:mm:ssXXX`) timestamp or an integer number representing the seconds since the epoch.                                                                                                                                | 
| quiet                       | boolean                                     | `false`                            | Only report warnings and errors if set to `true`.                                                                                                                                                                                                                                                                                                       | 
| repackClassifier            | string                                      | repacked                           | The classifier for the final artifact.                                                                                                                                                                                                                                                                                                                  | 
| report                      | boolean                                     | `true`                             | If `true`, display a summary report of all packaged and ignored dependencies and their scope.                                                                                                                                                                                                                                                           | 
| runtimeUnpackedDependencies | set of `runtimeUnpackedDependency` elements | <empty>                            | List of dependencies that do not function within the packaged jar and must be unpacked first.                                                                                                                                                                                                                                                           | 
| skip                        | boolean                                     | `false`                            | If `true`, skip plugin execution.                                                                                                                                                                                                                                                                                                                       | 
 #### Specifying dependencies

The `includedDependencies`, `excludedDependencies`, `optionalDependencies` and `runtimeUnpackDependencies` parameters all define elements for dependency matchers that are applied to the dependencies of the main artifact. The matchers can only include or exclude dependencies that are defined by the artifact, they can not add any additional dependencies.

| matcher                     | function                                                                                                                                                                                                               | 
|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `includedDependencies`      | defines which artifact dependencies are included in the final archive. The default is the empty list; this includes all dependencies scope that the artifact has declared.                                             |
| `excludedDependencies`      | defines which artifact dependencies are excluded from the final archive. The default is the empty list; no dependencies are excluded.                                                                                  |
| `optionalDependencies`      | defines which artifacts in `optional` scope are included in the final archive, even if the `includeOptional` option is set to `false`.                                                                                 |
| `runtimeUnpackDependencies` | defines which artifacts are unpacked from the archive at runtime and added to the classpath using the standard java class loader. This is required for some dependencies that do not work inside the repacked archive. |

A dependency is defined as `<group-id>:<artifact-id>:<type>:<classifier>`. Only the group id is required, all other elements can be omitted or left empty.

| field       | default value | function                                                                 |
|-------------|---------------|--------------------------------------------------------------------------|
| group-id    | <unset>       | Matches the group id. The value `*` matches any group id.                |
| artifact-id | `*`           | Matches the artifact id. The default value, `*` matches any artifact id. |
| type        | `jar`         | The artifact type.                                                       |
| classifier  | <empty>       | Matches a dependency classifier. Most jars do not use classifiers.       |

See the plugin goals documentation for additional details on how to use this plugin.
