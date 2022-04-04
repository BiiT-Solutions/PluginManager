package com.biit.plugins;

import com.biit.plugins.exceptions.DuplicatedPluginFoundException;
import com.biit.plugins.interfaces.ICommonPlugin;
import com.biit.plugins.interfaces.IPlugin;
import com.biit.plugins.interfaces.ISpringPlugin;
import com.biit.plugins.interfaces.exceptions.InvalidMethodParametersException;
import com.biit.plugins.interfaces.exceptions.MethodInvocationException;
import com.biit.plugins.interfaces.exceptions.NoMethodFoundException;
import com.biit.plugins.interfaces.exceptions.NoPluginFoundException;
import com.biit.plugins.logger.PluginManagerLogger;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

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
    }

    @PostConstruct
    public void assignInstance() {
        setInstance(this);
    }

    public synchronized static void setInstance(PluginController pluginController) {
        PluginController.instance = pluginController;
    }

    public static PluginController getInstance() {
        return instance;
    }

    public <T extends ICommonPlugin> T getPlugin(Class<T> pluginInterface, String pluginName)
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
        List<?> plugins = pluginManager.getExtensions(pluginName);
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
    public <T extends ICommonPlugin> Object executePluginMethod(Class<T> pluginInterface, String pluginName,
                                                                String methodName, Object... parameters) throws NoPluginFoundException, DuplicatedPluginFoundException {
        try {
            try {
                PluginManagerLogger.debug(this.getClass().getName(),
                        "Executing '" + methodName + "' with parameters '" + Arrays.toString(parameters) + "'.");
                T plugin = getPlugin(pluginInterface, pluginName);
                if (plugin == null) {
                    PluginManagerLogger.warning(this.getClass().getName(), "No plugin exists with name '" + pluginName + "'.");
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
            return !getPlugins(ICommonPlugin.class).isEmpty();
        } catch (NoPluginFoundException e) {
            return false;
        } catch (DuplicatedPluginFoundException e) {
            return true;
        }
    }

    public Map<Class<?>, List<?>> getAllPluginsByClass() {
        Map<Class<?>, List<?>> pluginsFound = new HashMap<>();
        try {
            pluginsFound.put(ICommonPlugin.class, getPlugins(ICommonPlugin.class));
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

    public List<ICommonPlugin> getAllCommonPlugins() {
        try {
            return getPlugins(ICommonPlugin.class);
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
