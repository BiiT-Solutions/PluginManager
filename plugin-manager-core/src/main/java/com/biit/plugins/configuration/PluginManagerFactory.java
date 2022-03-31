package com.biit.plugins.configuration;

import com.biit.plugins.logger.PluginManagerLogger;
import org.pf4j.*;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class PluginManagerFactory {

    private final String pluginsLocations;

    public PluginManagerFactory(@Value("${plugins.directory:}") String pluginsLocations) {
        this.pluginsLocations = pluginsLocations;
    }

    @Bean
    public PluginManager pluginManager() {
        return getSpringBootPluginManager();
    }

    private PluginManager getSpringBootPluginManager() {
        PluginManagerLogger.debug(this.getClass().getName(), "Scanning folder '" + pluginsLocations + "' for plugins.");
        System.setProperty("pf4j.pluginsDir", pluginsLocations);
        PluginManager pluginManager = new SpringPluginManager();
        PluginManagerLogger.info(this.getClass().getName(), "Folder for searching is '" + pluginManager.getPluginsRoots() + "'.");
        return pluginManager;
    }

    /**
     * Not used now, as using Spring Boot plugin
     *
     * @return
     */
    private PluginManager getDefaultPluginManager() {
        // create the plugin manager
        PluginManagerLogger.debug(this.getClass().getName(), "Scanning folder '" + pluginsLocations + "' for plugins.");
        PluginManager pluginManager = new DefaultPluginManager(Paths.get(pluginsLocations)) {

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
}
