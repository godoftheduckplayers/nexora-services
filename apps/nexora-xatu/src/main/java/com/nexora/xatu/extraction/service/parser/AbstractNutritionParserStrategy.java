package com.nexora.xatu.extraction.service.parser;

import com.nexora.xatu.extraction.model.NutritionValue;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractNutritionParserStrategy implements NutritionParserStrategy {

  protected List<String> normalizeLines(String rawText) {
    List<String> lines = new ArrayList<>();

    for (String line : rawText.split("\\R")) {
      String normalizedLine =
          line.trim()
              .replace("Og", "0g")
              .replace("O9", "0g")
              .replace("Oy", "0g")
              .replace("D,", "0,")
              .replace("%j", "kj")
              .replace("9)", "g)")
              .replace("(9)", "(g)")
              .replace("(3)", "(g)");

      if (!normalizedLine.isBlank()) {
        lines.add(normalizedLine);
      }
    }

    return lines;
  }

  protected String normalizeNutrient(String line) {
    String normalized = removeAccents(line).toLowerCase();

    if (normalized.contains("valor energetico")) {
      return "Valor Energético";
    }

    if (normalized.contains("carboidratos") || normalized.contains("carbeiaratos")) {
      return "Carboidratos";
    }

    if (normalized.contains("acucares totais")
        || normalized.contains("agucares totais")
        || normalized.contains("agcucares totais")) {
      return "Açúcares totais";
    }

    if (normalized.contains("acucares adicionados")
        || normalized.contains("agucares adicionados")) {
      return "Açúcares adicionados";
    }

    if (normalized.contains("galactose") || normalized.contains("legados")) {
      return "Galactose";
    }

    if (normalized.contains("lactose") || normalized.contains("laetose")) {
      return "Lactose";
    }

    if (normalized.contains("proteinas") || normalized.contains("rotenas")) {
      return "Proteínas";
    }

    if (normalized.contains("gorduras totais")) {
      return "Gorduras totais";
    }

    if (normalized.contains("gorduras saturadas") || normalized.contains("saturamas")) {
      return "Gorduras saturadas";
    }

    if (normalized.contains("gorduras trans") || normalized.contains("gomwas trans")) {
      return "Gorduras Trans";
    }

    if (normalized.contains("fibra alimentar")
        || normalized.contains("fibras alimentares")
        || normalized.contains("fa amena")) {
      return "Fibra Alimentar";
    }

    if (normalized.contains("sodio") || normalized.equals("eso")) {
      return "Sódio";
    }

    if (normalized.contains("calcio")) {
      return "Cálcio";
    }

    if (normalized.contains("cafeina")) {
      return "Cafeína";
    }

    return null;
  }

  protected String normalizeAmount(String value, String nutrient) {
    String clean =
        value
            .toLowerCase()
            .replace(" ", "")
            .replace(".", ",")
            .replace("=647kj", "")
            .replace("kj", "")
            .replaceAll("[^0-9,mgkcalg]", "");

    if (clean.contains("kcal")) {
      return extractNumber(clean) + "kcal";
    }

    if (clean.contains("mg")) {
      return extractNumber(clean) + "mg";
    }

    if (clean.contains("g")) {
      return extractNumber(clean) + "g";
    }

    String number = extractNumber(clean);

    if (number == null) {
      return null;
    }

    if (nutrient.equals("Valor Energético")) {
      return number + "kcal";
    }

    if (isMilligramNutrient(nutrient)) {
      return number + "mg";
    }

    return normalizeGramNumber(number, nutrient) + "g";
  }

  protected String extractServingSize(String rawText) {
    String normalizedText = removeAccents(rawText).toLowerCase().replaceAll("\\s+", " ");

    Matcher matcher =
        Pattern.compile("porcao\\s*:?\\s*(?:de\\s*)?(\\d+[,.]?\\d*)\\s*(ml|g)")
            .matcher(normalizedText);

    return matcher.find() ? matcher.group(1).replace(".", ",") + matcher.group(2) : null;
  }

  protected boolean looksLikeAmount(String line) {
    String normalized = line.trim().toLowerCase();

    return normalized.matches(".*\\d+[,.]?\\d*\\s*(kcal|kj|mg|g).*")
        || normalized.matches("\\d+[,.]?\\d*");
  }

  protected boolean looksLikeDailyValue(String line) {
    String normalized = line.trim();

    return normalized.equals("**") || normalized.matches("\\d{1,3}");
  }

  protected String normalizeDailyValue(String line) {
    String normalized = line.trim();

    if (normalized.equals("**")) {
      return "**";
    }

    return normalized.matches("\\d{1,3}") ? normalized + "%" : null;
  }

  private String normalizeGramNumber(String number, String nutrient) {
    if (number.contains(",")) {
      return trimLastOcrNoiseDecimal(number);
    }

    if (isUsuallyIntegerGramNutrient(nutrient) && number.length() > 1 && number.endsWith("9")) {
      return number.substring(0, number.length() - 1);
    }

    return number;
  }

  private String trimLastOcrNoiseDecimal(String number) {
    String[] parts = number.split(",");

    if (parts.length != 2) {
      return number;
    }

    String decimal = parts[1];

    if (decimal.length() > 1 && decimal.endsWith("9")) {
      decimal = decimal.substring(0, decimal.length() - 1);
    }

    return parts[0] + "," + decimal;
  }

  private boolean isUsuallyIntegerGramNutrient(String nutrient) {
    return nutrient.equals("Carboidratos")
        || nutrient.equals("Açúcares totais")
        || nutrient.equals("Açúcares adicionados")
        || nutrient.equals("Lactose")
        || nutrient.equals("Proteínas")
        || nutrient.equals("Gorduras totais")
        || nutrient.equals("Gorduras Trans")
        || nutrient.equals("Fibra Alimentar");
  }

  private boolean isMilligramNutrient(String nutrient) {
    return nutrient.equals("Sódio") || nutrient.equals("Cálcio") || nutrient.equals("Cafeína");
  }

  private String extractNumber(String text) {
    Matcher matcher = Pattern.compile("\\d+[,.]?\\d*").matcher(text);

    return matcher.find() ? matcher.group().replace(".", ",") : null;
  }

  protected String removeAccents(String value) {
    return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
  }

  protected NutritionValue parseNutritionValue(String value, String nutrient) {

    String normalized =
        value
            .toLowerCase()
            .replace(" ", "")
            .replace(".", ",")
            .replace("=647kj", "")
            .replace("kj", "")
            .replaceAll("[^0-9,mgkcalg]", "");

    String number = extractNumber(normalized);

    if (number == null) {
      return null;
    }

    if (normalized.contains("kcal") || nutrient.equals("Valor Energético")) {

      return new NutritionValue(number, "kcal");
    }

    if (normalized.contains("mg") || isMilligramNutrient(nutrient)) {

      return new NutritionValue(number, "mg");
    }

    return new NutritionValue(normalizeGramNumber(number, nutrient), "g");
  }
}
