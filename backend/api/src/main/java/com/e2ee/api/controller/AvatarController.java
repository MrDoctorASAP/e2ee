package com.e2ee.api.controller;

import com.e2ee.api.service.AvatarService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    @SneakyThrows
    @GetMapping(value = "/user/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getUserAvatar(@PathVariable Long id) {
        return avatarService.getUserAvatar(id);
    }

    @SneakyThrows
    @GetMapping(value = "/chat/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getChatAvatar(@PathVariable Long id) {
        return avatarService.getChatAvatar(id);
    }

    @SneakyThrows
    @GetMapping(value = "/default/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public byte[] getDefaultAvatar(@PathVariable Long id,
                                   @RequestParam Optional<Integer> width,
                                   @RequestParam Optional<Integer> height) {
        return avatarService.generateDefaultAvatar(id,
                width.filter(x->x>1).filter(x->x<1000).orElse(150),
                height.filter(x->x>1).filter(x->x<1000).orElse(150));
    }

}
