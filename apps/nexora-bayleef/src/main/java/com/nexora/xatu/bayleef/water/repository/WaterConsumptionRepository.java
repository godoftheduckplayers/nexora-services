package com.nexora.xatu.bayleef.water.repository;

import com.nexora.xatu.bayleef.water.model.WaterConsumption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WaterConsumptionRepository extends MongoRepository<WaterConsumption, String> {

  Page<WaterConsumption> findByUserId(String userId, Pageable pageable);

  Page<WaterConsumption> findByUserIdAndConsumedOn(
      String userId, LocalDate consumedOn, Pageable pageable);

  List<WaterConsumption> findByUserIdAndConsumedOnOrderByConsumedAtDesc(
      String userId, LocalDate consumedOn);

  Optional<WaterConsumption> findByIdAndUserId(String id, String userId);
}
