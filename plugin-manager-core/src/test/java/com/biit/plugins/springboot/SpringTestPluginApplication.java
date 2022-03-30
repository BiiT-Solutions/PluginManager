package com.biit.plugins.springboot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.biit.plugins")
public class SpringTestPluginApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringTestPluginApplication.class, args);
    }
}
