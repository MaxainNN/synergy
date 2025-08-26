package io.mkalugin.synergy.controller;

import io.mkalugin.synergy.service.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/chat")
@RequiredArgsConstructor
public class ChatController {

    @Autowired
    private ChatServiceImpl chatService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        chatService.sendMessage(message);
        return ResponseEntity.ok("Message sent");
    }

    @PostMapping("/question")
    public ResponseEntity<String> askQuestion(@RequestBody String question) {
        String answer = chatService.processQuestion(question);
        return ResponseEntity.ok(answer);
    }
}
