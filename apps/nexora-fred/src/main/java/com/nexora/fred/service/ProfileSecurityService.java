package com.nexora.fred.service;

import com.nexora.fred.client.KeycloakAdminClient;
import com.nexora.fred.client.KeycloakTokenClient;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class ProfileSecurityService {

  private final KeycloakTokenClient tokenClient;
  private final KeycloakAdminClient adminClient;

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.client-id}")
  private String clientId;

  @Value("${keycloak.client-secret}")
  private String clientSecret;

  public ProfileSecurityService(KeycloakTokenClient tokenClient, KeycloakAdminClient adminClient) {
    this.tokenClient = tokenClient;
    this.adminClient = adminClient;
  }

  public void sendResetPasswordEmail(String userId) {
    String token = getAdminToken();

    adminClient.executeActionsEmail("Bearer " + token, realm, userId, List.of("UPDATE_PASSWORD"));
  }

  private String getAdminToken() {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "client_credentials");
    form.add("client_id", clientId);
    form.add("client_secret", clientSecret);

    Map<String, Object> response = tokenClient.getToken(realm, form);

    return (String) response.get("access_token");
  }
}
