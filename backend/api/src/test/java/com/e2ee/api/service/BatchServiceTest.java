package com.e2ee.api.service;

import com.e2ee.api.DevelopmentInit;
import com.e2ee.api.controller.BatchController;
import com.e2ee.api.controller.dto.FlatLastMessage;
import com.e2ee.api.controller.dto.FlatUnseenChat;
import com.e2ee.api.repository.MessageRepository;
import com.e2ee.api.repository.UnseenMessageRepository;
import com.e2ee.api.repository.batch.BatchRepository;
import com.e2ee.api.repository.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BatchServiceTest {

    @Autowired UserService userService;
    @Autowired BatchService batchService;
    @Autowired UnseenMessageRepository unseenMessageRepository;
    @Autowired BatchRepository batchRepository;
    @Autowired MessageRepository messageRepository;

    @Autowired
    DevelopmentInit devDataInit;

    ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

    @Test
    @SneakyThrows
    void getChats() {
        User admin = userService.loadUserByUsername("admin");
        var chats = batchService.getChats(admin);
        System.out.println(writer.writeValueAsString(chats));
    }

    @Test
    @SneakyThrows
    void getMessages() {
        User admin = userService.loadUserByUsername("admin");
        var messages = batchService.getMessages(admin, 1L, 0, Integer.MAX_VALUE);
        System.out.println(writer.writeValueAsString(messages));
    }

    @Test
    @SneakyThrows
    void getUnseenChats() {
        List<FlatUnseenChat> chats = unseenMessageRepository.getUnseenChatsByUserId(1L);
        System.out.println(writer.writeValueAsString(chats));
    }

    @Test
    @SneakyThrows
    void getLastMessages() {
        List<FlatLastMessage> messages = messageRepository.findLastMessagesByUserId(1L);
        System.out.println(writer.writeValueAsString(messages));
    }

}