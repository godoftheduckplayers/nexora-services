package com.nexora.xatu.bayleef.water.model;

import com.nexora.xatu.bayleef.water.dto.request.CreateWaterConsumptionRequest;
import com.nexora.xatu.bayleef.water.dto.request.UpdateWaterConsumptionRequest;
import com.nexora.xatu.bayleef.water.dto.response.WaterConsumptionResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "water_consumptions")
@CompoundIndex(name = "user_consumed_on_idx", def = "{'userId': 1, 'consumedOn': -1}")
public class WaterConsumption {

  @Id private String id;

  private String userId;
  private Integer volumeMl;
  private String note;
  private LocalDate consumedOn;
  private Instant consumedAt;

  private Instant createdAt;
  private Instant updatedAt;

  public static WaterConsumption from(String userId, CreateWaterConsumptionRequest request) {
    Instant consumedAt = request.consumedAt() == null ? Instant.now() : request.consumedAt();
    Instant now = Instant.now();

    WaterConsumption consumption = new WaterConsumption();

    consumption.setUserId(userId);
    consumption.setVolumeMl(request.volumeMl());
    consumption.setNote(normalizeNote(request.note()));
    consumption.setConsumedAt(consumedAt);
    consumption.setConsumedOn(LocalDate.ofInstant(consumedAt, ZoneOffset.UTC));
    consumption.setCreatedAt(now);
    consumption.setUpdatedAt(now);

    return consumption;
  }

  public void update(UpdateWaterConsumptionRequest request) {
    Instant consumedAt = request.consumedAt() == null ? this.consumedAt : request.consumedAt();

    this.volumeMl = request.volumeMl();
    this.note = normalizeNote(request.note());
    this.consumedAt = consumedAt;
    this.consumedOn = LocalDate.ofInstant(consumedAt, ZoneOffset.UTC);
    this.updatedAt = Instant.now();
  }

  public WaterConsumptionResponse toDto() {
    return new WaterConsumptionResponse(
        this.id, this.volumeMl, this.note, this.consumedOn, this.consumedAt);
  }

  private static String normalizeNote(String note) {
    if (note == null) {
      return null;
    }

    String trimmed = note.trim();

    return trimmed.isEmpty() ? null : trimmed;
  }
}
