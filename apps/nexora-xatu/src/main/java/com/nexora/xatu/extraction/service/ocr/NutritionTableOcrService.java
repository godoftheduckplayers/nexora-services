package com.nexora.xatu.extraction.service.ocr;

import java.awt.image.BufferedImage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

@Service
public class NutritionTableOcrService {

  public String read(BufferedImage image) {

    try {

      Tesseract tesseract = new Tesseract();

      tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
      tesseract.setLanguage("por+eng");

      tesseract.setPageSegMode(11);
      tesseract.setOcrEngineMode(1);

      tesseract.setVariable("user_defined_dpi", "300");
      tesseract.setVariable("preserve_interword_spaces", "1");

      return tesseract.doOCR(image);

    } catch (TesseractException exception) {
      throw new RuntimeException("Failed to execute OCR", exception);
    }
  }
}
