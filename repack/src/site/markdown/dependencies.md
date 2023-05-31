# Including and excluding dependencies from the final artifact

The `repack` plugin uses four properties to control which dependencies will be included in the final artifact:

| property                    | function                                                                                                                                                                                                               |
|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `includedDependencies`      | defines which artifact dependencies are included in the final archive. The default is the empty list; this includes all dependencies scope that the artifact has declared.                                             |
| `excludedDependencies`      | defines which artifact dependencies are excluded from the final archive. The default is the empty list; no dependencies are excluded.                                                                                  |
| `optionalDependencies`      | defines which artifacts in `optional` scope are included in the final archive, even if the `includeOptional` option is set to `false`.                                                                                 |

These properties define *matchers*, they do *not* define actual dependencies. The plugin will only ever package dependencies that have been declared by the original artifact. It can not add any dependency that has not been declared before.

The following boolean settings also affect what gets packaged:

| property               | function                                                  |
|------------------------|-----------------------------------------------------------|
| `includeOptional`      | include dependencies that have been declared as optional. |
| `includeProvidedScope` | include dependencies that are in `provided` scope.        |
| `includeSystemScope`   | include dependencies that are in `system` scope.          |


By default, all dependencies that are in `compile` and `runtime` scope will be packaged. The `includeProvidedScope` and `includeSystemScope` will also add dependencies in the `provided` or `system` scope respectively.

When the `includedDependencies` property contains any matcher, *only* dependencies that are matched will be included.

Any matcher in the `excludedDependencies` property will prevent any dependency that is matched from being included.

Optional dependencies can be included in two ways: By setting the `includeOptional` property to `true`, *all* dependencies in `optional` scope will be added to the final artifact. Otherwise, if the property is set to `false` (the default), matchers can be defined in the `optionalDependencies` property to include specific dependencies that are in `optional` scope.

## Examples

All the examples use this dependency list for a project: 

``` xml
<dependencies>
    <dependency>
        <groupId>org.foo</groupId>
        <artifactId>dep1</artifactId>
        <version>1.0</version>
    </dependency>
    <dependency>
        <groupId>com.bar</groupId>
        <artifactId>dep2</artifactId>
        <version>1.0</version>
        <classifier>special</classifier>
    </dependency>
    <dependency>
        <groupId>org.foo</groupId>
        <artifactId>runtime-dep</artifactId>
        <version>1.0</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.foo</groupId>
        <artifactId>optional1</artifactId>
        <version>1.0</version>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>com.bar</groupId>
        <artifactId>optional2</artifactId>
        <version>1.0</version>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.bar</groupId>
        <artifactId>provided-dep</artifactId>
        <version>1.0</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>com.local</groupId>
      <artifactId>system-dep</artifactId>
      <version>1.0</version>
      <scope>system</scope>
      <systemPath>/usr/local/lib/system-dep.jar</systemPath>
    </dependency>
</dependencies>
```

Without any configuration, the `org.foo:dep1`, `com.bar:dep2` and `org.foo:runtime-dep` artifacts will be packaged into the final archive. All other dependencies are excluded either by scope or because they are optional.

### Including artifacts

By default, all dependency artifacts are included. The following example only includes specific artifacts, that match `org.foo:*`. Note that the `*` was not actually necessary, just using `org.foo` would have worked as well. Only `org.foo:dep1` and `org.foo:runtime-dep` will be in the resulting artifact.

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>repack-maven-plugin</artifactId>
    <configuration>
        <includedDependencies>
            <includedDependency>org.foo:*</includedDependency>
        </includedDependencies>
    </configuration>
</plugin>
```

### Excluding artifacts

This example adds an exclude for any dependency that matches `org.foo:dep1`. Only `com.bar:dep2` and `org.foo:runtime-dep` will be in the resulting artifact.

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>repack-maven-plugin</artifactId>
    <configuration>
        <excludedDependencies>
            <excludedDependency>org.foo:dep1</excludedDependency>
        </excludedDependencies>
    </configuration>
</plugin>
```

### Including optional artifacts

By default, artifacts that are marked as optional are not included in the final archive. In the following example, all optional artifacts are pulled in by setting the `includeOptional`. The resulting archive will contain `org.foo:dep1`, `com.bar:dep2`, `org.foo:runtime-dep`, `org.foo:optional1` and `com.bar:optional2`.

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>repack-maven-plugin</artifactId>
    <configuration>
        <includeOptional>true</includeOptional>
    </configuration>
</plugin>
```

It is also possible to only include a subset of the optional artifacts. The next example only includes the `org.foo:optional1` artifact in addition to `org.foo:dep1`, `com.bar:dep2` and `org.foo:runtime-dep`.

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>repack-maven-plugin</artifactId>
    <configuration>
        <optionalDependencies>
            <optionalDependency>org.foo:optional1</optionalDependency>
        </optionalDependencies>
    </configuration>
</plugin>
```

When combining with excludes, the excludes will be applied after included artifacts have been selected. The next example will omit all dependencies that match `com.bar`, so only `org.foo:dep1`, `org.foo:runtime-dep` and `org.foo:optional1` will be in the resulting archive.

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>repack-maven-plugin</artifactId>
    <configuration>
        <includeOptional>true</includeOptional>
        <excludedDependencies>
            <excludedDependency>com.bar</excludedDependency>
        </excludedDependencies>
    </configuration>
</plugin>
```

### `provided` and `system` scope

Note: `system` scope is deprecated and may not be supported in future Apache Maven versions.

The special configuration options `includeProvidedScope` and `includeSystemScope` allow the additional inclusion of artifacts that are normally not part of a distribution.
In this example, the org.foo:dep1`, `com.bar:dep2`, `org.foo:runtime-dep`, `com.bar:provided-dep` and `com.local:system.dep` artifacts are packages into the resulting archive.

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>repack-maven-plugin</artifactId>
    <configuration>
        <includedProvidedScope>true</includedProvidedScope>
        <includeSystemScope>true</includeSystemScope>
    </configuration>
</plugin>
```

Similar to optional dependencies, exclusions are applied after all artifacts are enumerated. In this example, even though the `includeProvided` property is set to `true`,
the `com.bar:provided-dep` is not included because it is matched by the `excludedDependencies` setting:

```xml
<plugin>
    <groupId>org.basepom.maven</groupId>
    <artifactId>repack-maven-plugin</artifactId>
    <configuration>
        <includedProvidedScope>true</includedProvidedScope>
        <excludedDependencies>
            <excludedDependency>com.bar</excludedDependency>
        </excludedDependencies>
    </configuration>
</plugin>
```

