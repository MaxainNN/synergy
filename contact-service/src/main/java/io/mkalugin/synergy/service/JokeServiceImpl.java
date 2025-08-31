package io.mkalugin.synergy.service;

import io.mkalugin.synergy.client.JokeClient;
import io.mkalugin.synergy.dto.JokeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JokeServiceImpl implements JokeService {
    private final JokeClient jokeClient;

    @Autowired
    public JokeServiceImpl(JokeClient jokeClient) {
        this.jokeClient = jokeClient;
    }

    @Override
    public JokeDto getJokes() {
        return jokeClient.getJoke();
    }
}
