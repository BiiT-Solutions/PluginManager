package com.biit.plugins.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pf4j.*;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.nio.file.Paths;

@Configuration
public class PluginConfiguration implements BeanFactoryAware {

    private final SpringPluginManager pluginManager;
    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper;
    private BeanFactory beanFactory;

    @Autowired
    public PluginConfiguration(SpringPluginManager pluginManager, ApplicationContext applicationContext, ObjectMapper objectMapper) {
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
