package com.nexora.xatu.chansey.weight.service;

import com.nexora.xatu.chansey.shared.enums.ProgressGranularity;
import com.nexora.xatu.chansey.shared.service.JwtUserService;
import com.nexora.xatu.chansey.weight.dto.request.UpsertWeightEntryRequest;
import com.nexora.xatu.chansey.weight.dto.response.WeightEntryResponse;
import com.nexora.xatu.chansey.weight.dto.response.WeightProgressPoint;
import com.nexora.xatu.chansey.weight.dto.response.WeightProgressResponse;
import com.nexora.xatu.chansey.weight.model.WeightEntry;
import com.nexora.xatu.chansey.weight.repository.WeightEntryRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class WeightEntryService {

  private final WeightEntryRepository weightEntryRepository;
  private final JwtUserService jwtUserService;

  public WeightEntryService(
      WeightEntryRepository weightEntryRepository, JwtUserService jwtUserService) {
    this.weightEntryRepository = weightEntryRepository;
    this.jwtUserService = jwtUserService;
  }

  public WeightEntryResponse upsert(Jwt jwt, UpsertWeightEntryRequest request) {
    String userId = jwtUserService.requireUserId(jwt);
    LocalDate recordedOn =
        request.recordedOn() == null ? LocalDate.now(ZoneOffset.UTC) : request.recordedOn();

    WeightEntry entry =
        weightEntryRepository
            .findByUserIdAndRecordedOnDate(userId, recordedOn.toString())
            .map(
                existing -> {
                  existing.apply(request);
                  return existing;
                })
            .orElseGet(() -> WeightEntry.from(userId, request));

    return weightEntryRepository.save(entry).toDto();
  }

  public List<WeightEntryResponse> findRecent(Jwt jwt, LocalDate from, LocalDate to) {
    String userId = jwtUserService.requireUserId(jwt);
    LocalDate end = to == null ? LocalDate.now(ZoneOffset.UTC) : to;
    LocalDate start = from == null ? end.minusDays(30) : from;

    return weightEntryRepository
        .findByUserIdAndRecordedOnInRange(userId, start.toString(), end.toString())
        .stream()
        .map(WeightEntry::toDto)
        .toList();
  }

  public WeightProgressResponse findProgress(
      Jwt jwt, LocalDate from, LocalDate to, ProgressGranularity granularity) {
    String userId = jwtUserService.requireUserId(jwt);
    LocalDate end = to == null ? LocalDate.now(ZoneOffset.UTC) : to;
    LocalDate start = from == null ? end.minusDays(30) : from;
    ProgressGranularity targetGranularity =
        granularity == null ? ProgressGranularity.DAY : granularity;

    List<WeightEntry> entries =
        weightEntryRepository.findByUserIdAndRecordedOnInRange(
            userId, start.toString(), end.toString());

    List<WeightProgressPoint> points =
        switch (targetGranularity) {
          case DAY -> entries.stream()
              .map(entry -> new WeightProgressPoint(
                  entry.getRecordedOn().toString(),
                  entry.getRecordedOn(),
                  entry.getWeightKg()))
              .toList();
          case WEEK -> aggregateByWeek(entries);
          case MONTH -> aggregateByMonth(entries);
        };

    return new WeightProgressResponse(targetGranularity, start, end, points);
  }

  public void delete(Jwt jwt, String id) {
    String userId = jwtUserService.requireUserId(jwt);

    WeightEntry entry =
        weightEntryRepository
            .findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Weight entry not found: " + id));

    weightEntryRepository.delete(entry);
  }

  public Optional<BigDecimal> findLatestWeightKg(Jwt jwt) {
    String userId = jwtUserService.requireUserId(jwt);

    return weightEntryRepository
        .findTopByUserIdOrderByRecordedOnDescRecordedAtDesc(userId)
        .map(WeightEntry::getWeightKg);
  }

  private List<WeightProgressPoint> aggregateByWeek(List<WeightEntry> entries) {
    Map<String, List<BigDecimal>> grouped = new LinkedHashMap<>();
    Map<String, LocalDate> starts = new LinkedHashMap<>();
    WeekFields weekFields = WeekFields.of(Locale.getDefault());

    for (WeightEntry entry : entries) {
      int week = entry.getRecordedOn().get(weekFields.weekOfWeekBasedYear());
      int year = entry.getRecordedOn().get(weekFields.weekBasedYear());
      String key = year + "-W" + String.format("%02d", week);

      grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(entry.getWeightKg());
      starts.putIfAbsent(key, entry.getRecordedOn());
    }

    List<WeightProgressPoint> points = new ArrayList<>();

    grouped.forEach(
        (label, values) ->
            points.add(
                new WeightProgressPoint(label, starts.get(label), average(values))));

    return points;
  }

  private List<WeightProgressPoint> aggregateByMonth(List<WeightEntry> entries) {
    Map<String, List<BigDecimal>> grouped = new LinkedHashMap<>();
    Map<String, LocalDate> starts = new LinkedHashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

    for (WeightEntry entry : entries) {
      String key = entry.getRecordedOn().format(formatter);

      grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(entry.getWeightKg());
      starts.putIfAbsent(key, entry.getRecordedOn().withDayOfMonth(1));
    }

    List<WeightProgressPoint> points = new ArrayList<>();

    grouped.forEach(
        (label, values) ->
            points.add(
                new WeightProgressPoint(label, starts.get(label), average(values))));

    return points;
  }

  private BigDecimal average(List<BigDecimal> values) {
    BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    return sum.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
  }
}
