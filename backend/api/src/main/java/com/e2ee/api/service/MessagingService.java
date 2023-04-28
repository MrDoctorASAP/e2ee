package com.e2ee.api.service;

import com.e2ee.api.controller.dto.ChatCreationEventDto;
import com.e2ee.api.controller.dto.MessageEventDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MessagingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void publish(MessageEventDto message) {
        simpMessagingTemplate.convertAndSend("/topic/message", message);
    }

    public void publish(ChatCreationEventDto chatCreation) {
        simpMessagingTemplate.convertAndSend("/topic/chat", chatCreation);
    }

}
