package com.biit.plugins.tests.springboot;

import com.biit.plugins.springboot.SpringTestPluginApplication;
import com.biit.plugins.springboot.TestPluginController;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringTestPluginApplication.class)
@ExtendWith(MockitoExtension.class)
@Test(groups = "springboot")
public class SpringPluginTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeClass
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    public void checkGreetingsService() throws Exception {
        //Info services are opened in rest-server library
        mockMvc.perform(get("/plugin-mvc-controller/greetings"))
                .andExpect(status().isOk())
                .andExpect(content().string(TestPluginController.GREETINGS_MESSAGE));
    }
}
