package com.biit.plugins.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public GreetingsProvider greetProvider(){
        return new GreetingsProvider();
    }
}
