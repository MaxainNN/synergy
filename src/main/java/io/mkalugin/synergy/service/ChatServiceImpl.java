package io.mkalugin.synergy.service;

import io.mkalugin.synergy.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final KafkaTemplate<String, MessageDto> kafkaTemplate;

    private final Map<String, String> questionAnswerMap = Map.of(
            "what time", "10:00",
            "hello", "Hi there!",
            "how are you", "I'm fine, thank you!",
            "what's your name", "I'm Chat Service"
    );

    public ChatServiceImpl(KafkaTemplate<String, MessageDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String processQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "Please ask a question";
        }

        String lowerQuestion = question.toLowerCase().trim();

        String answer = questionAnswerMap.entrySet().stream()
                .filter(entry -> lowerQuestion.contains(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse("Sorry, I don't understand that question");

        log.info("Question: '{}' -> Answer: '{}'", question, answer);
        return answer;
    }

    @KafkaListener(
            topics = "chat-consumer-topic",
            groupId = "chat-group")
//            containerFactory = "kafkaListenerContainerFactory")
    public void processMessage(@Payload MessageDto chatMessage) {
        log.info("Received message: {}", chatMessage.getMessage());

        String answer = processQuestion(chatMessage.getMessage());

        MessageDto response = new MessageDto();
        response.setMessage("Response to: " + chatMessage.getMessage());
        response.setResponse(answer);

        kafkaTemplate.send("chat-producer-topic", response);
        log.info("Sent response: {}", answer);
    }

    @Bean
    public Supplier<Message<MessageDto>> produceMessage() {
        return () -> {
            // Здесь можно реализовать периодическую отправку сообщений
            return null;
        };
    }

    public void sendMessage(String message) {
        MessageDto chatMessage = new MessageDto(message);
        kafkaTemplate.send("chat-producer-topic", chatMessage);
        log.info("Sent message: {}", message);
    }
}
