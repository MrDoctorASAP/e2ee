package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.ApiErrorDto;
import com.e2ee.api.controller.dto.MessageDto;
import com.e2ee.api.repository.entities.Message;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.ChatService;
import com.e2ee.api.service.MessageService;
import com.e2ee.api.service.UserAuthenticationService;
import com.e2ee.api.service.exceptons.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/message")
public class MessageController {

    private final MessageService messageService;
    private final UserAuthenticationService authService;

    @PostMapping("/send")
    public Message sendMessage(@RequestBody @Valid MessageDto message) {
        User user = authService.getAuthenticatedUser();
        return messageService.sendMessage(user, message);
    }

    @GetMapping("/messages")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public List<Message> getMessages(@RequestParam Long chatId, Optional<Integer> page, Optional<Integer> count) {
        User user = authService.getAuthenticatedUser();
        return messageService.getMessages(user, chatId, page.orElse(0), count.orElse(Integer.MAX_VALUE));
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
