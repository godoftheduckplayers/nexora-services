package com.nexora.xatu.extraction.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class ImagePreprocessorService {

  public BufferedImage preprocess(File file) {

    try {
      BufferedImage original = ImageIO.read(file);

      BufferedImage resized = resize(original, 3);

      return toGrayScale(resized);

    } catch (IOException exception) {
      throw new RuntimeException("Failed to preprocess image", exception);
    }
  }

  private BufferedImage resize(BufferedImage image, int scale) {

    BufferedImage resized =
        new BufferedImage(
            image.getWidth() * scale, image.getHeight() * scale, BufferedImage.TYPE_INT_RGB);

    Graphics2D graphics = resized.createGraphics();

    graphics.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

    graphics.drawImage(image, 0, 0, resized.getWidth(), resized.getHeight(), null);

    graphics.dispose();

    return resized;
  }

  private BufferedImage toGrayScale(BufferedImage image) {

    BufferedImage gray =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

    Graphics2D graphics = gray.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    graphics.dispose();

    return gray;
  }
}
