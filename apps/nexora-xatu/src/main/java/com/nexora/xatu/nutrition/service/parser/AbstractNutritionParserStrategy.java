package com.nexora.xatu.nutrition.service.parser;

import com.nexora.xatu.nutrition.model.NutritionValue;
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
              .replace("Omg", "0mg")
              .replace("O9", "0g")
              .replace("Oy", "0g")
              .replace("Img", "1mg")
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

  protected String resolveNutrientName(String line) {
    String knownNutrient = normalizeKnownNutrient(line);

    if (knownNutrient != null) {
      return knownNutrient;
    }

    if (looksLikeNutrientLine(line)) {
      return formatNutrientLabel(line);
    }

    return null;
  }

  protected boolean looksLikeNutrientLine(String line) {
    String trimmedLine = line.trim();

    if (trimmedLine.isEmpty() || isSkippableLine(trimmedLine)) {
      return false;
    }

    if (looksLikeAmount(trimmedLine) || looksLikeDailyValue(trimmedLine)) {
      return false;
    }

    return trimmedLine.matches(".*[\\p{L}].*") && trimmedLine.length() <= 80;
  }

  private String normalizeKnownNutrient(String line) {
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

    if (normalized.contains("colesterol")) {
      return "Colesterol";
    }

    if (normalized.contains("fibra alimentar")
        || normalized.contains("fibras alimentares")
        || normalized.contains("fa amena")) {
      return "Fibra Alimentar";
    }

    if (normalized.contains("sodio") || normalized.contains("sedio") || normalized.equals("eso")) {
      return "Sódio";
    }

    if (normalized.contains("calcio")) {
      return "Cálcio";
    }

    if (normalized.contains("ferro")) {
      return "Ferro";
    }

    if (normalized.contains("cafeina")) {
      return "Cafeína";
    }

    return null;
  }

  private boolean isSkippableLine(String line) {
    String normalized = removeAccents(line).toLowerCase();

    return normalized.contains("informacao nutricional")
        || normalized.contains("tabela nutricional")
        || (normalized.contains("quant") && normalized.contains("porcao"))
        || normalized.matches(".*%\\s*vd.*")
        || normalized.contains("valores diarios")
        || normalized.contains("valor diario")
        || normalized.contains("necessidades energeticas")
        || normalized.contains("referencia com base")
        || normalized.contains("fonte:")
        || normalized.startsWith("porcao de")
        || normalized.startsWith("*");
  }

  private String formatNutrientLabel(String line) {
    String cleaned = line.trim().replaceAll("\\s+", " ");

    if (cleaned.isEmpty()) {
      return cleaned;
    }

    String[] words = cleaned.split(" ");
    StringBuilder formatted = new StringBuilder();

    for (int index = 0; index < words.length; index++) {
      String word = words[index];

      if (word.isBlank()) {
        continue;
      }

      if (index > 0) {
        formatted.append(' ');
      }

      formatted
          .append(Character.toUpperCase(word.charAt(0)))
          .append(word.substring(1).toLowerCase());
    }

    return formatted.toString();
  }

  protected String normalizeAmount(String value, String nutrient) {
    NutritionValue nutritionValue = parseNutritionValue(value, nutrient);

    if (nutritionValue == null) {
      return null;
    }

    return nutritionValue.value() + nutritionValue.unit();
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
    String normalized = line.trim().replace("%", "");

    return normalized.equals("**") || normalized.matches("\\d{1,3}");
  }

  protected String normalizeDailyValue(String line) {
    String normalized = line.trim();

    if (normalized.equals("**")) {
      return "**";
    }

    if (normalized.matches("\\d{1,3}%?")) {
      return normalized.endsWith("%") ? normalized : normalized + "%";
    }

    return null;
  }

  private String normalizeGramNumber(String number) {
    if (number.contains(",")) {
      return trimLastOcrNoiseDecimal(number);
    }

    if (number.length() > 1 && number.endsWith("9")) {
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

  protected NutritionValue parseNutritionValue(String value, String nutrient) {
    String compact =
        value
            .toLowerCase()
            .replace(" ", "")
            .replace(".", ",")
            .replace("=647kj", "")
            .replaceAll("[^0-9,mgkcalkj]", "");

    String number = extractNumber(compact);

    if (number == null) {
      return null;
    }

    String unit = detectUnit(value, compact);

    if (unit == null) {
      return null;
    }

    if ("g".equals(unit)) {
      return new NutritionValue(normalizeGramNumber(number), unit);
    }

    return new NutritionValue(number, unit);
  }

  private String detectUnit(String rawLine, String compact) {
    String normalizedLine = rawLine.toLowerCase();

    if (compact.contains("kcal") || normalizedLine.contains("kcal") || normalizedLine.contains("kj")) {
      return "kcal";
    }

    if (compact.contains("mg") || normalizedLine.contains("mg")) {
      return "mg";
    }

    if (compact.contains("g") || normalizedLine.matches(".*\\d+\\s*g.*")) {
      return "g";
    }

    return null;
  }

  private String extractNumber(String text) {
    Matcher matcher = Pattern.compile("\\d+[,.]?\\d*").matcher(text);

    return matcher.find() ? matcher.group().replace(".", ",") : null;
  }

  protected String removeAccents(String value) {
    return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
  }
}
