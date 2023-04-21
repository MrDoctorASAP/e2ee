package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.ApiErrorDto;
import com.e2ee.api.controller.dto.AuthenticationTokenDto;
import com.e2ee.api.controller.dto.UserCredentialsDto;
import com.e2ee.api.controller.dto.UserRegistrationDto;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.UserAuthenticationService;
import com.e2ee.api.service.UserService;
import com.e2ee.api.service.exceptons.AuthException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final UserAuthenticationService authService;

    @PostMapping("/login")
    public AuthenticationTokenDto login(@Valid @RequestBody UserCredentialsDto details) {
        return authService.authenticate(details.getUsername(), details.getPassword());
    }

    @PostMapping("/register")
    public AuthenticationTokenDto register(@Valid @RequestBody UserRegistrationDto details) {
        userService.createUser(details);
        return authService.authenticate(details.getCredentials().getUsername(), details.getCredentials().getPassword());
    }

    @PostMapping("/extend")
    public AuthenticationTokenDto extend() {
        User user = authService.getAuthenticatedUser();
        return authService.expand(user);
    }

    @ExceptionHandler(AuthenticationException.class)
    private ResponseEntity<ApiErrorDto> handleAuthException(AuthenticationException e) {
        log.debug(e.getMessage(), e);
        return ApiErrorDto.exception(HttpStatus.UNAUTHORIZED, e);
    }

}
