package com.biit.plugins.configuration;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.pf4j.PluginManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PluginConfiguration implements BeanFactoryAware {

    private final PluginManager pluginManager;
    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper;
    private BeanFactory beanFactory;

    @Autowired
    public PluginConfiguration(PluginManager pluginManager, ApplicationContext applicationContext, ObjectMapper objectMapper) {
        this.pluginManager = pluginManager;
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @PreDestroy
    public void cleanup() {
        pluginManager.stopPlugins();
    }


}
