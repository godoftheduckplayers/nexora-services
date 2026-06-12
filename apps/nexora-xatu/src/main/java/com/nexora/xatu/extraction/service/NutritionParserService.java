package com.nexora.xatu.extraction.service;

import com.nexora.xatu.extraction.dto.NutritionOcrResponse;
import com.nexora.xatu.extraction.enums.NutritionTableFormat;
import com.nexora.xatu.extraction.service.parser.NutritionParserStrategy;
import com.nexora.xatu.extraction.service.parser.NutritionTableLayoutDetector;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NutritionParserService {

  private final NutritionTableLayoutDetector layoutDetector;
  private final List<NutritionParserStrategy> strategies;

  public NutritionOcrResponse parse(String rawText) {
    NutritionTableFormat format = layoutDetector.detect(rawText);

    return strategies.stream()
        .filter(strategy -> strategy.supports(format))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No nutrition parser found for format: " + format))
        .parse(rawText);
  }
}
