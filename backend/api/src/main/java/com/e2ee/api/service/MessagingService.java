package com.e2ee.api.service;

import com.e2ee.api.controller.dto.MessageEventDto;
import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.repository.entities.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MessagingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void publish(MessageEventDto message) {
        try {
            simpMessagingTemplate.convertAndSend("/topic/message", message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

}
