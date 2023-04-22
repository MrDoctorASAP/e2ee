package com.e2ee.api;

import com.e2ee.api.controller.dto.UserRegistrationDto;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class TestSupportService {

    private final AtomicLong userNumber = new AtomicLong(1L);

    private final UserService userService;

    public User createUser() {
        long number = userNumber.getAndIncrement();
        return userService.createUser(UserRegistrationDto.sampleUser(
                "testUser" + number,
                "password" + number
        ));
    }

}
