package com.biit.plugins.tests;

import com.biit.plugins.helloworld.Greeting;
import com.biit.plugins.interfaces.ISpringPlugin;
import com.biit.plugins.test.interfaces.IPlugin2;
import org.pf4j.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.List;

@Test(groups = {"pluginLoader"})
public class PluginLoaderTests {
    private final static String PLUGINS_FOLDER = "src/test/plugins";
    private DefaultPluginManager pluginManager;

    @BeforeClass
    public void loadPlugins() {
        // create the plugin manager
        pluginManager = new DefaultPluginManager(Paths.get(PLUGINS_FOLDER)) {

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

    @Test
    public void loadPlugin() {
        // retrieve all extensions for "Greeting" extension point
        List<Greeting> plugins = pluginManager.getExtensions(Greeting.class);
        //Gets one value for each plugin.
        Assert.assertEquals(plugins.size(), 3);

        for (Greeting plugin : plugins) {
            Assert.assertEquals(plugin.getGreeting(), "Welcome");
        }
    }

    @Test
    public void loadIPlugin() {
        // IPlugin2 must be in the plugin. If not java.lang.InstantiationException appears.
        List<IPlugin2> plugins = pluginManager.getExtensions(IPlugin2.class);
        //Gets one value for each plugin.
        Assert.assertEquals(plugins.size(), 3);

        for (IPlugin2 plugin : plugins) {
            Assert.assertEquals(plugin.getPluginMethods().size(), 2);
        }
    }

    @Test
    public void loadSpringBootPlugin() {
        // IPlugin2 must be in the plugin. If not java.lang.InstantiationException appears.
        List<ISpringPlugin> plugins = pluginManager.getExtensions(ISpringPlugin.class);
        //Gets one value for each plugin.
        Assert.assertEquals(plugins.size(), 4);

        for (ISpringPlugin plugin : plugins) {
            Assert.assertEquals(plugin.getPluginMethods().size(), 2);
        }
    }

    @AfterClass
    public void stopPlugins() {
        // stop and unload all plugins
        pluginManager.stopPlugins();
    }
}
