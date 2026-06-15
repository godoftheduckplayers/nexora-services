package com.nexora.xatu.bayleef.consumption.repository;

import com.nexora.xatu.bayleef.consumption.model.FoodConsumption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FoodConsumptionRepository extends MongoRepository<FoodConsumption, String> {

  Page<FoodConsumption> findByUserId(String userId, Pageable pageable);

  Page<FoodConsumption> findByUserIdAndConsumedOn(
      String userId, LocalDate consumedOn, Pageable pageable);

  List<FoodConsumption> findByUserIdAndConsumedOnOrderByConsumedAtDesc(
      String userId, LocalDate consumedOn);

  Optional<FoodConsumption> findByIdAndUserId(String id, String userId);
}
