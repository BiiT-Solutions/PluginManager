package com.biit.plugins.configuration;

import java.nio.file.Path;

import com.biit.plugins.logger.PluginManagerLogger;
import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;
import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;
import com.biit.utils.configuration.exceptions.PropertyNotFoundException;
import com.biit.utils.file.watcher.FileWatcher.FileModifiedListener;

public class PluginManagerConfigurationReader extends ConfigurationReader {
	private static final String SETTINGS_FILE = "settings.conf";
	private static final String PLUGINS_SYSTEM_VARIABLE_CONFIG = "PLUGINS_CONFIG";

	private static final String PLUGINS_PATH_PROPERTY_NAME = "drools.plugins.path";
	private static final String DEFAULT_PLUGINS_PATH = "plugins/";

	private static PluginManagerConfigurationReader instance;

	/**
	 * Load settings from defaults folders, conf with plugin jar or system
	 * variables.
	 */
	private PluginManagerConfigurationReader() {
		super();

		addProperty(PLUGINS_PATH_PROPERTY_NAME, DEFAULT_PLUGINS_PATH);

		PropertiesSourceFile sourceFile = new PropertiesSourceFile(SETTINGS_FILE);
		sourceFile.addFileModifiedListeners(new FileModifiedListener() {

			@Override
			public void changeDetected(Path pathToFile) {
				PluginManagerLogger.info(this.getClass().getName(), "WAR settings file '" + pathToFile + "' change detected.");
				readConfigurations();
			}
		});
		addPropertiesSource(sourceFile);

		SystemVariablePropertiesSourceFile systemSourceFile = new SystemVariablePropertiesSourceFile(PLUGINS_SYSTEM_VARIABLE_CONFIG, SETTINGS_FILE);
		systemSourceFile.addFileModifiedListeners(new FileModifiedListener() {

			@Override
			public void changeDetected(Path pathToFile) {
				PluginManagerLogger.info(this.getClass().getName(), "System variable settings file '" + pathToFile + "' change detected.");
				readConfigurations();
			}
		});
		addPropertiesSource(systemSourceFile);

		readConfigurations();
	}

	public static PluginManagerConfigurationReader getInstance() {
		if (instance == null) {
			synchronized (PluginManagerConfigurationReader.class) {
				if (instance == null) {
					instance = new PluginManagerConfigurationReader();
				}
			}
		}
		return instance;
	}

	@Override
	public String getProperty(String propertyId) {
		try {
			return super.getProperty(propertyId);
		} catch (PropertyNotFoundException e) {
			PluginManagerLogger.errorMessage(this.getClass().getName(), e);
			return null;
		}
	}

	public String getPluginsPath() {
		return getProperty(PLUGINS_PATH_PROPERTY_NAME);
	}
}
