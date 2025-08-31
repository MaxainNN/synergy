package io.mkalugin.synergy.controller;

import feign.FeignException;
import io.mkalugin.synergy.dto.JokeDto;
import io.mkalugin.synergy.service.JokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<JokeDto> getJokeResponse() {
        try {
            JokeDto joke = jokeService.getJokes();
            if (joke == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            return ResponseEntity.ok(joke);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).build();
        }
    }
}
