package com.e2ee.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    private UserCredentialsDto credentials;
    private String firstName;
    private String lastName;
    private String email;

    public static UserRegistrationDto create(String username, String password) {
        return new UserRegistrationDto(new UserCredentialsDto(username, password),
                username, username, username + "@gmail.com");
    }

}
