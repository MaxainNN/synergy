package io.mkalugin.synergy.client;

import feign.RequestLine;
import io.mkalugin.synergy.dto.JokeDto;


public interface JokeClient {
    @RequestLine("GET /random_joke")
    JokeDto getJoke();
}
