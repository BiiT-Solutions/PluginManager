package com.biit.plugins.tests;

import com.biit.plugins.PluginController;
import com.biit.plugins.helloworld.Greeting;
import com.biit.plugins.interfaces.ICommonPlugin;
import com.biit.plugins.interfaces.ISpringPlugin;
import com.biit.plugins.springboot.SpringTestPluginApplication;
import com.biit.plugins.test.interfaces.ICommonPlugin2;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringTestPluginApplication.class)
@Test(groups = {"pluginLoader"})
public class PluginLoaderTests extends AbstractTestNGSpringContextTests {
    private final static String PLUGINS_FOLDER = "src/test/plugins";

    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private PluginController pluginController;


    @Test
    public void loadPlugin() {
        // retrieve all extensions for "Greeting" extension point
        List<Greeting> plugins = pluginManager.getExtensions(Greeting.class);
        //Gets one value for each plugin.
        Assert.assertEquals(plugins.size(), 1);

        for (Greeting plugin : plugins) {
            Assert.assertEquals(plugin.getGreeting(), "Welcome");
        }
    }

    @Test
    public void loadPlugins() {
        Map<Class<?>, List<?>> plugins = pluginController.getAllPluginsByClass();
        Assert.assertEquals(plugins.get(ICommonPlugin.class).size(), 2);
        Assert.assertEquals(plugins.get(ISpringPlugin.class).size(), 1);

        Assert.assertEquals(pluginController.getAllPlugins().size(), 3);
    }

    @Test
    public void loadIPlugin() {
        // IPlugin2 must be in the plugin. If not java.lang.InstantiationException appears.
        List<ICommonPlugin2> plugins = pluginManager.getExtensions(ICommonPlugin2.class);
        //Gets one value for each plugin.
        Assert.assertEquals(plugins.size(), 1);

        for (ICommonPlugin2 plugin : plugins) {
            Assert.assertEquals(plugin.getPluginMethods().size(), 2);
        }
    }

    @Test
    public void loadSpringBootPlugin() {
        // IPlugin2 must be in the plugin. If not java.lang.InstantiationException appears.
        List<ISpringPlugin> plugins = pluginManager.getExtensions(ISpringPlugin.class);
        //Gets one value for each plugin.
        Assert.assertEquals(plugins.size(), 1);

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
