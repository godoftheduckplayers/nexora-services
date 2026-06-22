package com.nexora.xatu.daffy.shared.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class JwtUserService {

  public String requireUserId(Jwt jwt) {
    if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
      throw new IllegalArgumentException("Authenticated user is required.");
    }

    return jwt.getSubject();
  }
}
