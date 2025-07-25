package io.mkalugin.synergy.client;

import feign.RequestLine;
import io.mkalugin.synergy.dto.Joke;


public interface JokeClient {
    @RequestLine("GET /random_joke")
    Joke getJoke();
}
