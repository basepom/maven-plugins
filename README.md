# Maven Plugins

Assorted plugins that are useful in Maven projects

* `property-helper-maven-plugin` creates and manipulates properties, strings, dates, uuid etc. in the build process
* `dependency-management` validates that the versions in dependency management and plugin management match the resolved versions. Fork from [the original hubspot plugin](https://github.com/HubSpot/dependency-management-maven-plugin), which seems to be abandoned.
* `dependency-scope` validates that dependencies in `test` scope don't override `compile` scope dependencies. Fork from [the original hubspot plugin](https://github.com/HubSpot/dependency-scope-maven-plugin), which seems to be abandoned.
