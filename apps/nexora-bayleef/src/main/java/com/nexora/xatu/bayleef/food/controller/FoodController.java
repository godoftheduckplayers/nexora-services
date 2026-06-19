package com.nexora.xatu.bayleef.food.controller;

import com.nexora.xatu.bayleef.food.dto.request.CreateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.request.UpdateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.response.FoodResponse;
import com.nexora.xatu.bayleef.food.service.FoodService;
import com.nexora.xatu.bayleef.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/foods")
public class FoodController {

  private final FoodService foodService;

  public FoodController(FoodService foodService) {
    this.foodService = foodService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FoodResponse create(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateFoodRequest request) {
    return foodService.create(jwt, request);
  }

  @GetMapping
  public PageResponse<FoodResponse> findAll(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) String name,
      @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
      Pageable pageable) {
    return foodService.findAll(jwt, name, pageable);
  }

  @GetMapping("/{id}")
  public FoodResponse findById(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    return foodService.findById(jwt, id);
  }

  @PutMapping("/{id}")
  public FoodResponse update(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String id,
      @Valid @RequestBody UpdateFoodRequest request) {
    return foodService.update(jwt, id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    foodService.delete(jwt, id);
  }
}
