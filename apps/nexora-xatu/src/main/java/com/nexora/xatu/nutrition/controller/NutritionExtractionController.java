package com.nexora.xatu.nutrition.controller;

import com.nexora.xatu.nutrition.dto.NutritionOcrResponse;
import com.nexora.xatu.nutrition.service.NutritionOcrService;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/nutrition")
@RequiredArgsConstructor
public class NutritionExtractionController {

  private final NutritionOcrService ocrService;

  @PostMapping("/extract")
  public ResponseEntity<NutritionOcrResponse> extract(@RequestParam("file") MultipartFile file)
      throws IOException {
    File tempFile = File.createTempFile("nutrition-label-", ".png");
    file.transferTo(tempFile);

    NutritionOcrResponse text = ocrService.extractNutrition(tempFile);

    return ResponseEntity.ok(text);
  }
}
