package com.nexora.xatu.chansey.profile.repository;

import com.nexora.xatu.chansey.profile.model.HealthProfile;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HealthProfileRepository extends MongoRepository<HealthProfile, String> {

  Optional<HealthProfile> findByUserId(String userId);
}
