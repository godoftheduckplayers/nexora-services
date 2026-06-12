package com.nexora.xatu.velma.feature.repository;

import com.nexora.xatu.velma.feature.model.Feature;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends MongoRepository<Feature, String> {

  Optional<Feature> findByAppKeyAndKey(String appKey, String key);

  List<Feature> findAllByAppKey(String appKey);

  boolean existsByAppKeyAndKey(String appKey, String key);

  void deleteByAppKeyAndKey(String appKey, String key);
}
