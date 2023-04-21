package com.e2ee.api.service;

import com.e2ee.api.controller.dto.AuthenticationTokenDto;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.security.jwt.JwtTokenProvider;
import com.e2ee.api.service.exceptons.AuthException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserAuthenticationService {

    private final UserService userService;
    private final JwtTokenProvider provider;
    private final AuthenticationManager manager;

    public User getAuthenticatedUser() throws AuthException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new AuthException("Not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        throw new AuthException("Not authenticated");
    }

    public AuthenticationTokenDto authenticate(String username, String password) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        String token = provider.createToken(username);
        Long userId = userService.loadUserByUsername(username).getId();
        log.info("User authenticated: {}", username);
        return new AuthenticationTokenDto(userId, username, token);
    }

    public AuthenticationTokenDto expand(User user) {
        String username = user.getUsername();
        String token = provider.createToken(username);
        return new AuthenticationTokenDto(user.getId(), username, token);
    }

}
