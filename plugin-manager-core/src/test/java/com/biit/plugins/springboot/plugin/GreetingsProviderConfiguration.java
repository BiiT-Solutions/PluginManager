package com.biit.plugins.springboot.plugin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GreetingsProviderConfiguration {

    @Bean
    public GreetingsProvider greetProvider(){
        return new GreetingsProvider();
    }
}
