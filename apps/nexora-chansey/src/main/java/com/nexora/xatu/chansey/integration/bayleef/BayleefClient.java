package com.nexora.xatu.chansey.integration.bayleef;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BayleefClient {

  private final RestClient restClient;

  public BayleefClient(@Value("${nexora.bayleef.base-url}") String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  public BayleefDailyConsumptionResponse fetchDailyConsumption(
      String authorizationHeader, LocalDate date) {
    return restClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/consumptions/daily")
                    .queryParam("date", date)
                    .build())
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
        .retrieve()
        .body(BayleefDailyConsumptionResponse.class);
  }
}
