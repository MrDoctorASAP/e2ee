package com.e2ee.api.service;

import com.e2ee.api.controller.dto.UserCredentialsDto;
import com.e2ee.api.controller.dto.UserRegistrationDto;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.exceptons.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void createUser_NotExists() {
        String username = "createUser_NotExists";
        User user = userService.createUser(new UserRegistrationDto(
                new UserCredentialsDto(username, "password"),
                "Bob", "Smit", "Bob.Smit@mail.ru"));
        System.out.println(user);
        assertThat(user.getId(), is(notNullValue()));
        assertThat(user.getUsername(), is(equalTo(username)));
        assertThat(userService.existsByIds(List.of(user.getId())), is(true));
    }

    @Test
    void createUser_Exists() {
        String username = "createUser_Exists";
        userService.createUser(new UserRegistrationDto(
                new UserCredentialsDto(username, "password1"),
                "Bob", "Smit", "Bob.Smit@mail.ru"));
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(new UserRegistrationDto(
                    new UserCredentialsDto(username, "password2"),
                    "Woody", "Maclaren", "WM@mail.ru"));
        });
    }

    @Test
    void loadUserByUsername_Present() {
        String username = "loadUserByUsername_Present";
        String password = "password";
        User user = userService.createUser(new UserRegistrationDto(
                new UserCredentialsDto(username, password),
                "Bob", "Smit", "Bob.Smit@mail.ru"));
        System.out.println(user);
        User actualUser = userService.loadUserByUsername(username);
        System.out.println(actualUser);
        assertThat(actualUser, is(equalTo(user)));
    }

    @Test
    void loadUserByUsername_NotPresent() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("loadUserByUsername_NotPresent");
        });
    }

}