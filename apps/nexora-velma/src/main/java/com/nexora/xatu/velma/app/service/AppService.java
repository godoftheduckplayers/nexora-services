package com.nexora.xatu.velma.app.service;

import com.nexora.xatu.velma.app.dto.request.CreateAppRequest;
import com.nexora.xatu.velma.app.dto.request.UpdateAppRequest;
import com.nexora.xatu.velma.app.dto.response.AppResponse;
import com.nexora.xatu.velma.app.model.App;
import com.nexora.xatu.velma.app.repository.AppRepository;
import com.nexora.xatu.velma.feature.service.FeatureService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppService {

  private final AppRepository appRepository;
  private final FeatureService featureService;

  public AppService(AppRepository appRepository, FeatureService featureService) {
    this.appRepository = appRepository;
    this.featureService = featureService;
  }

  public AppResponse create(CreateAppRequest request) {
    if (appRepository.existsByKey(request.key())) {
      throw new IllegalArgumentException("App already exists: " + request.key());
    }

    App app = App.from(request);

    return appRepository.save(app).toDto();
  }

  public List<AppResponse> findAll() {
    return appRepository.findAll().stream().map(App::toDto).toList();
  }

  public AppResponse findByKey(String key) {
    return findEntityByKey(key).toDto();
  }

  public AppResponse update(String key, UpdateAppRequest request) {
    App app = findEntityByKey(key);

    app.update(request);

    return appRepository.save(app).toDto();
  }

  public AppResponse enable(String key) {
    App app = findEntityByKey(key);

    app.enable();

    featureService.enableByApp(key);

    return appRepository.save(app).toDto();
  }

  public AppResponse disable(String key) {
    App app = findEntityByKey(key);

    app.disable();

    featureService.disableByApp(key);

    return appRepository.save(app).toDto();
  }

  public void delete(String key) {
    if (!appRepository.existsByKey(key)) {
      throw new IllegalArgumentException("App not found: " + key);
    }

    appRepository.deleteByKey(key);
  }

  private App findEntityByKey(String key) {
    return appRepository
        .findByKey(key)
        .orElseThrow(() -> new IllegalArgumentException("App not found: " + key));
  }
}
