package com.biit.plugins.springboot.plugin;

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

import com.biit.plugins.SpringBasePlugin;
import com.biit.plugins.configuration.PluginManagerFactory;
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
        super.start();
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
        applicationContext.register(PluginManagerFactory.class);
        applicationContext.refresh();
        return applicationContext;
    }

    @Extension(ordinal = 1)
    public static class SpringPlugin extends SpringBasePlugin implements SpringGreetingPlugin {

        @Autowired
        private GreetingsProvider greetProvider;


        @Override
        public String methodGetGreeting() {
            return greetProvider.provide();
        }

        @Override
        public List<Object> restControllers() {
            return new ArrayList<Object>() {{
                add(new TestPluginController());
            }};
        }

        public String getPluginName() {
            return "SpringBootPlugin";
        }

        /**
         * Methods that starts with "method" are selectables.
         */
        public void methodOne() {
            return;
        }
    }

}
