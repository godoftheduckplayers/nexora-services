package com.nexora.velma.feature.service;

import com.nexora.velma.app.dto.response.AppResponse;
import com.nexora.velma.app.model.App;
import com.nexora.velma.app.repository.AppRepository;
import com.nexora.velma.feature.dto.response.MeFeaturesResponse;
import com.nexora.velma.feature.model.Feature;
import com.nexora.velma.feature.repository.FeatureRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class MeFeatureService {

  private final FeatureRepository featureRepository;
  private final AppRepository appRepository;

  public MeFeatureService(FeatureRepository featureRepository, AppRepository appRepository) {
    this.featureRepository = featureRepository;
    this.appRepository = appRepository;
  }

  public MeFeaturesResponse findMyFeatures(Jwt jwt) {
    String userId = jwt.getSubject();

    Collection<String> groups = jwt.getClaimAsStringList("groups");

    String profile = extractGroupValue(groups, "/PROFILE/");

    String plan = extractGroupValue(groups, "/PLAN/");

    List<AppResponse> apps =
        appRepository.findAll().stream()
            .filter(app -> Boolean.TRUE.equals(app.getEnabled()))
            .map(App::toDto)
            .toList();

    Map<String, Boolean> features =
        featureRepository.findAll().stream()
            .filter(feature -> Boolean.TRUE.equals(feature.getEnabled()))
            .collect(
                Collectors.toMap(
                    Feature::getKey, feature -> Boolean.TRUE.equals(feature.getGlobalEnabled())));

    return new MeFeaturesResponse(userId, profile, plan, apps, features);
  }

  private String extractGroupValue(Collection<String> groups, String prefix) {
    if (groups == null) {
      return null;
    }

    return groups.stream()
        .filter(group -> group.startsWith(prefix))
        .map(group -> group.substring(prefix.length()))
        .findFirst()
        .orElse(null);
  }
}
