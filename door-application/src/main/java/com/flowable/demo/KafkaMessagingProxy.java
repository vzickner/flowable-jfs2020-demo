package com.flowable.demo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Valentin Zickner
 */
@Component
public class KafkaMessagingProxy {

    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper;

    public KafkaMessagingProxy(SimpMessagingTemplate template, ObjectMapper objectMapper) {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "gate-action-topic")
    public void gateActionTopic(String message) throws JsonProcessingException {
        this.template.convertAndSend("/topic/pushNotification", new Message("gate-action-topic", this.objectMapper.readTree(message)));
    }

    @KafkaListener(topics = "card-scanned-topic")
    public void cardScannedTopic(String message) throws JsonProcessingException {
        this.template.convertAndSend("/topic/pushNotification", new Message("card-scanned-topic", this.objectMapper.readTree(message)));
    }

    private static class Message {
        public final JsonNode message;
        public final String topic;

        private Message(String topic, JsonNode message) {
            this.message = message;
            this.topic = topic;
        }
    }

}
