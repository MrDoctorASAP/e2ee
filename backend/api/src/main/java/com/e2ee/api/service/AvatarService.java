package com.e2ee.api.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@Service
public class AvatarService {

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
