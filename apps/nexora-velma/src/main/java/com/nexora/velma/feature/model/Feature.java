package com.nexora.velma.feature.model;

import com.nexora.velma.feature.dto.request.CreateFeatureRequest;
import com.nexora.velma.feature.dto.request.UpdateFeatureRequest;
import com.nexora.velma.feature.dto.response.FeatureResponse;
import com.nexora.velma.shared.enums.FeatureCategory;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "features")
@CompoundIndex(name = "uk_feature_app_key", def = "{'appKey': 1, 'key': 1}", unique = true)
public class Feature {

  @Id private String id;

  @Indexed private String appKey;

  @Indexed private String key;

  private String name;
  private String description;
  private FeatureCategory category;
  private Boolean enabled;
  private Boolean globalEnabled;

  private Instant createdAt;
  private Instant updatedAt;

  public static Feature from(String appKey, CreateFeatureRequest request) {
    Instant now = Instant.now();

    Feature feature = new Feature();

    feature.setAppKey(appKey);
    feature.setKey(request.key());
    feature.setName(request.name());
    feature.setDescription(request.description());
    feature.setCategory(request.category());
    feature.setEnabled(request.enabled());
    feature.setGlobalEnabled(request.globalEnabled());
    feature.setCreatedAt(now);
    feature.setUpdatedAt(now);

    return feature;
  }

  public void update(UpdateFeatureRequest request) {
    this.name = request.name();
    this.description = request.description();
    this.category = request.category();
    this.enabled = request.enabled();
    this.globalEnabled = request.globalEnabled();
    this.updatedAt = Instant.now();
  }

  public void enable() {
    this.enabled = true;
    this.updatedAt = Instant.now();
  }

  public void disable() {
    this.enabled = false;
    this.updatedAt = Instant.now();
  }

  public FeatureResponse toDto() {
    return new FeatureResponse(
        this.id,
        this.key,
        this.appKey,
        this.name,
        this.description,
        this.category,
        this.enabled,
        this.globalEnabled,
        this.createdAt,
        this.updatedAt);
  }
}
