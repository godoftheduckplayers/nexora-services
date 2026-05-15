package com.nexora.fred.security.service;

import com.nexora.fred.keycloak.service.KeycloakAdminService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProfileSecurityService {

  private final KeycloakAdminService keycloakAdminService;

  public ProfileSecurityService(KeycloakAdminService keycloakAdminService) {
    this.keycloakAdminService = keycloakAdminService;
  }

  public void sendResetPasswordEmail(String userId) {
    keycloakAdminService.executeActionsEmail(userId, List.of("UPDATE_PASSWORD"));
  }
}
