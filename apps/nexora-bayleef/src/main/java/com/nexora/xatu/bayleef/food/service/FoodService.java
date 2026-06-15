package com.nexora.xatu.bayleef.food.service;

import com.nexora.xatu.bayleef.food.dto.request.CreateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.request.UpdateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.response.FoodResponse;
import com.nexora.xatu.bayleef.food.model.Food;
import com.nexora.xatu.bayleef.food.repository.FoodRepository;
import com.nexora.xatu.bayleef.shared.dto.PageResponse;
import com.nexora.xatu.bayleef.shared.service.JwtUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class FoodService {

  private final FoodRepository foodRepository;
  private final JwtUserService jwtUserService;

  public FoodService(FoodRepository foodRepository, JwtUserService jwtUserService) {
    this.foodRepository = foodRepository;
    this.jwtUserService = jwtUserService;
  }

  public FoodResponse create(Jwt jwt, CreateFoodRequest request) {
    String userId = jwtUserService.requireUserId(jwt);

    return foodRepository.save(Food.from(userId, request)).toDto();
  }

  public PageResponse<FoodResponse> findAll(Jwt jwt, String name, Pageable pageable) {
    String userId = jwtUserService.requireUserId(jwt);

    Page<Food> page =
        name == null || name.isBlank()
            ? foodRepository.findByUserId(userId, pageable)
            : foodRepository.findByUserIdAndNameContainingIgnoreCase(
                userId, name.trim(), pageable);

    return JwtUserService.toPageResponse(page.map(Food::toDto));
  }

  public FoodResponse findById(Jwt jwt, String id) {
    return findEntity(jwt, id).toDto();
  }

  public FoodResponse update(Jwt jwt, String id, UpdateFoodRequest request) {
    Food food = findEntity(jwt, id);

    food.update(request);

    return foodRepository.save(food).toDto();
  }

  public void delete(Jwt jwt, String id) {
    Food food = findEntity(jwt, id);

    foodRepository.delete(food);
  }

  public Food findEntity(Jwt jwt, String id) {
    String userId = jwtUserService.requireUserId(jwt);

    return foodRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new IllegalArgumentException("Food not found: " + id));
  }
}
