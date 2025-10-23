package com.biit.plugins;

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

import com.biit.plugins.exceptions.DuplicatedPluginFoundException;
import com.biit.plugins.interfaces.IPlugin;
import com.biit.plugins.interfaces.ISpringPlugin;
import com.biit.plugins.interfaces.IStandardPlugin;
import com.biit.plugins.interfaces.exceptions.InvalidMethodParametersException;
import com.biit.plugins.interfaces.exceptions.MethodInvocationException;
import com.biit.plugins.interfaces.exceptions.NoMethodFoundException;
import com.biit.plugins.interfaces.exceptions.NoPluginFoundException;
import com.biit.plugins.logger.PluginManagerLogger;
import jakarta.annotation.PostConstruct;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton in charge of managing the plugins of the application
 */
@Component
public class PluginController {

    @Autowired
    private final PluginManager pluginManager;

    //For compatibility in old rules
    private static PluginController instance;

    public PluginController(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        pluginManager.stopPlugins();
        pluginManager.unloadPlugins();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
    }

    @PostConstruct
    public void assignInstance() {
        setInstance(this);
    }

    public static synchronized void setInstance(PluginController pluginController) {
        PluginController.instance = pluginController;
    }

    public static PluginController getInstance() {
        return instance;
    }

    public <T extends IPlugin> T getPlugin(Class<T> pluginInterface, String pluginName)
            throws NoPluginFoundException, DuplicatedPluginFoundException {
        PluginManagerLogger.debug(this.getClass().getName(),
                "Searching for plugin '" + pluginInterface + "' with name '" + pluginName + "'.");
        for (T plugin : getPlugins(pluginInterface)) {
            PluginManagerLogger.debug(this.getClass().getName(), "Existing plugin '" + plugin.getPluginName() + "'.");
            if (plugin.getPluginName().equalsIgnoreCase(pluginName)) {
                PluginManagerLogger.debug(this.getClass().getName(), "Found plugin '" + plugin.getPluginName() + "'.");
                return plugin;
            }
        }
        return null;
    }

    public <T extends ExtensionPoint> List<T> getPlugins(Class<T> pluginInterface)
            throws NoPluginFoundException, DuplicatedPluginFoundException {
        PluginManagerLogger.debug(this.getClass().getName(), "Searching for plugin '" + pluginInterface + "'.");
        return pluginManager.getExtensions(pluginInterface);
    }

    public ExtensionPoint getPlugin(String pluginName) throws NoPluginFoundException, DuplicatedPluginFoundException {
        PluginManagerLogger.debug(this.getClass().getName(), "Searching for plugin '" + pluginName + "'.");
        final List<?> plugins = pluginManager.getExtensions(pluginName);
        if (plugins.isEmpty()) {
            throw new NoPluginFoundException("No plugin exists with name '" + pluginName + "'.");
        }
        if (plugins.size() > 1) {
            throw new DuplicatedPluginFoundException("Several plugins with name '" + pluginName + "' found.");
        }
        return (ExtensionPoint) plugins.iterator().next();
    }

    /**
     * Executes the method of the plugin specified.<br>
     * It takes any number of parameters and passes them to the method invocation.
     *
     * @param pluginInterface interface of the plugin.
     * @param pluginName      name of the plugin.
     * @param methodName      method to be used.
     * @param parameters      parameters of the method.
     * @return the result of the execution of the plugin method.
     * @throws NoPluginFoundException
     * @throws DuplicatedPluginFoundException
     */
    public <T extends IPlugin> Object executePluginMethod(
            Class<T> pluginInterface, String pluginName,
            String methodName, Object... parameters) throws NoPluginFoundException, DuplicatedPluginFoundException {
        try {
            try {
                PluginManagerLogger.debug(this.getClass().getName(),
                        "Executing '" + methodName + "' with parameters '" + Arrays.toString(parameters) + "'.");
                final T plugin = getPlugin(pluginInterface, pluginName);
                if (plugin == null) {
                    PluginManagerLogger.warning(this.getClass().getName(), "No plugin exists with name '" + pluginName + "'.");
                    throw new NoPluginFoundException("No plugin exists with name '" + pluginName + "'.");
                }
                return plugin.executeMethod(methodName, parameters);
            } catch (MethodInvocationException e) {
                final StringBuilder sb = new StringBuilder();
                for (Object parameter : parameters) {
                    sb.append(parameter).append(" (").append(parameter.getClass().getName()).append(")");
                }
                if (e instanceof NoMethodFoundException) {
                    PluginManagerLogger.severe(this.getClass().getName(),
                            "No plugin method found '" + methodName + "' with parameters '" + sb.toString() + "'.");
                } else if (e instanceof InvalidMethodParametersException) {
                    PluginManagerLogger.severe(this.getClass().getName(),
                            "Invalid parameters on '" + methodName + "' with parameters '" + sb.toString() + "'.");
                } else {
                    PluginManagerLogger.severe(this.getClass().getName(),
                            "Exception invoking method '" + methodName + "' with parameters '" + sb.toString() + "'.");
                }
                PluginManagerLogger.errorMessage(this.getClass().getName(), e);
            }
        } catch (NoPluginFoundException | DuplicatedPluginFoundException e) {
            PluginManagerLogger.errorMessage(this.getClass().getName(), e);
            throw e;
        }
        return null;
    }

    public boolean existsPlugins() {
        try {
            return !getPlugins(IStandardPlugin.class).isEmpty();
        } catch (NoPluginFoundException e) {
            return false;
        } catch (DuplicatedPluginFoundException e) {
            return true;
        }
    }

    public Map<Class<?>, List<?>> getAllPluginsByClass() {
        final Map<Class<?>, List<?>> pluginsFound = new HashMap<>();
        try {
            pluginsFound.put(IStandardPlugin.class, getPlugins(IStandardPlugin.class));
        } catch (NoPluginFoundException | DuplicatedPluginFoundException e) {
            PluginManagerLogger.errorMessage(this.getClass().getName(), e);
        }
        try {
            pluginsFound.put(ISpringPlugin.class, getPlugins(ISpringPlugin.class));
        } catch (NoPluginFoundException | DuplicatedPluginFoundException e) {
            PluginManagerLogger.errorMessage(this.getClass().getName(), e);
        }
        return pluginsFound;
    }

    public List<IPlugin> getAllPlugins() {
        try {
            return getPlugins(IPlugin.class);
        } catch (NoPluginFoundException ignored) {

        } catch (DuplicatedPluginFoundException e) {
            PluginManagerLogger.severe(this.getClass().getName(), "Duplicated plugin found!");
        }

        return new ArrayList<>();
    }

    public List<IStandardPlugin> getAllStandardPlugins() {
        try {
            return getPlugins(IStandardPlugin.class);
        } catch (NoPluginFoundException ignored) {

        } catch (DuplicatedPluginFoundException e) {
            PluginManagerLogger.severe(this.getClass().getName(), "Duplicated plugin found!");
        }

        return new ArrayList<>();
    }

    public List<ISpringPlugin> getAllSpringBootPlugins() {
        try {
            return getPlugins(ISpringPlugin.class);
        } catch (NoPluginFoundException ignored) {

        } catch (DuplicatedPluginFoundException e) {
            PluginManagerLogger.severe(this.getClass().getName(), "Duplicated plugin found!");
        }

        return new ArrayList<>();
    }
}
