package com.nexora.xatu.velma.app.model;

import com.nexora.xatu.velma.app.dto.request.CreateAppRequest;
import com.nexora.xatu.velma.app.dto.request.UpdateAppRequest;
import com.nexora.xatu.velma.app.dto.response.AppResponse;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "apps")
public class App {

  @Id private String id;

  @Indexed(unique = true)
  private String key;

  private String name;
  private String description;
  private String icon;
  private String route;

  private String remoteEntry;
  private String exposedModule;

  private Boolean enabled;

  private Instant createdAt;
  private Instant updatedAt;

  public static App from(CreateAppRequest request) {
    Instant now = Instant.now();

    App app = new App();

    app.setKey(request.key());
    app.setName(request.name());
    app.setDescription(request.description());
    app.setIcon(request.icon());
    app.setRoute(request.route());
    app.setRemoteEntry(request.remoteEntry());
    app.setExposedModule(request.exposedModule());
    app.setEnabled(request.enabled());
    app.setCreatedAt(now);
    app.setUpdatedAt(now);

    return app;
  }

  public void update(UpdateAppRequest request) {
    this.name = request.name();
    this.description = request.description();
    this.icon = request.icon();
    this.route = request.route();
    this.remoteEntry = request.remoteEntry();
    this.exposedModule = request.exposedModule();
    this.enabled = request.enabled();
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

  public AppResponse toDto() {
    return new AppResponse(
        this.id,
        this.key,
        this.name,
        this.description,
        this.icon,
        this.route,
        this.remoteEntry,
        this.exposedModule,
        this.enabled,
        this.createdAt,
        this.updatedAt);
  }
}
