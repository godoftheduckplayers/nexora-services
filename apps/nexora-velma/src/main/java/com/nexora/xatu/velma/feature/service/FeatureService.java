package com.nexora.xatu.velma.feature.service;

import com.nexora.xatu.velma.app.repository.AppRepository;
import com.nexora.xatu.velma.feature.dto.request.CreateFeatureRequest;
import com.nexora.xatu.velma.feature.dto.request.UpdateFeatureRequest;
import com.nexora.xatu.velma.feature.dto.response.FeatureResponse;
import com.nexora.xatu.velma.feature.model.Feature;
import com.nexora.xatu.velma.feature.repository.FeatureRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FeatureService {

  private final FeatureRepository featureRepository;
  private final AppRepository appRepository;

  public FeatureService(FeatureRepository featureRepository, AppRepository appRepository) {
    this.featureRepository = featureRepository;
    this.appRepository = appRepository;
  }

  public FeatureResponse create(String appKey, CreateFeatureRequest request) {
    validateAppExists(appKey);

    if (featureRepository.existsByAppKeyAndKey(appKey, request.key())) {
      throw new IllegalArgumentException("Feature already exists: " + appKey + "/" + request.key());
    }

    Feature feature = Feature.from(appKey, request);

    return featureRepository.save(feature).toDto();
  }

  public List<FeatureResponse> findAll() {
    return featureRepository.findAll().stream().map(Feature::toDto).toList();
  }

  public List<FeatureResponse> findAllByApp(String appKey) {
    validateAppExists(appKey);

    return featureRepository.findAllByAppKey(appKey).stream().map(Feature::toDto).toList();
  }

  public FeatureResponse findByAppAndKey(String appKey, String key) {
    return findEntityByAppAndKey(appKey, key).toDto();
  }

  public FeatureResponse update(String appKey, String key, UpdateFeatureRequest request) {
    Feature feature = findEntityByAppAndKey(appKey, key);

    feature.update(request);

    return featureRepository.save(feature).toDto();
  }

  public FeatureResponse enable(String appKey, String key) {
    Feature feature = findEntityByAppAndKey(appKey, key);

    feature.enable();

    return featureRepository.save(feature).toDto();
  }

  public FeatureResponse disable(String appKey, String key) {
    Feature feature = findEntityByAppAndKey(appKey, key);

    feature.disable();

    return featureRepository.save(feature).toDto();
  }

  public void enableByApp(String appKey) {
    validateAppExists(appKey);

    List<Feature> features = featureRepository.findAllByAppKey(appKey);

    features.forEach(Feature::enable);

    featureRepository.saveAll(features);
  }

  public void disableByApp(String appKey) {
    validateAppExists(appKey);

    List<Feature> features = featureRepository.findAllByAppKey(appKey);

    features.forEach(Feature::disable);

    featureRepository.saveAll(features);
  }

  public void delete(String appKey, String key) {
    if (!featureRepository.existsByAppKeyAndKey(appKey, key)) {
      throw new IllegalArgumentException("Feature not found: " + appKey + "/" + key);
    }

    featureRepository.deleteByAppKeyAndKey(appKey, key);
  }

  private Feature findEntityByAppAndKey(String appKey, String key) {
    return featureRepository
        .findByAppKeyAndKey(appKey, key)
        .orElseThrow(
            () -> new IllegalArgumentException("Feature not found: " + appKey + "/" + key));
  }

  private void validateAppExists(String appKey) {
    if (!appRepository.existsByKey(appKey)) {
      throw new IllegalArgumentException("App not found: " + appKey);
    }
  }
}
