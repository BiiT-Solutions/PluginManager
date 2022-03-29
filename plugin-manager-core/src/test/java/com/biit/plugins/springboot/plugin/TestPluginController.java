package com.biit.plugins.springboot.plugin;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plugin-mvc-controller")
public class TestPluginController {
    public final static String GREETINGS_MESSAGE =  "An endpoint defined by annotation in plugin";

    @GetMapping(value = "/greetings", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String greetings(){
        return GREETINGS_MESSAGE;
    }
}
