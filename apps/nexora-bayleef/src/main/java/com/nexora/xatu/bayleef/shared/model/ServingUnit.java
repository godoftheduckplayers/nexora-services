package com.nexora.xatu.bayleef.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ServingUnit {
  G("g"),
  ML("ml");

  private final String label;

  ServingUnit(String label) {
    this.label = label;
  }

  @JsonValue
  public String label() {
    return label;
  }

  @JsonCreator
  public static ServingUnit fromLabel(String value) {
    if (value != null && value.trim().equalsIgnoreCase("ml")) {
      return ML;
    }

    return G;
  }

  public static ServingUnit fromServingSizeLabel(String servingSize) {
    if (servingSize == null || servingSize.isBlank()) {
      return G;
    }

    String normalized = servingSize.trim().toLowerCase();

    if (normalized.endsWith("ml")) {
      return ML;
    }

    return G;
  }
}
