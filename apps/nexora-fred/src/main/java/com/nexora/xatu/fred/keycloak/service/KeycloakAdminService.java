package com.nexora.xatu.fred.keycloak.service;

import com.nexora.xatu.fred.keycloak.client.KeycloakAdminClient;
import com.nexora.xatu.fred.keycloak.client.KeycloakTokenClient;
import com.nexora.xatu.fred.keycloak.dto.response.KeycloakTokenResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class KeycloakAdminService {

  private final KeycloakTokenClient keycloakTokenClient;
  private final KeycloakAdminClient keycloakAdminClient;

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.client-id}")
  private String clientId;

  @Value("${keycloak.client-secret}")
  private String clientSecret;

  public KeycloakAdminService(
      KeycloakTokenClient keycloakTokenClient, KeycloakAdminClient keycloakAdminClient) {
    this.keycloakTokenClient = keycloakTokenClient;
    this.keycloakAdminClient = keycloakAdminClient;
  }

  public String getAdminToken() {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

    form.add("grant_type", "client_credentials");
    form.add("client_id", clientId);
    form.add("client_secret", clientSecret);

    KeycloakTokenResponse response = keycloakTokenClient.getToken(realm, form);

    return response.accessToken();
  }

  public void executeActionsEmail(String userId, List<String> actionList) {
    String adminToken = getAdminToken();

    keycloakAdminClient.executeActionsEmail("Bearer " + adminToken, realm, userId, actionList);
  }
}
