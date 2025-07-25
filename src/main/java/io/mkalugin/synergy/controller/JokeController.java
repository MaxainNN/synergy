package io.mkalugin.synergy.controller;

import feign.FeignException;
import io.mkalugin.synergy.dto.Joke;
import io.mkalugin.synergy.service.JokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/joke")
public class JokeController {
    private final JokeService jokeService;

    @Autowired
    public JokeController(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    @GetMapping
    public ResponseEntity<?> getJokeResponse() {
        try {
            return ResponseEntity.ok(jokeService.getJokes());
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body("Error getting Joke");
        }
    }
}
