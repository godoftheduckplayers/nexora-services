package com.nexora.xatu.bayleef.water.service;

import com.nexora.xatu.bayleef.shared.dto.PageResponse;
import com.nexora.xatu.bayleef.shared.service.JwtUserService;
import com.nexora.xatu.bayleef.water.dto.request.CreateWaterConsumptionRequest;
import com.nexora.xatu.bayleef.water.dto.request.UpdateWaterConsumptionRequest;
import com.nexora.xatu.bayleef.water.dto.response.DailyWaterConsumptionResponse;
import com.nexora.xatu.bayleef.water.dto.response.WaterConsumptionResponse;
import com.nexora.xatu.bayleef.water.model.WaterConsumption;
import com.nexora.xatu.bayleef.water.repository.WaterConsumptionRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class WaterConsumptionService {

  private final WaterConsumptionRepository waterConsumptionRepository;
  private final JwtUserService jwtUserService;

  public WaterConsumptionService(
      WaterConsumptionRepository waterConsumptionRepository, JwtUserService jwtUserService) {
    this.waterConsumptionRepository = waterConsumptionRepository;
    this.jwtUserService = jwtUserService;
  }

  public WaterConsumptionResponse create(Jwt jwt, CreateWaterConsumptionRequest request) {
    String userId = jwtUserService.requireUserId(jwt);

    WaterConsumption consumption =
        waterConsumptionRepository.save(WaterConsumption.from(userId, request));

    return consumption.toDto();
  }

  public PageResponse<WaterConsumptionResponse> findAll(
      Jwt jwt, LocalDate date, Pageable pageable) {
    String userId = jwtUserService.requireUserId(jwt);

    Page<WaterConsumption> page =
        date == null
            ? waterConsumptionRepository.findByUserId(userId, pageable)
            : waterConsumptionRepository.findByUserIdAndConsumedOn(userId, date, pageable);

    return JwtUserService.toPageResponse(page.map(WaterConsumption::toDto));
  }

  public DailyWaterConsumptionResponse findDaily(Jwt jwt, LocalDate date) {
    String userId = jwtUserService.requireUserId(jwt);
    LocalDate targetDate = date == null ? LocalDate.now(ZoneOffset.UTC) : date;

    List<WaterConsumptionResponse> items =
        waterConsumptionRepository
            .findByUserIdAndConsumedOnOrderByConsumedAtDesc(userId, targetDate)
            .stream()
            .map(WaterConsumption::toDto)
            .toList();

    int totalMl = items.stream().mapToInt(WaterConsumptionResponse::volumeMl).sum();

    return new DailyWaterConsumptionResponse(targetDate, items, totalMl);
  }

  public WaterConsumptionResponse findById(Jwt jwt, String id) {
    return findEntity(jwt, id).toDto();
  }

  public WaterConsumptionResponse update(
      Jwt jwt, String id, UpdateWaterConsumptionRequest request) {
    WaterConsumption consumption = findEntity(jwt, id);

    consumption.update(request);

    return waterConsumptionRepository.save(consumption).toDto();
  }

  public void delete(Jwt jwt, String id) {
    WaterConsumption consumption = findEntity(jwt, id);

    waterConsumptionRepository.delete(consumption);
  }

  private WaterConsumption findEntity(Jwt jwt, String id) {
    String userId = jwtUserService.requireUserId(jwt);

    return waterConsumptionRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new IllegalArgumentException("Water consumption not found: " + id));
  }
}
