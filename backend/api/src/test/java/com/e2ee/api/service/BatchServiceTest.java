package com.e2ee.api.service;

import com.e2ee.api.DevDataInit;
import com.e2ee.api.controller.BatchController;
import com.e2ee.api.controller.dto.FlatLastMessage;
import com.e2ee.api.controller.dto.FlatUnseenChat;
import com.e2ee.api.repository.MessageRepository;
import com.e2ee.api.repository.UnseenMessageRepository;
import com.e2ee.api.repository.batch.BatchRepository;
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
    @Autowired UnseenMessageRepository unseenMessageRepository;
    @Autowired BatchRepository batchRepository;
    @Autowired MessageRepository messageRepository;

    @Autowired DevDataInit devDataInit;

    ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();


//    @Test
//    @SneakyThrows
//    void getChats() {
//        User admin = userService.loadUserByUsername("admin");
//        List<BatchController.BatchChat> chats = batchService.getChats(admin);
//        System.out.println(writer.writeValueAsString(chats));
//    }
//
//    @Test
//    @SneakyThrows
//    void getMessages() {
//        User admin = userService.loadUserByUsername("admin");
//        List<BatchController.BatchMessage> messages = batchService.getMessages(admin, 1L);
//        System.out.println(writer.writeValueAsString(messages));
//    }

    @Test
    @SneakyThrows
    void getUnseenChats() {
        devDataInit.createDefault();
        List<FlatUnseenChat> chats = unseenMessageRepository.getUnseenChatsByUserId(1L);
        System.out.println(writer.writeValueAsString(chats));
    }

    @Test
    @SneakyThrows
    void getLastMessages() {
        devDataInit.createDefault();
        List<FlatLastMessage> messages = messageRepository.findLastMessagesByUserId(1L);
        System.out.println(writer.writeValueAsString(messages));
    }

}