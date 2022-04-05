package com.biit.plugins;

import com.biit.plugins.configuration.PluginConfigurationReader;
import com.biit.plugins.logger.PluginManagerLogger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PluginSearcher {


    protected List<String> getSystemConfigurationSettings() {
        return getSystemEnvFiles(PluginConfigurationReader.PLUGINS_CONFIG_FILES_EXTENSION);
    }

    protected List<String> getSystemEnvFiles(String extension) {
        if (System.getenv(PluginConfigurationReader.SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER) != null) {
            PluginManagerLogger.debug(this.getClass().getName(), "Searching plugins configuration on path defined in system variable as '"
                    + System.getenv(PluginConfigurationReader.SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER) + "'.");
            Path folder = Paths.get(System.getenv(PluginConfigurationReader.SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER));
            if (Files.isDirectory(folder)) {
                try {
                    // find files matched `png` file extension from folder C:\\test
                    try (Stream<Path> walk = Files.walk(folder, 1)) {
                        return walk
                                .filter(p -> !Files.isDirectory(p))   // not a directory
                                .map(p -> p.toString().toLowerCase()) // convert path to string
                                .filter(f -> {
                                    PluginManagerLogger.debug(this.getClass().getName(), "Found configuration file '" + f + "'.");
                                    return f.endsWith(extension);
                                })       // check end with
                                .collect(Collectors.toList());        // collect all matched to a List
                    }
                } catch (IOException e) {
                    PluginManagerLogger.warning(this.getClass().getName(), "Invalid folder '" + folder + "'.");
                }
            } else {
                PluginManagerLogger.warning(this.getClass().getName(), "System variable '" + folder + "' is not a folder path.");
            }
        } else {
            PluginManagerLogger.debug(this.getClass().getName(), "No system variable found for '"
                    + PluginConfigurationReader.SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER + "'.");
        }
        return new ArrayList<>();
    }
}
