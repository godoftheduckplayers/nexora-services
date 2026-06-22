package com.nexora.xatu.porky.budget.repository;

import com.nexora.xatu.porky.budget.model.BudgetProfile;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BudgetProfileRepository extends MongoRepository<BudgetProfile, String> {

  Optional<BudgetProfile> findByUserId(String userId);
}
