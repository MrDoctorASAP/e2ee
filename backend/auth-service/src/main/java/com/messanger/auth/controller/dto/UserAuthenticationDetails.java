package com.messanger.auth.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserAuthenticationDetails {

    @NotBlank(message = "username required")
    @Size(min = 3, max = 255)
    private final String username;

    @NotBlank(message = "password required")
    @Size(min = 3, max = 255)
    private final String password;

}
