package com.messanger.auth.controller.dto;

import lombok.Data;

@Data
public class AuthenticationJwtToken {
    private final String username;
    private final String token;
}
