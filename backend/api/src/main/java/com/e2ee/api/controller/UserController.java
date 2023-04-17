package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.UserProfileDto;
import com.e2ee.api.service.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.Random;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public UserProfileDto getProfile(@RequestParam Long userId) {
        return null;
    }

    @PostMapping("/users")
    public UserProfileDto getUsers(@RequestBody List<Long> userIds) {
        return null;
    }

    @SneakyThrows
    @GetMapping(value = "/avatar", produces = MediaType.IMAGE_JPEG_VALUE)
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public byte[] getAvatar(@RequestParam Long userId,
                            @RequestParam Optional<Integer> width,
                            @RequestParam Optional<Integer> height) {
        BufferedImage bufferedImage = new BufferedImage(
                width.filter(x->x>1).orElse(150),
                height.filter(x->x>1).orElse(150),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        Random random = new Random(userId);
        g.setColor(new Color(
                random.nextInt(0, 255),
                random.nextInt(0, 255),
                random.nextInt(0, 255)));
        g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        return baos.toByteArray();
    }

}
