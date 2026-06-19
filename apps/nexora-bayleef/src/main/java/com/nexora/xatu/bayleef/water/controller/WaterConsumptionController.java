package com.nexora.xatu.bayleef.water.controller;

import com.nexora.xatu.bayleef.shared.dto.PageResponse;
import com.nexora.xatu.bayleef.water.dto.request.CreateWaterConsumptionRequest;
import com.nexora.xatu.bayleef.water.dto.request.UpdateWaterConsumptionRequest;
import com.nexora.xatu.bayleef.water.dto.response.DailyWaterConsumptionResponse;
import com.nexora.xatu.bayleef.water.dto.response.WaterConsumptionResponse;
import com.nexora.xatu.bayleef.water.service.WaterConsumptionService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/water-consumptions")
public class WaterConsumptionController {

  private final WaterConsumptionService waterConsumptionService;

  public WaterConsumptionController(WaterConsumptionService waterConsumptionService) {
    this.waterConsumptionService = waterConsumptionService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public WaterConsumptionResponse create(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateWaterConsumptionRequest request) {
    return waterConsumptionService.create(jwt, request);
  }

  @GetMapping
  public PageResponse<WaterConsumptionResponse> findAll(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate date,
      @PageableDefault(size = 20) Pageable pageable) {
    return waterConsumptionService.findAll(jwt, date, pageable);
  }

  @GetMapping("/daily")
  public DailyWaterConsumptionResponse findDaily(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate date) {
    return waterConsumptionService.findDaily(jwt, date);
  }

  @GetMapping("/{id}")
  public WaterConsumptionResponse findById(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    return waterConsumptionService.findById(jwt, id);
  }

  @PutMapping("/{id}")
  public WaterConsumptionResponse update(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String id,
      @Valid @RequestBody UpdateWaterConsumptionRequest request) {
    return waterConsumptionService.update(jwt, id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    waterConsumptionService.delete(jwt, id);
  }
}
