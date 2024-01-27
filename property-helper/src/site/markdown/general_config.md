## Plugin configuration

The property helper plugin supports the following main configuration options:


| configuration attribute | allowed values                      | required | default value                           | function                                                                       |
|-------------------------|-------------------------------------|----------|-----------------------------------------|--------------------------------------------------------------------------------|
| skip                    | `true`, `false`                     | no       | `false`                                 | if true, skip plugin execution                                                 |
| persist                 | `true`, `false`                     | no       | "get" goal: `false`, "inc" goal: `true` | if true, persist all referenced file properties                                |
| onDuplicateField        | `ignore`, `warn`, `fail`            | no       | `fail`                                  | action in case a field name is used multiple times                             |
| activeGroups            | nested list of property group names | no       | (none)                                  | which property groups to activate. If unset, all property groups are activated. If an empty list is present, no property group is activated. |

See also [Field definitions](fields.html) for the `<strings>`, `<numbers>`, `<dates>`, `<uuids>` and `<macros>` attributes, which define fields and [Property Groups](property_groups.html) on how to define and export property groups to the maven build properties.
