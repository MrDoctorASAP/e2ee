package com.e2ee.api;

import com.e2ee.api.controller.dto.AuthenticationTokenDto;
import com.e2ee.api.controller.dto.UserCredentialsDto;
import com.e2ee.api.controller.dto.UserRegistrationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    @SneakyThrows
    void registerTest() {

        String username = "registerTestUser";
        String password = "password";

        UserRegistrationDto details =
                new UserRegistrationDto(new UserCredentialsDto(username, password),
                        "User", "Name", "username@mail.ru");

        mvc.perform(post("/api/v1/auth/register")
                        .content(mapper.writeValueAsString(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(not(emptyString()))))
                .andExpect(jsonPath("$.token", is(not(emptyString()))))
                .andExpect(jsonPath("$.username", is(equalTo(username))));
    }

    @Test
    @SneakyThrows
    public void login() {

        String username = "loginTestUser";
        String password = "password";

        UserCredentialsDto credentials = new UserCredentialsDto(username, password);
        UserRegistrationDto details = new UserRegistrationDto(credentials, "User", "Name", "username@mail.ru");

        mvc.perform(post("/api/v1/auth/register")
                        .content(mapper.writeValueAsString(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(not(emptyString()))))
                .andExpect(jsonPath("$.token", is(not(emptyString()))))
                .andExpect(jsonPath("$.username", is(equalTo(username))));

        mvc.perform(post("/api/v1/auth/login")
                        .content(mapper.writeValueAsString(credentials))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(not(emptyString()))))
                .andExpect(jsonPath("$.token", is(not(emptyString()))))
                .andExpect(jsonPath("$.username", is(equalTo(username))));

    }

    @Test
    @SneakyThrows
    public void publicResourceAccess() {

        String username = "publicResourceAccessTestUser";
        String password = "password";

        UserCredentialsDto credentials = new UserCredentialsDto(username, password);
        UserRegistrationDto details = new UserRegistrationDto(credentials, "User", "Name", "username@mail.ru");

        String registerResponse = mvc.perform(post("/api/v1/auth/register")
                        .content(mapper.writeValueAsString(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(not(emptyString()))))
                .andExpect(jsonPath("$.token", is(not(emptyString()))))
                .andExpect(jsonPath("$.username", is(equalTo(username))))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = mapper.readValue(registerResponse, AuthenticationTokenDto.class).getUserId();

        mvc.perform(get("/api/v1/user/profile").queryParam("userId", userId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(equalTo(username))));

    }

    @Test
    @SneakyThrows
    public void securedResourceAccess() {

        String username = "securedResourceAccessTestUser";
        String password = "password";

        UserCredentialsDto credentials = new UserCredentialsDto(username, password);
        UserRegistrationDto details = new UserRegistrationDto(credentials, "User", "Name", "username@mail.ru");

        String registerResponse = mvc.perform(post("/api/v1/auth/register")
                        .content(mapper.writeValueAsString(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(not(emptyString()))))
                .andExpect(jsonPath("$.token", is(not(emptyString()))))
                .andExpect(jsonPath("$.username", is(equalTo(username))))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var token = mapper.readValue(registerResponse, AuthenticationTokenDto.class);

        mvc.perform(get("/api/v1/chat/chats")
                        .header("Authorization", "Bearer " + token.getToken()))
                .andDo(print())
                .andExpect(status().isOk());

    }

}
