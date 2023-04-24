package com.e2ee.api.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

@Slf4j
@Service
public class AvatarService {

    @SneakyThrows
    public byte[] getChatAvatar(Long chatId) {
        return readImage(chatId, "images/chat_" + chatId + ".jpg");
    }

    @SneakyThrows
    public byte[] getUserAvatar(Long userId) {
        return readImage(userId, "images/user_" + userId + ".jpg");
    }

    private byte[] readImage(Long defaultId, String path) throws IOException {
        try {
            Path imagePath = Path.of(path);
            if (Files.exists(imagePath)) {
                BufferedImage image = ImageIO.read(imagePath.toFile());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                return baos.toByteArray();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return generateDefaultAvatar(defaultId, 150, 150);
    }

    public byte[] generateDefaultAvatar(Long userId, int width, int height) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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
