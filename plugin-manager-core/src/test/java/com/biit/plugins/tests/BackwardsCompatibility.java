package com.biit.plugins.tests;

import com.biit.plugins.PluginController;
import com.biit.plugins.springboot.SpringTestPluginApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringTestPluginApplication.class)
@Test(groups = {"backwardsCompatibility"})
public class BackwardsCompatibility extends AbstractTestNGSpringContextTests {

    @Autowired
    private PluginController pluginController;

    @Test
    public void checkCompatibilityWithInstance(){
        Assert.assertNotNull(PluginController.getInstance());
    }
}
