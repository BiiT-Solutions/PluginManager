package com.biit.plugins.configuration;

/*-
 * #%L
 * Plugin Manager (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.plugins.logger.PluginManagerLogger;
import org.pf4j.DefaultPluginManager;
import org.pf4j.JarPluginLoader;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginLoader;
import org.pf4j.PluginManager;
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
    public static final String SYSTEM_VARIABLE_PLUGINS_FOLDER = "PLUGINS_PATH";

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
        final Set<String> pluginsPaths = new HashSet<>(Arrays.asList(pluginsLocations));
        //Getting from system environment.
        addSystemVariablePath(pluginsPaths);
        PluginManagerLogger.debug(this.getClass().getName(), "Scanning folder '" + pluginsPaths + "' for plugins.");
        System.setProperty("pf4j.pluginsDir", String.join(",", pluginsPaths));
        final PluginManager pluginManager = new SpringPluginManager();
        PluginManagerLogger.info(this.getClass().getName(), "Folders for searching are '" + pluginManager.getPluginsRoots() + "'.");
        return pluginManager;
    }

    /**
     * Not used now, as using Spring Boot plugin
     *
     * @return
     */
    private PluginManager getDefaultPluginManager() {
        // create the plugin manager
        final Set<String> pluginsPaths = new HashSet<>(Arrays.asList(pluginsLocations));
        addSystemVariablePath(pluginsPaths);
        PluginManagerLogger.debug(this.getClass().getName(), "Scanning folders '" + pluginsPaths + "' for plugins.");
        final Path[] paths = pluginsPaths.stream().map(Paths::get).toArray(Path[]::new);
        final PluginManager pluginManager = new DefaultPluginManager(paths) {

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
        final String systemVariable = System.getenv(SYSTEM_VARIABLE_PLUGINS_FOLDER);
        if (systemVariable != null) {
            PluginManagerLogger.debug(this.getClass().getName(), "Env variable '" + SYSTEM_VARIABLE_PLUGINS_FOLDER
                    + "' set as '" + systemVariable + "'.");
            final String[] paths = systemVariable.split(",");
            for (String path : paths) {
                final Path folder = Paths.get(path);
                if (Files.isDirectory(folder)) {
                    PluginManagerLogger.info(this.getClass().getName(), "Directory '"
                            + folder + "' obtained from env variable '" + SYSTEM_VARIABLE_PLUGINS_FOLDER
                            + "' added as plugin folder!");
                    pluginsPaths.add(folder.toString());
                } else {
                    PluginManagerLogger.warning(this.getClass().getName(), "Directory '"
                            + folder.toString() + "' defined on env variable '" + SYSTEM_VARIABLE_PLUGINS_FOLDER + "' is invalid.");
                }
            }
        } else {
            PluginManagerLogger.debug(this.getClass().getName(), "No system variable found for plugins directory '"
                    + SYSTEM_VARIABLE_PLUGINS_FOLDER + "'.");
        }
    }
}
