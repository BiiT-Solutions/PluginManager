package com.biit.plugins.tests;

import java.nio.file.Paths;
import java.util.List;

import org.pf4j.DefaultPluginManager;
import org.pf4j.JarPluginLoader;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.biit.plugins.helloworld.Greeting;
import com.biit.plugins.test.interfaces.IPlugin2;

@Test(groups = { "pluginLoader" })
public class PluginLoaderTests {
	private final static String PLUGINS_FOLDER = "src/test/plugins";

	@Test
	public void loadPlugin() {
		// create the plugin manager
		DefaultPluginManager pluginManager = new DefaultPluginManager(Paths.get(PLUGINS_FOLDER)) {

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

		// retrieve all extensions for "Greeting" extension point
		List<Greeting> plugins = pluginManager.getExtensions(Greeting.class);
		Assert.assertEquals(plugins.size(), 1);

		for (Greeting plugin : plugins) {
			Assert.assertEquals(plugin.getGreeting(), "Welcome");
		}

		// stop and unload all plugins
		pluginManager.stopPlugins();
	}

	@Test
	public void loadIPlugin() {
		// create the plugin manager
		DefaultPluginManager pluginManager = new DefaultPluginManager(Paths.get(PLUGINS_FOLDER)) {

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

		// IPLugin2 must be in the plugin. If not
		// java.lang.InstantiationException appears.
		List<IPlugin2> plugins = pluginManager.getExtensions(IPlugin2.class);
		Assert.assertEquals(plugins.size(), 1);

		for (IPlugin2 plugin : plugins) {
			Assert.assertEquals(plugin.getPluginMethods().size(), 2);
		}

		// stop and unload all plugins
		pluginManager.stopPlugins();
	}
}
