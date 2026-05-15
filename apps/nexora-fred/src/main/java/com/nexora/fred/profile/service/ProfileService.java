package com.nexora.fred.profile.service;

import com.nexora.fred.keycloak.client.KeycloakAdminClient;
import com.nexora.fred.keycloak.dto.request.KeycloakUpdateUserRequest;
import com.nexora.fred.keycloak.service.KeycloakAdminService;
import com.nexora.fred.profile.dto.request.UpdateProfileRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

  private final KeycloakAdminClient keycloakAdminClient;
  private final KeycloakAdminService tokenService;

  @Value("${keycloak.realm}")
  private String realm;

  public ProfileService(
      KeycloakAdminClient keycloakAdminClient, KeycloakAdminService tokenService) {
    this.keycloakAdminClient = keycloakAdminClient;
    this.tokenService = tokenService;
  }

  public void updateProfile(String userId, UpdateProfileRequest request) {
    String adminToken = tokenService.getAdminToken();

    KeycloakUpdateUserRequest keycloakRequest =
        new KeycloakUpdateUserRequest(request.firstName(), request.lastName());

    keycloakAdminClient.updateUser("Bearer " + adminToken, realm, userId, keycloakRequest);
  }
}
