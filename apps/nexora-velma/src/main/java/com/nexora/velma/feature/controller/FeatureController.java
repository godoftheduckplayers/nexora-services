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
@RequestMapping("/api/features")
public class FeatureController {

  private final FeatureService featureService;

  public FeatureController(FeatureService featureService) {
    this.featureService = featureService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FeatureResponse create(@Valid @RequestBody CreateFeatureRequest request) {
    return featureService.create(request);
  }

  @GetMapping
  public List<FeatureResponse> findAll() {
    return featureService.findAll();
  }

  @GetMapping("/{key}")
  public FeatureResponse findByKey(@PathVariable String key) {
    return featureService.findByKey(key);
  }

  @PutMapping("/{key}")
  public FeatureResponse update(
      @PathVariable String key, @Valid @RequestBody UpdateFeatureRequest request) {
    return featureService.update(key, request);
  }

  @DeleteMapping("/{key}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String key) {
    featureService.delete(key);
  }
}
