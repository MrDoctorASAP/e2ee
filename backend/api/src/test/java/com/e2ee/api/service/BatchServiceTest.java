package com.e2ee.api.service;

import com.e2ee.api.controller.BatchController;
import com.e2ee.api.repository.batch.BatchChatImpl;
import com.e2ee.api.repository.batch.BatchRepository;
import com.e2ee.api.repository.batch.IBatchChat;
import com.e2ee.api.repository.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BatchServiceTest {

    @Autowired UserService userService;
    @Autowired BatchService batchService;
    @Autowired BatchRepository batchRepository;

    ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();


    @Test
    @SneakyThrows
    void getChats() {
        User admin = userService.loadUserByUsername("admin");
        List<BatchController.BatchChat> chats = batchService.getChats(admin);
        System.out.println(writer.writeValueAsString(chats));
    }

    @Test
    @SneakyThrows
    void getMessages() {
        User admin = userService.loadUserByUsername("admin");
        List<BatchController.BatchMessage> messages = batchService.getMessages(admin, 1L);
        System.out.println(writer.writeValueAsString(messages));
    }
}