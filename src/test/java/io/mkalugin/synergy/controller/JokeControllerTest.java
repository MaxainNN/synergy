package io.mkalugin.synergy.controller;

import feign.FeignException;
import io.mkalugin.synergy.dto.Joke;
import io.mkalugin.synergy.service.JokeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JokeController.class)
@Import(JokeControllerTest.TestConfig.class)
class JokeControllerTest {

    private static final Logger log = LoggerFactory.getLogger(JokeControllerTest.class);

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private JokeService jokeService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public JokeService jokeService() {
            return Mockito.mock(JokeService.class);
        }
    }

    @Test
    @DisplayName("Not empty response")
    void whenGetJoke_thenReturnsNonEmptyResponse() throws Exception {
        try {
            log.info("Starting test: whenGetJoke_thenReturnsNonEmptyResponse");

            Joke testJoke = new Joke(1,"Test setup", "Test punchline","test type");
            Mockito.when(jokeService.getJokes()).thenReturn(testJoke);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/joke")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.setup").exists())
                    .andReturn();

            log.info("Test completed successfully. Response: {}",
                    result.getResponse().getContentAsString());
        } catch (Exception e) {
            log.error("Test failed with exception: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    @DisplayName("When service returns null")
    void whenServiceReturnsNull_thenReturnsError() throws Exception {
        try {
            log.info("Starting test: whenServiceReturnsNull_thenReturnsError");

            Mockito.when(jokeService.getJokes()).thenReturn(null);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/joke")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andReturn();

            log.info("Test completed with expected error. Response status: {}",
                    result.getResponse().getStatus());

        } catch (Exception e) {
            log.error("Test failed unexpectedly: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    @DisplayName("If get Feign exception - get error response")
    void whenServiceThrowsFeignException_thenReturnsErrorResponse() throws Exception {
        try {
            log.info("Starting test: whenServiceThrowsFeignException_thenReturnsErrorResponse");

            FeignException feignException = Mockito.mock(FeignException.class);
            Mockito.when(feignException.status()).thenReturn(503);
            Mockito.when(jokeService.getJokes()).thenThrow(feignException);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/joke")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().string("Error getting Joke"))
                    .andReturn();

            log.info("Test completed successfully. Response: {}",
                    result.getResponse().getContentAsString());
        } catch (Exception e) {
            log.error("Test failed with exception: {}", e.getMessage());
            throw e;
        }
    }
}
