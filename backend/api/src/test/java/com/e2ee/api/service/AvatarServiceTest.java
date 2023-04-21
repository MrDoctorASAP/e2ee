package com.e2ee.api.service;

import com.e2ee.api.TestSupportService;
import com.e2ee.api.repository.entities.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
class AvatarServiceTest {

    @Autowired AvatarService avatarService;
    @Autowired TestSupportService testSupport;

    @Test
    @SneakyThrows
    void generateAvatar() {

        User user = testSupport.createUser();
        int width = 200;
        int height = 300;

        byte[] imageData = avatarService.generateDefaultAvatar(user.getId(), width, height);
        BufferedImage avatar = ImageIO.read(new ByteArrayInputStream(imageData));

        assertThat(avatar.getWidth(), is(equalTo(width)));
        assertThat(avatar.getHeight(), is(equalTo(height)));

    }

    @Test
    @SneakyThrows
    void generateAvatarIdentity() {

        User user = testSupport.createUser();

        int width = 150;
        int height = 200;

        byte[] avatar1 = avatarService.generateDefaultAvatar(user.getId(), width, height);
        byte[] avatar2 = avatarService.generateDefaultAvatar(user.getId(), width, height);

        assertArrayEquals(avatar1, avatar2);

    }

}