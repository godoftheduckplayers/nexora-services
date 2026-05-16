package com.nexora.velma.feature.repository;

import com.nexora.velma.feature.model.Feature;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends MongoRepository<Feature, String> {

  Optional<Feature> findByKey(String key);

  boolean existsByKey(String key);

  void deleteByKey(String key);
}
