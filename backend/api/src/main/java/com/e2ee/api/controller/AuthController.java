package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.AuthenticationTokenDto;
import com.e2ee.api.controller.dto.UserCredentialsDto;
import com.e2ee.api.controller.dto.UserRegistrationDto;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.security.jwt.JwtTokenProvider;
import com.e2ee.api.service.UserAuthenticationService;
import com.e2ee.api.service.UserService;
import com.e2ee.api.service.exceptons.AuthException;
import com.e2ee.api.service.exceptons.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider provider;
    private final AuthenticationManager manager;
    private final UserAuthenticationService authService;

    @PostMapping("/login")
    public AuthenticationTokenDto login(@Valid @RequestBody UserCredentialsDto details) {
        return authenticate(details.getUsername(), details.getPassword());
    }

    @Deprecated
    @PostMapping("/loginOrRegister")
    public AuthenticationTokenDto loginOrRegister(@Valid @RequestBody UserCredentialsDto details) {
        userService.createUserIfAbsent(details);
        return authenticate(details.getUsername(), details.getPassword());
    }

    @PostMapping("/register")
    public AuthenticationTokenDto register(@Valid @RequestBody UserRegistrationDto details) {
        userService.createUser(details);
        return authenticate(details.getCredentials().getUsername(), details.getCredentials().getPassword());
    }

    @PostMapping("/extend")
    public AuthenticationTokenDto extend() {
        User user = authService.getAuthenticatedUser();
        String username = user.getUsername();
        String token = provider.createToken(username);
        return new AuthenticationTokenDto(user.getId(), username, token);
    }

    private AuthenticationTokenDto authenticate(String username, String password) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        String token = provider.createToken(username);
        Long userId = userService.loadUserByUsername(username).getId();
        log.info("User authenticated: {}", username);
        return new AuthenticationTokenDto(userId, username, token);
    }

    @SneakyThrows
    @ExceptionHandler(AuthException.class)
    private ModelAndView handleAuthException(HttpServletResponse response,
                                             AuthException e) {
        log.info("Auth failed", e);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return new ModelAndView();
    }

    @SneakyThrows
    @ExceptionHandler(UserAlreadyExistsException.class)
    private ModelAndView handleUserAlreadyExistsException(HttpServletResponse response,
                                                          UserAlreadyExistsException e) {
        log.info("Register failed: {}", e.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return new ModelAndView();
    }

    @SneakyThrows
    @ExceptionHandler(UsernameNotFoundException.class)
    private ModelAndView handleAuthException(HttpServletResponse response,
                                             UsernameNotFoundException e) {
        log.info("Auth failed: {}", e.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return new ModelAndView();
    }

    @SneakyThrows
    @ExceptionHandler(BadCredentialsException.class)
    private ModelAndView handleAuthCredentialsException(HttpServletResponse response,
                                                        BadCredentialsException e) {
        log.info("Auth failed: {}", e.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return new ModelAndView();
    }

}
