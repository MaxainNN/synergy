package io.mkalugin.synergy.kafka;

import io.mkalugin.synergy.dto.MessageDto;
import io.mkalugin.synergy.service.ChatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Kafka tests")
@EmbeddedKafka(partitions = 1,
        topics = {"chat-consumer-topic", "chat-producer-topic"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:29092", "port=29092"})
class ChatServiceKafkaTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private KafkaTemplate<String, MessageDto> kafkaTemplate;

    private final BlockingQueue<MessageDto> receivedMessages = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "chat-producer-topic",
            groupId = "test-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(MessageDto message) {
        System.out.println("Test listener received: " + message.getResponse());
        receivedMessages.offer(message);
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        receivedMessages.clear();
        Thread.sleep(1000);
    }

    @Test
    @Order(1)
    @DisplayName("Sending and receive message")
    void testSendAndReceiveMessage() throws InterruptedException {
        MessageDto testMessage = new MessageDto("what time");
        kafkaTemplate.send("chat-consumer-topic", testMessage);

        MessageDto response = receivedMessages.poll(10, TimeUnit.SECONDS);

        assertNotNull(response, "Response should not be null - timeout waiting for response");
        assertNotNull(response.getResponse(), "Response message should not be null");
        assertEquals("10:00", response.getResponse());
        System.out.println("Received response: " + response.getResponse());
    }

    @Test
    @Order(2)
    @DisplayName("Sending hello")
    void testSpecificQuestionAnswer() throws InterruptedException {
        MessageDto question = new MessageDto("hello");
        kafkaTemplate.send("chat-consumer-topic", question);

        MessageDto response = receivedMessages.poll(10, TimeUnit.SECONDS);

        assertNotNull(response, "Response should not be null - timeout waiting for response");
        assertEquals("Hi there!", response.getResponse());
    }

    @Test
    @Order(3)
    @DisplayName("Sending unknown message")
    void testUnknownQuestion() throws InterruptedException {
        MessageDto question = new MessageDto("unknown question");
        kafkaTemplate.send("chat-consumer-topic", question);

        MessageDto response = receivedMessages.poll(10, TimeUnit.SECONDS);

        assertNotNull(response, "Response should not be null - timeout waiting for response");
        assertEquals("Sorry, I don't understand that question", response.getResponse());
    }
}