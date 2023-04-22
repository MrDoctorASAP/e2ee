package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.ApiErrorDto;
import com.e2ee.api.controller.dto.GroupChatDto;
import com.e2ee.api.controller.dto.LastMessageDto;
import com.e2ee.api.repository.entities.Chat;
import com.e2ee.api.repository.entities.Message;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.ChatService;
import com.e2ee.api.service.UserAuthenticationService;
import com.e2ee.api.service.exceptons.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserAuthenticationService authService;

    @GetMapping("/chats")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public List<Chat> getChats(@RequestParam Optional<Integer> page,
                               @RequestParam Optional<Integer> count) {
        User user = authService.getAuthenticatedUser();
        return chatService.getChats(user);
    }

    @PostMapping("/with")
    public Chat createChat(@RequestBody @Valid GroupChatDto chat) {
        User user = authService.getAuthenticatedUser();
        return chatService.createGroupChat(user, chat);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDto> handleAuthException(AuthenticationException e) {
        log.debug(e.getMessage(), e);
        return ApiErrorDto.exception(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorDto> handleServiceException(ServiceException e) {
        log.debug(e.getMessage(), e);
        return ApiErrorDto.exception(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorDto> handleValidationException(ServiceException e) {
        log.debug(e.getMessage(), e);
        return ApiErrorDto.exception(HttpStatus.BAD_REQUEST, e);
    }

}
