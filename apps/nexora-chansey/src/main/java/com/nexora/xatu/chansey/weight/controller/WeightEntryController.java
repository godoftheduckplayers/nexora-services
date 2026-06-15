package com.nexora.xatu.chansey.weight.controller;

import com.nexora.xatu.chansey.shared.enums.ProgressGranularity;
import com.nexora.xatu.chansey.weight.dto.request.UpsertWeightEntryRequest;
import com.nexora.xatu.chansey.weight.dto.response.WeightEntryResponse;
import com.nexora.xatu.chansey.weight.dto.response.WeightProgressResponse;
import com.nexora.xatu.chansey.weight.service.WeightEntryService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weight-entries")
public class WeightEntryController {

  private final WeightEntryService weightEntryService;

  public WeightEntryController(WeightEntryService weightEntryService) {
    this.weightEntryService = weightEntryService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public WeightEntryResponse upsert(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpsertWeightEntryRequest request) {
    return weightEntryService.upsert(jwt, request);
  }

  @GetMapping
  public List<WeightEntryResponse> findRecent(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    return weightEntryService.findRecent(jwt, from, to);
  }

  @GetMapping("/progress")
  public WeightProgressResponse findProgress(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false) ProgressGranularity granularity) {
    return weightEntryService.findProgress(jwt, from, to, granularity);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    weightEntryService.delete(jwt, id);
  }
}
