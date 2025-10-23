package com.biit.plugins.tests;

/*-
 * #%L
 * Plugin Manager (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.plugins.PluginController;
import com.biit.plugins.exceptions.DuplicatedPluginFoundException;
import com.biit.plugins.interfaces.IStandardPlugin;
import com.biit.plugins.interfaces.exceptions.NoPluginFoundException;
import com.biit.plugins.springboot.SpringTestPluginApplication;
import com.biit.plugins.test.interfaces.IStandardPlugin2;
import com.biit.plugins.test.interfaces.IStandardPlugin3;
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
        // IPlugin2 must be in the plugin. If not
        // java.lang.InstantiationException appears.
        List<IStandardPlugin2> plugins2 = pluginController.getPlugins(IStandardPlugin2.class);
        Assert.assertEquals(plugins2.size(), 1);

        for (IStandardPlugin2 plugin : plugins2) {
            Assert.assertEquals(plugin.getPluginMethods().size(), 2);
        }

        // IPlugin3 must be in the plugin. If not
        // java.lang.InstantiationException appears.
        List<IStandardPlugin3> plugins3 = pluginController.getPlugins(IStandardPlugin3.class);
        Assert.assertEquals(plugins3.size(), 1);

        for (IStandardPlugin3 plugin : plugins3) {
            Assert.assertEquals(plugin.getPluginMethods().size(), 2);
        }

        // Load all together
        List<IStandardPlugin> plugins1 = pluginController.getPlugins(IStandardPlugin.class);
        //IPlugin2, IPlugin3 extends IPlugin
        Assert.assertEquals(plugins1.size(), 2);

        for (IStandardPlugin plugin : plugins1) {
            Assert.assertEquals(plugin.getPluginMethods().size(), 2);
        }
    }

    @Test
    public void helloWorldPluginSelectionTest1() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoPluginFoundException, DuplicatedPluginFoundException {
        // Calling the first plugin
        IStandardPlugin3 pluginInterface = pluginController.getPlugin(IStandardPlugin3.class, ANOTHER_PLUGIN_ID);
        Assert.assertNotNull(pluginInterface);
        Method method = pluginInterface.getPluginMethod(ANOTHER_PLUGIN_METHOD);
        Assert.assertEquals(method.invoke(pluginInterface), ANOTHER_PLUGIN_METHOD_RETURN);
    }
}
