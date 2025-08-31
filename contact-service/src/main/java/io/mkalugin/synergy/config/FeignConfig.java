package io.mkalugin.synergy.config;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import io.mkalugin.synergy.client.JokeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public JokeClient createJokeClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(FeignConfig.class))
                .logLevel(Logger.Level.BASIC)
                .target(JokeClient.class, "https://official-joke-api.appspot.com/");
    }
}
