package com.biit.plugins;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.pf4j.DefaultPluginManager;
import org.pf4j.JarPluginLoader;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginLoader;
import org.pf4j.PluginManager;

import com.biit.plugins.configuration.PluginManagerConfigurationReader;
import com.biit.plugins.exceptions.DuplicatedPluginFoundException;
import com.biit.plugins.exceptions.InvalidMethodParametersException;
import com.biit.plugins.exceptions.MethodInvocationException;
import com.biit.plugins.exceptions.NoMethodFoundException;
import com.biit.plugins.exceptions.NoPluginFoundException;
import com.biit.plugins.interfaces.IPlugin;
import com.biit.plugins.logger.PluginManagerLogger;

/**
 * Singleton in charge of managing the plugins of the application
 * 
 */
public class PluginController {
	private static PluginController instance = new PluginController();
	private PluginManager pluginManager;

	public static PluginController getInstance() {
		return instance;
	}

	/**
	 * Override of the clone method to avoid creating more than one instance
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private PluginController() {
		// create the plugin manager
		String folderToScan = PluginManagerConfigurationReader.getInstance().getPluginsPath();
		PluginManagerLogger.debug(this.getClass().getName(), "Scanning folder '" + folderToScan + "' for plugins.");
		pluginManager = new DefaultPluginManager(Paths.get(folderToScan)) {

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
	}

	public <T extends IPlugin> T getPlugins(Class<T> pluginInterface, String pluginName) throws NoPluginFoundException, DuplicatedPluginFoundException {
		PluginManagerLogger.debug(this.getClass().getName(), "Searching for plugin '" + pluginInterface + "' with name '" + pluginName + "'.");
		for (T plugin : getPlugins(pluginInterface)) {
			if (plugin.getPluginName().equalsIgnoreCase(pluginName)) {
				return plugin;
			}
		}
		return null;
	}

	public <T extends IPlugin> List<T> getPlugins(Class<T> pluginInterface) throws NoPluginFoundException, DuplicatedPluginFoundException {
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
	 * It takes any number of parameters and passes them to the method
	 * invocation.
	 * 
	 * @param interfaceName
	 *            interface of the plugin.
	 * @param pluginName
	 *            name of the plugin.
	 * @param methodName
	 *            method to be used.
	 * @param parameters
	 *            parameters of the method.
	 * @return the result of the execution of the plugin method.
	 * @throws NoPluginFoundException
	 * @throws DuplicatedPluginFoundException
	 * @throws MethodInvocationException
	 * @throws InvalidMethodParametersException
	 * @throws NoMethodFoundException
	 */
	public Object executePluginMethod(String pluginName, String methodName, Object... parameters) throws NoPluginFoundException,
			DuplicatedPluginFoundException, NoMethodFoundException, InvalidMethodParametersException, MethodInvocationException {
		try {
			PluginManagerLogger.debug(this.getClass().getName(), "Executing '" + methodName + "' with parameters '" + Arrays.toString(parameters) + "'.");
			IPlugin pluginInterface = getPlugin(pluginName);
			return pluginInterface.executeMethod(methodName, parameters);
		} catch (IllegalArgumentException e) {
			StringBuilder sb = new StringBuilder();
			for (Object parameter : parameters) {
				sb.append(parameter + " (" + parameter.getClass().getName() + ")");
			}
			PluginManagerLogger.severe(this.getClass().getName(), "No plugin method found '" + methodName + "' with parameters '" + sb.toString() + "'.");
			PluginManagerLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
	}
}
