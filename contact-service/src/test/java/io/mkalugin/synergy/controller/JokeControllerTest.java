package io.mkalugin.synergy.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JokeControllerTest {

    private static final Logger log = LoggerFactory.getLogger(JokeControllerTest.class);

    @Autowired
    public MockMvc mockMvc;

    @Test
    @DisplayName("Not empty response")
    void whenGetJoke_thenReturnsNonEmptyResponse() throws Exception {
        log.info("Starting test: whenGetJoke_thenReturnsNonEmptyResponse");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/joke")
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.setup").exists())
                .andReturn();

        log.info("Test completed successfully. Response: {}",
                result.getResponse().getContentAsString());
    }
}
