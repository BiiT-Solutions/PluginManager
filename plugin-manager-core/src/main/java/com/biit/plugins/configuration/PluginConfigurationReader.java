package com.biit.plugins.configuration;

import com.biit.logger.BiitCommonLogger;
import com.biit.plugins.logger.PluginManagerLogger;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PluginConfigurationReader implements EmbeddedValueResolverAware {
    public static final String SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER = "PLUGIN_CONFIG_PATH";
    public static final String SYSTEM_VARIABLE_PLUGINS_CONFIG_FILES = "PLUGIN_CONFIG_FILES";
    protected static final String SETTINGS_FILE = "settings.conf";

    private StringValueResolver resolver;

    private final Map<String, String> properties = new HashMap<>();


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
        BiitCommonLogger.debug(this.getClass(), "Loading settings file for '" + settingsFile + "'.");
        // Load settings as resource.
        if (settingsFile != null) {
            // using same name as jar file.
            if (resourceExist(settingsFile)) {
                loadPropertiesFileInResources(settingsFile);
                BiitCommonLogger.debug(this.getClass(), "Plugin using settings in resource folder '" + settingsFile + "'.");
            }
        }
        // Load settings as file.
        settingsFile = getJarFolder() + "/" + getSettingsFileName();
        BiitCommonLogger.debug(this.getClass(), "Searching for configuration file in '" + settingsFile + "'.");
        if (fileExists(settingsFile)) {
            loadPropertiesFileInResources(settingsFile);
            BiitCommonLogger.debug(this.getClass(), "Found configuration file '" + settingsFile + "'!");
        }
        //Load settings in system environment file path
        getConfigurationSettings().forEach(settingsSystemFile -> {
            if (fileExists(settingsSystemFile)) {
                loadPropertiesFileAbsolutePath(settingsSystemFile);
                BiitCommonLogger.debug(this.getClass(), "Found configuration file '" + settingsSystemFile + "'!");
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
        File file = new File(filePathString);
        return file.exists() && !file.isDirectory();
    }

    protected List<String> getConfigurationSettings() {
        String folder = System.getProperty(SYSTEM_VARIABLE_PLUGINS_CONFIG_FOLDER);
        String filesDefinitions = System.getProperty(SYSTEM_VARIABLE_PLUGINS_CONFIG_FILES);
        List<String> configurationFiles = new ArrayList<>();
        if (filesDefinitions != null) {
            List<String> files = Stream.of(filesDefinitions.split(",", -1))
                    .collect(Collectors.toList());
            files.forEach(file -> configurationFiles.add(folder + "/" + file));
        }
        return configurationFiles;
    }

    private void loadPropertiesFileInResources(String propertiesFile) {
        Resource resource = new ClassPathResource(propertiesFile);
        try {
            Properties propertiesLoaded = PropertiesLoaderUtils.loadProperties(resource);
            propertiesLoaded.stringPropertyNames().forEach(key -> properties.put(key, propertiesLoaded.getProperty(key)));
        } catch (IOException e) {
            PluginManagerLogger.warning(this.getClass().getName(), "No settings file found for '" + propertiesFile + "'.");
        }
    }

    private void loadPropertiesFileAbsolutePath(String propertiesFile) {
        try (InputStream input = new FileInputStream(propertiesFile)) {
            Properties propertiesLoaded = new Properties();

            // load a properties file
            propertiesLoaded.load(input);
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
        URL settingsUrl = getJarUrl();
        if (settingsUrl == null || !settingsUrl.getPath().contains(".jar")) {
            return null;
        }
        return settingsUrl.getPath().substring(settingsUrl.getPath().lastIndexOf('/') + 1, settingsUrl.getPath().length() - ".jar".length());
    }

    private boolean resourceExist(String resourceName) {
        return PluginConfigurationReader.class.getClassLoader().getResource(resourceName) != null;
    }
}
