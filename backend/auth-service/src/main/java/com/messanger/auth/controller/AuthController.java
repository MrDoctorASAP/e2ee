package com.messanger.auth.controller;

import com.messanger.auth.controller.dto.AuthenticationJwtToken;
import com.messanger.auth.controller.dto.UserAuthenticationDetails;
import com.messanger.auth.repository.entities.User;
import com.messanger.auth.security.jwt.JwtTokenProvider;
import com.messanger.auth.service.UserAuthenticationService;
import com.messanger.auth.service.UserService;
import com.messanger.auth.service.exceptons.UserAlreadyExistsException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider provider;
    private final AuthenticationManager manager;
    private final UserAuthenticationService authService;

    public AuthController(UserService userService, JwtTokenProvider provider, AuthenticationManager manager, UserAuthenticationService authService) {
        this.userService = userService;
        this.provider = provider;
        this.manager = manager;
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthenticationJwtToken login(@Valid @RequestBody UserAuthenticationDetails details) {
        return authenticate(details.getUsername(), details.getPassword());
    }

    @PostMapping("/register")
    public AuthenticationJwtToken register(@Valid @RequestBody UserAuthenticationDetails details) {
        userService.createUser(details.getUsername(), details.getPassword());
        return authenticate(details.getUsername(), details.getPassword());
    }

    @PostMapping("/extend")
    public AuthenticationJwtToken extend() throws AuthenticationException {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(AuthenticationException::new);
        String username = user.getUsername();
        String token = provider.createToken(username);
        return new AuthenticationJwtToken(username, token);
    }

    private AuthenticationJwtToken authenticate(String username, String password) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        String token = provider.createToken(username);
        log.info("User authenticated: {}", username);
        return new AuthenticationJwtToken(username, token);
    }

    @SneakyThrows
    @ExceptionHandler(AuthenticationException.class)
    private ModelAndView handleAuthException(HttpServletResponse response,
                                             AuthenticationException e) {
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
