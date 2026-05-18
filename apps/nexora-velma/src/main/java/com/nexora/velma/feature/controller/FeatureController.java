package com.nexora.velma.feature.controller;

import com.nexora.velma.feature.dto.request.CreateFeatureRequest;
import com.nexora.velma.feature.dto.request.UpdateFeatureRequest;
import com.nexora.velma.feature.dto.response.FeatureResponse;
import com.nexora.velma.feature.service.FeatureService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apps/{appKey}/features")
public class FeatureController {

  private final FeatureService featureService;

  public FeatureController(FeatureService featureService) {
    this.featureService = featureService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FeatureResponse create(
      @PathVariable String appKey, @Valid @RequestBody CreateFeatureRequest request) {
    return featureService.create(appKey, request);
  }

  @GetMapping
  public List<FeatureResponse> findAllByApp(@PathVariable String appKey) {
    return featureService.findAllByApp(appKey);
  }

  @GetMapping("/{key}")
  public FeatureResponse findByKey(@PathVariable String appKey, @PathVariable String key) {
    return featureService.findByAppAndKey(appKey, key);
  }

  @PutMapping("/{key}")
  public FeatureResponse update(
      @PathVariable String appKey,
      @PathVariable String key,
      @Valid @RequestBody UpdateFeatureRequest request) {
    return featureService.update(appKey, key, request);
  }

  @PatchMapping("/{key}/enable")
  public FeatureResponse enable(@PathVariable String appKey, @PathVariable String key) {
    return featureService.enable(appKey, key);
  }

  @PatchMapping("/{key}/disable")
  public FeatureResponse disable(@PathVariable String appKey, @PathVariable String key) {
    return featureService.disable(appKey, key);
  }

  @DeleteMapping("/{key}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String appKey, @PathVariable String key) {
    featureService.delete(appKey, key);
  }
}
