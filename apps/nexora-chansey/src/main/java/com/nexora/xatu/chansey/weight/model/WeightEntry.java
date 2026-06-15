package com.nexora.xatu.chansey.weight.model;

import com.nexora.xatu.chansey.weight.dto.request.UpsertWeightEntryRequest;
import com.nexora.xatu.chansey.weight.dto.response.WeightEntryResponse;
import java.math.BigDecimal;
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
@Document(collection = "weight_entries")
@CompoundIndex(name = "user_recorded_on_idx", def = "{'userId': 1, 'recordedOn': 1}", unique = true)
public class WeightEntry {

  @Id private String id;

  private String userId;
  private LocalDate recordedOn;
  private BigDecimal weightKg;
  private String note;
  private Instant recordedAt;

  public static WeightEntry from(String userId, UpsertWeightEntryRequest request) {
    WeightEntry entry = new WeightEntry();

    entry.setUserId(userId);
    entry.apply(request);

    return entry;
  }

  public void apply(UpsertWeightEntryRequest request) {
    this.recordedOn =
        request.recordedOn() == null ? LocalDate.now(ZoneOffset.UTC) : request.recordedOn();
    this.weightKg = request.weightKg();
    this.note = request.note();
    this.recordedAt = Instant.now();
  }

  public WeightEntryResponse toDto() {
    return new WeightEntryResponse(id, recordedOn, weightKg, note, recordedAt);
  }
}
