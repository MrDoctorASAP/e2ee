package com.example.sock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSender {

    private final SimpMessagingTemplate messagingTemplate;
    private final AtomicLong messageNumber = new AtomicLong();

    @Scheduled(initialDelay = 30, fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void sendMessage() {
        MessageObject payload = new MessageObject("user", "message" + messageNumber.incrementAndGet());
        messagingTemplate.convertAndSend("/topic/message", payload);
        log.info("Message sent {}", payload);
    }

}
