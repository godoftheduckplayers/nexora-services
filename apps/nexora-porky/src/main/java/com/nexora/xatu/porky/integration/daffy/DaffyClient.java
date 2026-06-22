package com.nexora.xatu.porky.integration.daffy;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class DaffyClient {

  private final RestClient restClient;

  public DaffyClient(@Value("${nexora.daffy.base-url}") String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  public DaffyMonthlyLedgerSummary fetchMonthlySummary(
      String authorizationHeader, Integer year, Integer month) {
    return restClient
        .get()
        .uri(
            uriBuilder -> {
              var builder = uriBuilder.path("/api/summary/monthly");
              if (year != null) {
                builder.queryParam("year", year);
              }
              if (month != null) {
                builder.queryParam("month", month);
              }
              return builder.build();
            })
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
        .retrieve()
        .body(DaffyMonthlyLedgerSummary.class);
  }

  public DaffyPeriodLedgerSummary fetchPeriodSummary(
      String authorizationHeader, LocalDate from, LocalDate to) {
    return restClient
        .get()
        .uri(
            uriBuilder -> {
              var builder = uriBuilder.path("/api/summary/period");
              if (from != null) {
                builder.queryParam("from", from);
              }
              if (to != null) {
                builder.queryParam("to", to);
              }
              return builder.build();
            })
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
        .retrieve()
        .body(DaffyPeriodLedgerSummary.class);
  }
}
