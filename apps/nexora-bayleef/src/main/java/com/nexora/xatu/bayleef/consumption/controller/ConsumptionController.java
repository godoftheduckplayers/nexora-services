package com.nexora.xatu.bayleef.consumption.controller;

import com.nexora.xatu.bayleef.consumption.dto.request.CreateConsumptionRequest;
import com.nexora.xatu.bayleef.consumption.dto.request.UpdateConsumptionRequest;
import com.nexora.xatu.bayleef.consumption.dto.response.ConsumptionResponse;
import com.nexora.xatu.bayleef.consumption.dto.response.DailyConsumptionResponse;
import com.nexora.xatu.bayleef.consumption.service.ConsumptionService;
import com.nexora.xatu.bayleef.shared.dto.PageResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api/consumptions")
public class ConsumptionController {

  private final ConsumptionService consumptionService;

  public ConsumptionController(ConsumptionService consumptionService) {
    this.consumptionService = consumptionService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ConsumptionResponse create(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateConsumptionRequest request) {
    return consumptionService.create(jwt, request);
  }

  @GetMapping
  public PageResponse<ConsumptionResponse> findAll(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate date,
      @PageableDefault(size = 20, sort = "foodName", direction = Sort.Direction.ASC)
      Pageable pageable) {
    return consumptionService.findAll(jwt, date, pageable);
  }

  @GetMapping("/daily")
  public DailyConsumptionResponse findDaily(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate date) {
    return consumptionService.findDaily(jwt, date);
  }

  @GetMapping("/{id}")
  public ConsumptionResponse findById(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    return consumptionService.findById(jwt, id);
  }

  @PutMapping("/{id}")
  public ConsumptionResponse update(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String id,
      @Valid @RequestBody UpdateConsumptionRequest request) {
    return consumptionService.update(jwt, id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    consumptionService.delete(jwt, id);
  }
}
