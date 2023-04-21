package com.e2ee.api.service;

import com.e2ee.api.TestSupportService;
import com.e2ee.api.controller.dto.UserCredentialsDto;
import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.controller.dto.UserProfileDto;
import com.e2ee.api.controller.dto.UserRegistrationDto;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.repository.entities.UserProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserProfileServiceTest {

    @Autowired
    UserProfileService profileService;

    @Autowired
    UserService userService;

    @Autowired
    TestSupportService testSupport;

    @Test
    void getUserProfile() {

        String username = "getUserProfile";
        String password = "password";
        String firstName = "Bob";
        String lastName = "Smit";
        String email = "Bob.Smit@mail.ru";

        User user = userService.createUser(new UserRegistrationDto(
                new UserCredentialsDto(username, password),
                firstName, lastName, email));
        System.out.println(user);

        Long userId = user.getId();
        UserProfileDto profile = profileService.getUserProfile(userId);
        assertThat(profile.getUserId(), is(equalTo(userId)));
        assertThat(profile.getUsername(), is(equalTo(username)));
        assertThat(profile.getFirstName(), is(equalTo(firstName)));
        assertThat(profile.getLastName(), is(equalTo(lastName)));
        assertThat(profile.getEmail(), is(equalTo(email)));
    }

    @Test
    void getUsers() {
        Map<Long, UserProfileDto> users = Stream.generate(() -> testSupport.createUser())
                .limit(10)
                .map(User::getId)
                .collect(Collectors.toMap(i->i, profileService::getUserProfile));
        List<UserDto> usersDto = profileService.getUsers(users.keySet());
        for (UserDto userDto : usersDto) {
            UserProfileDto user = users.get(userDto.getUserId());
            assertThat(user, is(notNullValue()));
            assertThat(userDto.getUserId(), is(equalTo(user.getUserId())));
            assertThat(userDto.getUsername(), is(equalTo(user.getUsername())));
            assertThat(userDto.getFirstName(), is(equalTo(user.getFirstName())));
            assertThat(userDto.getLastName(), is(equalTo(user.getLastName())));
        }
    }
}