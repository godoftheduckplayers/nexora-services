package com.nexora.velma.feature.service;

import com.nexora.velma.feature.dto.response.MeFeaturesResponse;
import com.nexora.velma.feature.model.Feature;
import com.nexora.velma.feature.repository.FeatureRepository;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class MeFeatureService {

  private final FeatureRepository featureRepository;

  public MeFeatureService(FeatureRepository featureRepository) {
    this.featureRepository = featureRepository;
  }

  public MeFeaturesResponse findMyFeatures(Jwt jwt) {
    String userId = jwt.getSubject();

    Collection<String> groups = jwt.getClaimAsStringList("groups");

    String profile = extractGroupValue(groups, "/PROFILE/");
    String plan = extractGroupValue(groups, "/PLAN/");

    Map<String, Boolean> features =
        featureRepository.findAll().stream()
            .filter(feature -> Boolean.TRUE.equals(feature.getEnabled()))
            .collect(
                Collectors.toMap(
                    Feature::getKey, feature -> Boolean.TRUE.equals(feature.getGlobalEnabled())));

    return new MeFeaturesResponse(userId, profile, plan, features);
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
