package com.biit.plugins;

import com.biit.plugins.exceptions.DuplicatedPluginFoundException;
import com.biit.plugins.interfaces.IPlugin;
import com.biit.plugins.interfaces.exceptions.InvalidMethodParametersException;
import com.biit.plugins.interfaces.exceptions.MethodInvocationException;
import com.biit.plugins.interfaces.exceptions.NoMethodFoundException;
import com.biit.plugins.interfaces.exceptions.NoPluginFoundException;
import com.biit.plugins.logger.PluginManagerLogger;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Singleton in charge of managing the plugins of the application
 */
@Component
public class PluginController {

    @Autowired
    private final PluginManager pluginManager;

    public PluginController(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
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

    public <T extends IPlugin> List<T> getPlugins(Class<T> pluginInterface)
            throws NoPluginFoundException, DuplicatedPluginFoundException {
        PluginManagerLogger.debug(this.getClass().getName(), "Searching for plugin '" + pluginInterface + "'.");
        return pluginManager.getExtensions(pluginInterface);
    }

    public IPlugin getPlugin(String pluginName) throws NoPluginFoundException, DuplicatedPluginFoundException {
        PluginManagerLogger.debug(this.getClass().getName(), "Searching for plugin '" + pluginName + "'.");
        List<?> plugins = pluginManager.getExtensions(pluginName);
        if (plugins.isEmpty()) {
            throw new NoPluginFoundException("No plugin exists with name '" + pluginName + "'.");
        }
        if (plugins.size() > 1) {
            throw new DuplicatedPluginFoundException("Several plugins with name '" + pluginName + "' found.");
        }
        return (IPlugin) plugins.iterator().next();
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
    public <T extends IPlugin> Object executePluginMethod(Class<T> pluginInterface, String pluginName,
                                                          String methodName, Object... parameters) throws NoPluginFoundException, DuplicatedPluginFoundException {
        try {
            try {
                PluginManagerLogger.debug(this.getClass().getName(),
                        "Executing '" + methodName + "' with parameters '" + Arrays.toString(parameters) + "'.");
                T plugin = getPlugin(pluginInterface, pluginName);
                if (plugin == null) {
                    throw new NoPluginFoundException("No plugin exists with name '" + pluginName + "'.");
                }
                return plugin.executeMethod(methodName, parameters);
            } catch (MethodInvocationException e) {
                StringBuilder sb = new StringBuilder();
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
            return !getPlugins(IPlugin.class).isEmpty();
        } catch (NoPluginFoundException e) {
            return false;
        } catch (DuplicatedPluginFoundException e) {
            return true;
        }
    }

    public List<IPlugin> getAllPlugins() {
        try {
            return getPlugins(IPlugin.class);
        } catch (NoPluginFoundException | DuplicatedPluginFoundException e) {
            PluginManagerLogger.errorMessage(this.getClass().getName(), e);
        }
        return new ArrayList<IPlugin>();
    }
}
