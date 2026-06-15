package com.nexora.xatu.bayleef.food.repository;

import com.nexora.xatu.bayleef.food.model.Food;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FoodRepository extends MongoRepository<Food, String> {

  Page<Food> findByUserId(String userId, Pageable pageable);

  Page<Food> findByUserIdAndNameContainingIgnoreCase(
      String userId, String name, Pageable pageable);

  Optional<Food> findByIdAndUserId(String id, String userId);
}
