package com.biit.plugins.tests;

import com.biit.plugins.PluginController;
import com.biit.plugins.exceptions.DuplicatedPluginFoundException;
import com.biit.plugins.interfaces.IPlugin;
import com.biit.plugins.interfaces.exceptions.NoPluginFoundException;
import com.biit.plugins.test.interfaces.IPlugin2;
import com.biit.plugins.test.interfaces.IPlugin3;
import org.pf4j.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.List;

@Test(groups = { "pluginController" })
public class TestPluginControllerTests {
	private final static String PLUGINS_FOLDER = "src/test/plugins";
	private final static String ANOTHER_PLUGIN_ID = "another-plugin";
	private final static String ANOTHER_PLUGIN_METHOD = "methodGetGreeting";
	private final static String ANOTHER_PLUGIN_METHOD_RETURN = "Another greeting";

	@Test
	public void loadIPlugin() throws NoPluginFoundException, DuplicatedPluginFoundException {
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
		List<IPlugin2> plugins2 = PluginController.getInstance().getPlugins(IPlugin2.class);
		Assert.assertEquals(plugins2.size(), 1);

		for (IPlugin2 plugin : plugins2) {
			Assert.assertEquals(plugin.getPluginMethods().size(), 2);
		}

		// IPlugin3 must be in the plugin. If not
		// java.lang.InstantiationException appears.
		List<IPlugin3> plugins3 = PluginController.getInstance().getPlugins(IPlugin3.class);
		Assert.assertEquals(plugins3.size(), 1);

		for (IPlugin3 plugin : plugins3) {
			Assert.assertEquals(plugin.getPluginMethods().size(), 2);
		}

		// Load all toghether
		List<IPlugin> plugins1 = PluginController.getInstance().getPlugins(IPlugin.class);
		//IPlugin2, IPlugin3, ISpringPlugin extends IPlugin
		Assert.assertEquals(plugins1.size(), 3);

		for (IPlugin plugin : plugins1) {
			Assert.assertEquals(plugin.getPluginMethods().size(), 2);
		}

		// stop and unload all plugins
		pluginManager.stopPlugins();
	}

	@Test
	public void helloWorldPluginSelectionTest1() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoPluginFoundException, DuplicatedPluginFoundException {
		// Calling the first plugin
		IPlugin3 pluginInterface = PluginController.getInstance().getPlugin(IPlugin3.class, ANOTHER_PLUGIN_ID);
		Assert.assertNotNull(pluginInterface);
		Method method = pluginInterface.getPluginMethod(ANOTHER_PLUGIN_METHOD);
		Assert.assertEquals(method.invoke(pluginInterface), ANOTHER_PLUGIN_METHOD_RETURN);
	}
}
