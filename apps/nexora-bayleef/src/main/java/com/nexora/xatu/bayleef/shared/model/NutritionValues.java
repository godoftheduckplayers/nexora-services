package com.nexora.xatu.bayleef.shared.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NutritionValues {

  private BigDecimal kcal;
  private BigDecimal proteins;
  private BigDecimal carbs;
  private BigDecimal saturatedFat;
  private BigDecimal transFat;
  private BigDecimal fat;
  private BigDecimal fiber;

  public static NutritionValues empty() {
    return new NutritionValues();
  }

  public NutritionValues copy() {
    NutritionValues copy = new NutritionValues();

    copy.setKcal(kcal);
    copy.setProteins(proteins);
    copy.setCarbs(carbs);
    copy.setSaturatedFat(saturatedFat);
    copy.setTransFat(transFat);
    copy.setFat(fat);
    copy.setFiber(fiber);

    return copy;
  }

  public NutritionValues scale(BigDecimal grams) {
    if (grams == null) {
      return copy();
    }

    BigDecimal factor = grams.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);

    return multiplyByFactor(factor);
  }

  public NutritionValues scaleToReferenceQuantity(
      BigDecimal quantity, BigDecimal referenceServingAmount) {
    if (quantity == null
        || referenceServingAmount == null
        || referenceServingAmount.compareTo(BigDecimal.ZERO) <= 0) {
      return copy();
    }

    BigDecimal factor = quantity.divide(referenceServingAmount, 8, RoundingMode.HALF_UP);

    return multiplyByFactor(factor);
  }

  public NutritionValues normalizeFromServingToPer100g(BigDecimal servingGrams) {
    if (servingGrams == null
        || servingGrams.compareTo(BigDecimal.ZERO) <= 0
        || servingGrams.compareTo(BigDecimal.valueOf(100)) == 0) {
      return copy();
    }

    BigDecimal factor =
        BigDecimal.valueOf(100).divide(servingGrams, 8, RoundingMode.HALF_UP);

    return multiplyByFactor(factor);
  }

  private NutritionValues multiplyByFactor(BigDecimal factor) {
    NutritionValues scaled = new NutritionValues();

    scaled.setKcal(scaleValue(kcal, factor));
    scaled.setProteins(scaleValue(proteins, factor));
    scaled.setCarbs(scaleValue(carbs, factor));
    scaled.setSaturatedFat(scaleValue(saturatedFat, factor));
    scaled.setTransFat(scaleValue(transFat, factor));
    scaled.setFat(scaleValue(fat, factor));
    scaled.setFiber(scaleValue(fiber, factor));

    return scaled;
  }

  public static NutritionValues sum(NutritionValues left, NutritionValues right) {
    NutritionValues total = new NutritionValues();

    total.setKcal(add(left == null ? null : left.getKcal(), right == null ? null : right.getKcal()));
    total.setProteins(
        add(left == null ? null : left.getProteins(), right == null ? null : right.getProteins()));
    total.setCarbs(
        add(left == null ? null : left.getCarbs(), right == null ? null : right.getCarbs()));
    total.setSaturatedFat(
        add(
            left == null ? null : left.getSaturatedFat(),
            right == null ? null : right.getSaturatedFat()));
    total.setTransFat(
        add(
            left == null ? null : left.getTransFat(),
            right == null ? null : right.getTransFat()));
    total.setFat(add(left == null ? null : left.getFat(), right == null ? null : right.getFat()));
    total.setFiber(
        add(left == null ? null : left.getFiber(), right == null ? null : right.getFiber()));

    return total;
  }

  private static BigDecimal scaleValue(BigDecimal value, BigDecimal factor) {
    if (value == null) {
      return null;
    }

    return value.multiply(factor).setScale(2, RoundingMode.HALF_UP);
  }

  private static BigDecimal add(BigDecimal left, BigDecimal right) {
    if (left == null && right == null) {
      return null;
    }

    if (left == null) {
      return right;
    }

    if (right == null) {
      return left;
    }

    return left.add(right).setScale(2, RoundingMode.HALF_UP);
  }
}
