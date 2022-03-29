package com.biit.plugins.springboot;

import com.biit.plugins.configuration.ApplicationConfiguration;
import com.biit.plugins.logger.PluginManagerLogger;
import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class SpringSamplePlugin extends SpringPlugin {

    public SpringSamplePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        PluginManagerLogger.info(this.getClass().getName(), "Spring Sample plugin.start()");
    }

    @Override
    public void stop() {
        PluginManagerLogger.info(this.getClass().getName(),"Spring Sample plugin.stop()");
        super.stop(); // to close applicationContext
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(ApplicationConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }

    @Extension(ordinal = 1)
    public static class SpringPlugin implements SpringGreetingPlugin {

        @Autowired
        private GreetingsProvider greetProvider;


        @Override
        public String getGreeting() {
            return greetProvider.provide();
        }

        @Override
        public List<Object> restControllers() {
            return new ArrayList<Object>() {{
                add(new TestPluginController());
            }};
        }
    }

}
