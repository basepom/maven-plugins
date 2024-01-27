## Field formatting

`date` fields use Java date time formatter syntax. See the [`date` field description](fields.html#date-fields) for more details.

The `string`, `number`, `uuid` and `macro` fields support the `format` attribute to format its output using the Java formatter syntax:

The `format` attribute is used to with [String#format()](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html#format(java.lang.String,java.lang.Object...)) and most be formatted accordingly. The value field must contain a single `%s` placeholder and can use [all formatting characters](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Formatter.html#syntax) related to this placeholder.

Note that for all fields (even number fields), the placeholder must be `%s` for a string!


## Field regular expressions

All fields support a `regexp` attribute that can be used to apply a regular expression to the output of the field. Regular expressions are applied after formatting and before transformations.

A regular expression *must* match all characters of a field (start with `^` and end with `$`) and contain at least one capture group.

This example captures the numeric part from a build tag by using a regular expression:

```xml
<strings>
    <string>
        <id>version_from_tag</id>
        <values>
            <value>${ci.build-tag}</value>
        </values>
        <regexp>^build-tag-(\d+)$</regexp>
    </string>
</strings>
```

## Field transformers

The property helper plugin defines a set of transformers that can be applied to field and property values. Transformers are configured as a comma-separated list
and will be executed left-to-right.

The following transformers are defined:

- `lowercase` - transform all alphabetic characters into their lowercase equivalent in the current locale
- `uppercase` - transform all alphabetic characters into their uppercase equivalent in the current locale
- `remove_whitespace` - remove all whitespace characters
- `underscore_for_whitespace` - replace all whitespace characters with `_`
- `dash_for_whitespace` - replace all whitespace characters with `-`
- `use_underscore` - replace all `-` characters with `_`
- `use_dash` - replace all `_` characters with `-`
- `trim` - remove all leading and trailing whitespace

This example converts an existing property into all-uppercase and replaces any whitespace and `-` characters with the underscore `_`:

```xml
<strings>
    <string>
        <id>group-id-uppercase</id>
        <values>
            <value>${project.groupId}</value>
        </values>
        <export>true</export>
        <transformers>uppercase,use_underscore,underscore_for_whitespace</transformers>
    </string>
</strings>
```

Transformers can also be applied to property elements in a property group.
