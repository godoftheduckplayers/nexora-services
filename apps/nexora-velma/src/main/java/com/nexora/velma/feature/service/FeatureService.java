package com.nexora.velma.feature.service;

import com.nexora.velma.feature.dto.request.CreateFeatureRequest;
import com.nexora.velma.feature.dto.request.UpdateFeatureRequest;
import com.nexora.velma.feature.dto.response.FeatureResponse;
import com.nexora.velma.feature.model.Feature;
import com.nexora.velma.feature.repository.FeatureRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FeatureService {

  private final FeatureRepository featureRepository;

  public FeatureService(FeatureRepository featureRepository) {
    this.featureRepository = featureRepository;
  }

  public FeatureResponse create(CreateFeatureRequest request) {
    if (featureRepository.existsByKey(request.key())) {
      throw new IllegalArgumentException("Feature already exists: " + request.key());
    }

    Feature feature = Feature.from(request);

    return featureRepository.save(feature).toDto();
  }

  public List<FeatureResponse> findAll() {
    return featureRepository.findAll().stream().map(Feature::toDto).toList();
  }

  public FeatureResponse findByKey(String key) {
    return featureRepository
        .findByKey(key)
        .map(Feature::toDto)
        .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + key));
  }

  public FeatureResponse update(String key, UpdateFeatureRequest request) {
    Feature feature =
        featureRepository
            .findByKey(key)
            .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + key));

    feature.update(request);

    return featureRepository.save(feature).toDto();
  }

  public void delete(String key) {
    if (!featureRepository.existsByKey(key)) {
      throw new IllegalArgumentException("Feature not found: " + key);
    }

    featureRepository.deleteByKey(key);
  }
}
