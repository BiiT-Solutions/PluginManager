package com.biit.plugins.configuration;

import java.net.MalformedURLException;
import java.net.URL;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.ConfigurationReader;

public class PluginConfigurationReader extends ConfigurationReader {

	private URL getJarUrl() {
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
			e.printStackTrace();
			BiitCommonLogger.errorMessageNotification(this.getClass(), e);
		}

		return url;
	}

	protected String getJarFolder() {
		URL settingsUrl = getJarUrl();
		return settingsUrl.getPath().substring(0, settingsUrl.getPath().lastIndexOf('/'));
	}

	protected String getJarName() {
		URL settingsUrl = getJarUrl();
		if (settingsUrl == null || !settingsUrl.getPath().contains(".jar")) {
			return null;
		}
		return settingsUrl.getPath().substring(settingsUrl.getPath().lastIndexOf('/') + 1,
				settingsUrl.getPath().length() - ".jar".length());
	}

	public PluginConfigurationReader() {

	}

}
