package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.ApiErrorDto;
import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.controller.dto.UserProfileDto;
import com.e2ee.api.service.AvatarService;
import com.e2ee.api.service.UserProfileService;
import com.e2ee.api.service.exceptons.ServiceException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserProfileService userProfileService;
    private final AvatarService avatarService;

    @PostMapping("/users")
    public List<UserDto> getUsers(@RequestBody List<Long> userIds) {
        return userProfileService.getUsers(userIds);
    }

    @GetMapping("/profile")
    public UserProfileDto getProfile(@RequestParam Long userId) {
        return userProfileService.getUserProfile(userId);
    }

    @SneakyThrows
    @GetMapping(value = "/avatar", produces = MediaType.IMAGE_JPEG_VALUE)
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public byte[] getAvatar(@RequestParam Long userId,
                            @RequestParam Optional<Integer> width,
                            @RequestParam Optional<Integer> height) {
        return avatarService.generateDefaultAvatar(userId,
                 width.filter(x->x>1).filter(x->x<1000).orElse(150),
                height.filter(x->x>1).filter(x->x<1000).orElse(150));
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
