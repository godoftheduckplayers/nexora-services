package com.nexora.xatu.fred.keycloak.client;

import com.nexora.xatu.fred.keycloak.dto.request.KeycloakUpdateUserRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "keycloak-admin-client", url = "${keycloak.url}")
public interface KeycloakAdminClient {

  @PutMapping(
      value = "/admin/realms/{realm}/users/{userId}/execute-actions-email",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  void executeActionsEmail(
      @RequestHeader("Authorization") String authorization,
      @PathVariable String realm,
      @PathVariable String userId,
      @RequestBody List<String> actions);

  @PutMapping(
      value = "/admin/realms/{realm}/users/{userId}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  void updateUser(
      @RequestHeader("Authorization") String authorization,
      @PathVariable String realm,
      @PathVariable String userId,
      @RequestBody KeycloakUpdateUserRequest request);
}
