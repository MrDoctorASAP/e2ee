package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.ApiErrorDto;
import com.e2ee.api.controller.dto.FlatUnseenChat;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.UnseenMessageService;
import com.e2ee.api.service.UserAuthenticationService;
import com.e2ee.api.service.exceptons.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/unseen")
public class UnseenMessagesController {

    private final UnseenMessageService unseenService;
    private final UserAuthenticationService authService;

    @GetMapping("/chats")
    public List<FlatUnseenChat> getUnseenChats() {
        User user = authService.getAuthenticatedUser();
        return unseenService.getUnseenChats(user);
    }

    @GetMapping("/seen")
    public void seen(@RequestParam Long chatId) {
        User user = authService.getAuthenticatedUser();
        unseenService.seen(user, chatId);
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
