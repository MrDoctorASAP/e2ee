package com.e2ee.api.service;

import com.e2ee.api.controller.dto.ChatCreationEventDto;
import com.e2ee.api.controller.dto.MessageEventDto;
import com.e2ee.api.controller.dto.secure.AcceptedSecureChatDto;
import com.e2ee.api.controller.dto.secure.RecipientKeyDto;
import com.e2ee.api.controller.dto.secure.SecureChatInviteDto;
import com.e2ee.api.repository.entities.secure.SecureChatInvite;
import com.e2ee.api.repository.entities.secure.SecureChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public void publish(Long userId, SecureChatInviteDto invite) {
        simpMessagingTemplate.convertAndSend("/topic/invite", new Event<>(userId, invite));
    }

    public void publish(Long userId, RecipientKeyDto recipientKey) {
        simpMessagingTemplate.convertAndSend("/topic/exchange", new Event<>(userId, recipientKey));
    }

    public void publish(Long userId, SecureChatMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/secureMessage", new Event<>(userId, message));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Event<T> {
        private Long userId;
        private T event;
    }

}
