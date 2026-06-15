package com.nexora.xatu.bayleef.shared.service;

import org.springframework.data.domain.Page;
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

  public static <T> com.nexora.xatu.bayleef.shared.dto.PageResponse<T> toPageResponse(Page<T> page) {
    return new com.nexora.xatu.bayleef.shared.dto.PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }
}
