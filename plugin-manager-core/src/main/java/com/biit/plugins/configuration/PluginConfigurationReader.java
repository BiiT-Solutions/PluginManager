package com.biit.plugins.configuration;

import com.biit.logger.BiitCommonLogger;
import com.biit.plugins.logger.PluginManagerLogger;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PluginConfigurationReader implements EmbeddedValueResolverAware {
    public static final String SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER = "PLUGINS_CONFIG_PATH";
    public static final String PLUGINS_CONFIG_FILES_EXTENSION = "conf";
    protected static final String SETTINGS_FILE = "settings.conf";
    private StringValueResolver resolver;
    private final Map<String, String> properties = new HashMap<>();
    private final String pluginsLocations;

    public PluginConfigurationReader(@Value("${plugins.directory:}") String pluginsLocations) {
        this.pluginsLocations = pluginsLocations;
    }


    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    public String getProperty(String propertyId) {
        try {
            return resolver.resolveStringValue("${" + propertyId.toLowerCase(Locale.ROOT) + "}");
        } catch (IllegalArgumentException e) {
            return properties.get(propertyId);
        }
    }

    public String getPropertyValue(String propertyTag) {
        //Check if property exists.
        try {
            return getProperty(propertyTag);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @PostConstruct
    public void loadPluginProperties() {
        String settingsFile = getSettingsFileName();
        PluginManagerLogger.debug(this.getClass(), "Loading plugins settings file for '" + settingsFile + "'.");
        // Load settings as resource.
        if (settingsFile != null) {
            // using same name as jar file.
            if (resourceExist(settingsFile)) {
                loadPropertiesFileInResources(settingsFile);
                PluginManagerLogger.debug(this.getClass(), "Plugin using settings in resource folder '" + settingsFile + "'.");
            }
        }
        // Load settings as file.
        settingsFile = getJarFolder() + "/" + getSettingsFileName();
        PluginManagerLogger.debug(this.getClass(), "Searching for plugins configuration file in '" + settingsFile + "'.");
        if (fileExists(settingsFile)) {
            loadPropertiesFileInResources(settingsFile);
            PluginManagerLogger.debug(this.getClass(), "Found plugins configuration file '" + settingsFile + "'!");
        }
        //Load settings in system environment file path
        getConfigurationSettings().forEach(settingsSystemFile -> {
            if (fileExists(settingsSystemFile)) {
                loadPropertiesFileAbsolutePath(settingsSystemFile);
                PluginManagerLogger.debug(this.getClass(), "Found plugins configuration file '" + settingsSystemFile + "'!");
            }
        });
        getSystemPropertyConfigurationSettings().forEach(settingsSystemFile -> {
            if (fileExists(settingsSystemFile)) {
                loadPropertiesFileAbsolutePath(settingsSystemFile);
                PluginManagerLogger.debug(this.getClass(), "Found plugins configuration file '" + settingsSystemFile + "' on folder '"
                        + System.getProperty(SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER) + "'.!");
            }
        });
        getSystemEnvConfigurationSettings().forEach(settingsSystemFile -> {
            if (fileExists(settingsSystemFile)) {
                loadPropertiesFileAbsolutePath(settingsSystemFile);
                PluginManagerLogger.debug(this.getClass(), "Found plugins configuration file '" + settingsSystemFile + "' on folder '"
                        + System.getenv(SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER) + "'.!");
            }
        });
    }

    private String getSettingsFileName() {
        final String settingsFile = getJarName();
        if (settingsFile == null) {
            return SETTINGS_FILE;
        }
        return settingsFile + ".conf";
    }

    protected boolean fileExists(String filePathString) {
        if (filePathString == null) {
            return false;
        }
        final File file = new File(filePathString);
        return file.exists() && !file.isDirectory();
    }

    protected List<String> getConfigurationSettings() {
        PluginManagerLogger.debug(this.getClass().getName(), "Searching plugins configuration on resources '"
                + pluginsLocations + "'.");
        if (pluginsLocations != null) {
            final Path folder = Paths.get(pluginsLocations);
            if (Files.isDirectory(folder)) {
                try {
                    // find files matched `png` file extension from folder C:\\test
                    try (Stream<Path> walk = Files.walk(folder, 1)) {
                        return walk
                                .filter(p -> !Files.isDirectory(p))   // not a directory
                                .map(p -> p.toString().toLowerCase()) // convert path to string
                                .filter(f -> f.endsWith(PLUGINS_CONFIG_FILES_EXTENSION))       // check end with
                                .collect(Collectors.toList());        // collect all matched to a List
                    }
                } catch (IOException e) {
                    PluginManagerLogger.warning(this.getClass().getName(), "Invalid folder '" + folder + "'.");
                }
            }
        }
        return new ArrayList<>();
    }

    protected List<String> getSystemPropertyConfigurationSettings() {
        if (System.getProperty(SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER) != null) {
            PluginManagerLogger.debug(this.getClass().getName(), "Searching plugins configuration on path defined in system property as '"
                    + System.getProperty(SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER) + "'.");
            final Path folder = Paths.get(System.getProperty(SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER));
            if (Files.isDirectory(folder)) {
                try {
                    // find files matched `png` file extension from folder C:\\test
                    try (Stream<Path> walk = Files.walk(folder, 1)) {
                        return walk
                                .filter(p -> !Files.isDirectory(p))   // not a directory
                                .map(p -> p.toString().toLowerCase()) // convert path to string
                                .filter(f -> {
                                    PluginManagerLogger.debug(this.getClass().getName(), "Found configuration file '" + f + "'.");
                                    return f.endsWith(PLUGINS_CONFIG_FILES_EXTENSION);
                                })       // check end with
                                .collect(Collectors.toList());        // collect all matched to a List
                    }
                } catch (IOException e) {
                    PluginManagerLogger.warning(this.getClass().getName(), "Invalid folder '" + folder + "'.");
                }
            } else {
                PluginManagerLogger.warning(this.getClass().getName(), "System property '" + folder + "' is not a folder path.");
            }
        } else {
            PluginManagerLogger.debug(this.getClass().getName(), "No system property found for '"
                    + SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER + "'.");
        }
        return new ArrayList<>();
    }


    protected List<String> getSystemEnvConfigurationSettings() {
        if (System.getenv(SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER) != null) {
            PluginManagerLogger.debug(this.getClass().getName(), "Searching plugins configuration on path defined in system variable as '"
                    + System.getenv(SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER) + "'.");
            final Path folder = Paths.get(System.getenv(SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER));
            if (Files.isDirectory(folder)) {
                try {
                    try (Stream<Path> walk = Files.walk(folder, 1)) {
                        return walk
                                .filter(p -> !Files.isDirectory(p))   // not a directory
                                .map(p -> p.toString().toLowerCase()) // convert path to string
                                .filter(f -> {
                                    PluginManagerLogger.debug(this.getClass().getName(), "Found configuration file '" + f + "'.");
                                    return f.endsWith(PLUGINS_CONFIG_FILES_EXTENSION);
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
                    + SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER + "'.");
        }
        return new ArrayList<>();
    }

    private void loadPropertiesFileInResources(String propertiesFile) {
        final Resource resource = new ClassPathResource(propertiesFile);
        try {
            final Properties propertiesLoaded = PropertiesLoaderUtils.loadProperties(resource);
            propertiesLoaded.stringPropertyNames().forEach(key -> properties.put(key, propertiesLoaded.getProperty(key)));
        } catch (IOException e) {
            PluginManagerLogger.warning(this.getClass().getName(), "No settings file found for '" + propertiesFile + "'.");
        }
    }

    private void loadPropertiesFileAbsolutePath(String propertiesFile) {
        try (InputStream input = new FileInputStream(propertiesFile)) {
            final Properties propertiesLoaded = new Properties();

            // load a properties file
            propertiesLoaded.load(input);
            PluginManagerLogger.debug(this.getClass().getName(), "Settings found '" + propertiesLoaded.stringPropertyNames() + "'.");
            propertiesLoaded.stringPropertyNames().forEach(key -> properties.put(key, propertiesLoaded.getProperty(key)));
        } catch (IOException ex) {
            PluginManagerLogger.warning(this.getClass().getName(), "No settings file found for '" + propertiesFile + "'.");
        }
    }

    protected URL getJarUrl() {
        URL url = this.getClass().getResource('/' + this.getClass().getName().replace('.', '/') + ".class");
        if (url == null) {
            return null;
        }
        // Remove class inside JAR file (i.e. jar:file:///outer.jar!/file.class)
        String packetPath = url.getPath();
        if (packetPath.contains("!")) {
            packetPath = packetPath.substring(0, packetPath.indexOf("!"));
        }

        packetPath = packetPath.replace("jar:", "");
        try {
            url = new URL(packetPath);
        } catch (MalformedURLException e) {
            BiitCommonLogger.warning(this.getClass(), e.getMessage());
        }

        return url;
    }

    protected String getJarFolder() {
        final URL settingsUrl = getJarUrl();
        if (settingsUrl == null) {
            return "";
        }
        return settingsUrl.getPath().substring(0, settingsUrl.getPath().lastIndexOf('/'));
    }

    protected String getJarName() {
        final URL settingsUrl = getJarUrl();
        if (settingsUrl == null || !settingsUrl.getPath().contains(".jar")) {
            return null;
        }
        return settingsUrl.getPath().substring(settingsUrl.getPath().lastIndexOf('/') + 1, settingsUrl.getPath().length() - ".jar".length());
    }

    private boolean resourceExist(String resourceName) {
        return PluginConfigurationReader.class.getClassLoader().getResource(resourceName) != null;
    }
}
