package com.nexora.xatu.bayleef.consumption.service;

import com.nexora.xatu.bayleef.consumption.dto.request.CreateConsumptionRequest;
import com.nexora.xatu.bayleef.consumption.dto.request.UpdateConsumptionRequest;
import com.nexora.xatu.bayleef.consumption.dto.response.ConsumptionResponse;
import com.nexora.xatu.bayleef.consumption.dto.response.DailyConsumptionResponse;
import com.nexora.xatu.bayleef.consumption.model.FoodConsumption;
import com.nexora.xatu.bayleef.consumption.repository.FoodConsumptionRepository;
import com.nexora.xatu.bayleef.food.model.Food;
import com.nexora.xatu.bayleef.food.service.FoodService;
import com.nexora.xatu.bayleef.shared.dto.PageResponse;
import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import com.nexora.xatu.bayleef.shared.service.JwtUserService;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ConsumptionService {

  private final FoodConsumptionRepository consumptionRepository;
  private final FoodService foodService;
  private final JwtUserService jwtUserService;

  public ConsumptionService(
      FoodConsumptionRepository consumptionRepository,
      FoodService foodService,
      JwtUserService jwtUserService) {
    this.consumptionRepository = consumptionRepository;
    this.foodService = foodService;
    this.jwtUserService = jwtUserService;
  }

  public ConsumptionResponse create(Jwt jwt, CreateConsumptionRequest request) {
    String userId = jwtUserService.requireUserId(jwt);
    Food food = foodService.findEntity(jwt, request.foodId());

    FoodConsumption consumption =
        consumptionRepository.save(FoodConsumption.from(userId, food, request));

    return consumption.toDto(food);
  }

  public PageResponse<ConsumptionResponse> findAll(
      Jwt jwt, LocalDate date, Pageable pageable) {
    String userId = jwtUserService.requireUserId(jwt);

    Page<FoodConsumption> page =
        date == null
            ? consumptionRepository.findByUserId(userId, pageable)
            : consumptionRepository.findByUserIdAndConsumedOn(userId, date, pageable);

    return JwtUserService.toPageResponse(page.map(consumption -> toResponse(jwt, consumption)));
  }

  public DailyConsumptionResponse findDaily(Jwt jwt, LocalDate date) {
    String userId = jwtUserService.requireUserId(jwt);
    LocalDate targetDate = date == null ? LocalDate.now(ZoneOffset.UTC) : date;

    List<ConsumptionResponse> items =
        consumptionRepository
            .findByUserIdAndConsumedOnOrderByConsumedAtDesc(userId, targetDate)
            .stream()
            .map(consumption -> toResponse(jwt, consumption))
            .toList();

    NutritionValues totals =
        items.stream()
            .map(ConsumptionResponse::nutritionConsumed)
            .reduce(NutritionValues.empty(), NutritionValues::sum);

    return new DailyConsumptionResponse(targetDate, items, totals);
  }

  public ConsumptionResponse findById(Jwt jwt, String id) {
    return toResponse(jwt, findEntity(jwt, id));
  }

  public ConsumptionResponse update(Jwt jwt, String id, UpdateConsumptionRequest request) {
    FoodConsumption consumption = findEntity(jwt, id);

    Optional<Food> food = foodService.findEntityOptional(jwt, consumption.getFoodId());

    if (food.isPresent()) {
      consumption.update(request, food.get());
    } else {
      consumption.updateWithoutFood(request);
    }

    FoodConsumption saved = consumptionRepository.save(consumption);

    return food.map(saved::toDto).orElseGet(saved::toDtoMissingFood);
  }

  public void delete(Jwt jwt, String id) {
    FoodConsumption consumption = findEntity(jwt, id);

    consumptionRepository.delete(consumption);
  }

  private FoodConsumption findEntity(Jwt jwt, String id) {
    String userId = jwtUserService.requireUserId(jwt);

    return consumptionRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new IllegalArgumentException("Consumption not found: " + id));
  }

  private ConsumptionResponse toResponse(Jwt jwt, FoodConsumption consumption) {
    return foodService
        .findEntityOptional(jwt, consumption.getFoodId())
        .map(consumption::toDto)
        .orElseGet(consumption::toDtoMissingFood);
  }
}
