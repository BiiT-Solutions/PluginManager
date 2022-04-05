package com.biit.plugins.configuration;

import com.biit.plugins.logger.PluginManagerLogger;
import org.pf4j.*;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class PluginManagerFactory {

    private final String[] pluginsLocations;

    public PluginManagerFactory(@Value("${plugins.directory:}") String[] pluginsLocations) {
        this.pluginsLocations = pluginsLocations;
    }

    @Bean
    public PluginManager pluginManager() {
        return getSpringBootPluginManager();
    }

    private PluginManager getSpringBootPluginManager() {
        //Default configuration from application.properties.
        Set<String> pluginsPaths = new HashSet<>(Arrays.asList(pluginsLocations));
        //Getting from system environment.
        addSystemVariablePath(pluginsPaths);
        PluginManagerLogger.debug(this.getClass().getName(), "Scanning folder '" + pluginsPaths + "' for plugins.");
        System.setProperty("pf4j.pluginsDir", String.join(",", pluginsPaths));
        PluginManager pluginManager = new SpringPluginManager();
        PluginManagerLogger.info(this.getClass().getName(), "Folder for searching are '" + pluginManager.getPluginsRoots() + "'.");
        return pluginManager;
    }

    /**
     * Not used now, as using Spring Boot plugin
     *
     * @return
     */
    private PluginManager getDefaultPluginManager() {
        // create the plugin manager
        Set<String> pluginsPaths = new HashSet<>(Arrays.asList(pluginsLocations));
        addSystemVariablePath(pluginsPaths);
        PluginManagerLogger.debug(this.getClass().getName(), "Scanning folders '" + pluginsPaths + "' for plugins.");
        Path[] paths = pluginsPaths.stream().map(Paths::get).toArray(Path[]::new);
        PluginManager pluginManager = new DefaultPluginManager(paths) {

            @Override
            protected PluginLoader createPluginLoader() {
                // load only jar plugins
                return new JarPluginLoader(this);
            }

            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                // read plugin descriptor from jar's manifest
                return new ManifestPluginDescriptorFinder();
            }
        };

        // start and load all plugins of application
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        PluginManagerLogger.debug(this.getClass().getName(), "Plugins found '" + pluginManager.getPlugins() + "'.");
        PluginManagerLogger.debug(this.getClass().getName(), "Resolved Plugins '" + pluginManager.getResolvedPlugins() + "'.");
        PluginManagerLogger.debug(this.getClass().getName(), "Started plugins '" + pluginManager.getStartedPlugins() + "'.");

        return pluginManager;
    }

    private void addSystemVariablePath(Set<String> pluginsPaths) {
        String systemVariable = System.getenv(PluginConfigurationReader.SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER);
        if (systemVariable != null) {
            Path folder = Paths.get(systemVariable);
            if (Files.isDirectory(folder)) {
                pluginsPaths.add(folder.toString());
            }
        } else {
            PluginManagerLogger.debug(this.getClass().getName(), "No system variable found for '"
                    + PluginConfigurationReader.SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER + "'.");
        }
    }
}
