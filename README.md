# Maven Plugins

Assorted plugins that are useful in Maven projects

| plugin                         |                                                     status                                                      | description                                                                                                                                                                                                                                  |
|--------------------------------|:---------------------------------------------------------------------------------------------------------------:|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `property-helper-maven-plugin` |     ![Maven Central](https://img.shields.io/maven-central/v/org.basepom.maven/property-helper-maven-plugin)     | creates and manipulates properties, strings, dates, uuid etc. in the build process                                                                                                                                                           |
| `dependency-management`        |  ![Maven Central](https://img.shields.io/maven-central/v/org.basepom.maven/dependency-management-maven-plugin)  | validates that the versions in dependency management and plugin management match the resolved versions. Fork from [the original hubspot plugin](https://github.com/HubSpot/dependency-management-maven-plugin), which seems to be abandoned. |
| `dependency-scope`             |    ![Maven Central](https://img.shields.io/maven-central/v/org.basepom.maven/dependency-scope-maven-plugin)     | validates that dependencies in `test` scope don't override `compile` scope dependencies. Fork from [the original hubspot plugin](https://github.com/HubSpot/dependency-scope-maven-plugin), which seems to be abandoned.                     |
| * `repack`                     |         ![Maven Central](https://img.shields.io/maven-central/v/org.basepom.maven/repack-maven-plugin)          | repackages any java project with all its dependencies in a single jar file.                                                                                                                                                                  |

----
[![CD (Snapshot deployment)](https://github.com/basepom/maven-plugins/actions/workflows/master-cd.yml/badge.svg)](https://github.com/basepom/maven-plugins/actions/workflows/master-cd.yml)
