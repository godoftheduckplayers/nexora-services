package com.nexora.xatu.chansey.weight.repository;

import com.nexora.xatu.chansey.weight.model.WeightEntry;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WeightEntryRepository extends MongoRepository<WeightEntry, String> {

  Optional<WeightEntry> findByUserIdAndRecordedOn(String userId, LocalDate recordedOn);

  Optional<WeightEntry> findTopByUserIdOrderByRecordedOnDescRecordedAtDesc(String userId);

  List<WeightEntry> findByUserIdAndRecordedOnBetweenOrderByRecordedOnAsc(
      String userId, LocalDate from, LocalDate to);

  Optional<WeightEntry> findByIdAndUserId(String id, String userId);
}
