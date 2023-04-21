package com.e2ee.api;

import com.e2ee.api.controller.dto.AuthenticationTokenDto;
import com.e2ee.api.controller.dto.GroupChatDto;
import com.e2ee.api.controller.dto.MessageDto;
import com.e2ee.api.controller.dto.UserCredentialsDto;
import com.e2ee.api.repository.entities.Chat;
import com.e2ee.api.repository.entities.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatTest {

    @Autowired
    private MockMvc mvc;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void messagingTest() {
        UserCredentialsDto user1 = new UserCredentialsDto("username1", "password1");
        String token1s = mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user1)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthenticationTokenDto token1 = mapper.readValue(token1s, AuthenticationTokenDto.class);
        System.out.println(token1);

        UserCredentialsDto user2 = new UserCredentialsDto("username2", "password2");
        String token2s = mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2)))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthenticationTokenDto token2 = mapper.readValue(token2s, AuthenticationTokenDto.class);
        System.out.println(token2);

        // Create chat
        GroupChatDto chatToCreate = new GroupChatDto("123",
                List.of(token1.getUserId(), token2.getUserId()));
        String chatStr = mvc.perform(post("/api/v1/chat/with")
                        .header("Authorization", "Bearer " + token1.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(chatToCreate)))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Chat chat = mapper.readValue(chatStr, Chat.class);
        System.out.println(chat);

        MessageDto message1 = new MessageDto(chat.getId(), "Hello");

        mvc.perform(post("/api/v1/message/send")
                        .header("Authorization", "Bearer " + token1.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(message1)))
                .andDo(print());

        MessageDto message2 = new MessageDto(chat.getId(), "World");

        mvc.perform(post("/api/v1/message/send")
                        .header("Authorization", "Bearer " + token1.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(message2)))
                .andDo(print());

        String messagesJson = mvc.perform(get("/api/v1/message/messages?chatId=" + chat.getId())
                        .header("Authorization", "Bearer " + token2.getToken()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        mapper.readValue(messagesJson, new TypeReference<List<Message>>() {})
            .forEach(System.out::println);
    }

}
