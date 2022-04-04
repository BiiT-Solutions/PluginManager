package com.biit.plugins.tests;

import com.biit.plugins.PluginController;
import com.biit.plugins.exceptions.DuplicatedPluginFoundException;
import com.biit.plugins.interfaces.ICommonPlugin;
import com.biit.plugins.interfaces.exceptions.NoPluginFoundException;
import com.biit.plugins.springboot.SpringTestPluginApplication;
import com.biit.plugins.test.interfaces.ICommonPlugin2;
import com.biit.plugins.test.interfaces.ICommonPlugin3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringTestPluginApplication.class)
@Test(groups = {"pluginController"})
public class TestPluginControllerTests extends AbstractTestNGSpringContextTests {
    private final static String ANOTHER_PLUGIN_ID = "another-plugin";
    private final static String ANOTHER_PLUGIN_METHOD = "methodGetGreeting";
    private final static String ANOTHER_PLUGIN_METHOD_RETURN = "Another greeting";

    @Autowired
    private PluginController pluginController;

    @Test
    public void loadIPlugin() throws NoPluginFoundException, DuplicatedPluginFoundException {
        // IPLugin2 must be in the plugin. If not
        // java.lang.InstantiationException appears.
        List<ICommonPlugin2> plugins2 = pluginController.getPlugins(ICommonPlugin2.class);
        Assert.assertEquals(plugins2.size(), 1);

        for (ICommonPlugin2 plugin : plugins2) {
            Assert.assertEquals(plugin.getPluginMethods().size(), 2);
        }

        // IPlugin3 must be in the plugin. If not
        // java.lang.InstantiationException appears.
        List<ICommonPlugin3> plugins3 = pluginController.getPlugins(ICommonPlugin3.class);
        Assert.assertEquals(plugins3.size(), 1);

        for (ICommonPlugin3 plugin : plugins3) {
            Assert.assertEquals(plugin.getPluginMethods().size(), 2);
        }

        // Load all toghether
        List<ICommonPlugin> plugins1 = pluginController.getPlugins(ICommonPlugin.class);
        //IPlugin2, IPlugin3 extends IPlugin
        Assert.assertEquals(plugins1.size(), 2);

        for (ICommonPlugin plugin : plugins1) {
            Assert.assertEquals(plugin.getPluginMethods().size(), 2);
        }
    }

    @Test
    public void helloWorldPluginSelectionTest1() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoPluginFoundException, DuplicatedPluginFoundException {
        // Calling the first plugin
        ICommonPlugin3 pluginInterface = pluginController.getPlugin(ICommonPlugin3.class, ANOTHER_PLUGIN_ID);
        Assert.assertNotNull(pluginInterface);
        Method method = pluginInterface.getPluginMethod(ANOTHER_PLUGIN_METHOD);
        Assert.assertEquals(method.invoke(pluginInterface), ANOTHER_PLUGIN_METHOD_RETURN);
    }
}
