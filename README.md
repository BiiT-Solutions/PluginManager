# HowTo use this library on your custom Spring boot application

Add the dependency on the maven pom.xml

```
<dependency>
    <groupId>com.biit</groupId>
    <artifactId>plugin-manager-core</artifactId>
    <version>${plugin-manager.version}</version>
</dependency>
```

And include the packet com.biit.plugins on the @ComponentScan annotation

```
@ComponentScan({"com.biit.project", "com.biit.plugins"})
```
Finally, remember to include `com.biit.plugins.logger.PluginManagerLogger` to the logback configuration file.

## Customize the plugins folder location
For customizing the plugins folder, you can set the property:
```
plugins.directory=plugins
```
The location of the folder is inside the resources folder. If not set, the default value is `plugins` included inside the resources folder.

## Usage

The bean `pluginManager` includes all basic methods for searching and using plugins. This is the default class from the original library. On this project can be used with `@Autowired`.

Also, we have the bean `pluginController` that have some extra actions such as:

- Methods for executing some plugins methods `executePluginMethod`. The plugin method must start with the prefix `method` to be captured.
- Methods for checking if a plugin exists `existsPlugins`.
- Methods for listing all available plugins `getAllPlugins`.



