package com.nexora.xatu.daffy.portfolio.controller;

import com.nexora.xatu.daffy.portfolio.dto.request.CreatePortfolioPositionRequest;
import com.nexora.xatu.daffy.portfolio.dto.request.UpdatePortfolioPositionRequest;
import com.nexora.xatu.daffy.portfolio.dto.request.UpdatePortfolioValueRequest;
import com.nexora.xatu.daffy.portfolio.dto.response.PortfolioPositionResponse;
import com.nexora.xatu.daffy.portfolio.service.PortfolioService;
import com.nexora.xatu.daffy.shared.enums.PortfolioType;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

  private final PortfolioService portfolioService;

  public PortfolioController(PortfolioService portfolioService) {
    this.portfolioService = portfolioService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PortfolioPositionResponse create(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreatePortfolioPositionRequest request) {
    return portfolioService.create(jwt, request);
  }

  @GetMapping
  public List<PortfolioPositionResponse> findAll(
      @AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) PortfolioType type) {
    return portfolioService.findAll(jwt, type);
  }

  @PutMapping("/{id}")
  public PortfolioPositionResponse update(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String id,
      @Valid @RequestBody UpdatePortfolioPositionRequest request) {
    return portfolioService.update(jwt, id, request);
  }

  @PatchMapping("/{id}/value")
  public PortfolioPositionResponse updateValue(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String id,
      @Valid @RequestBody UpdatePortfolioValueRequest request) {
    return portfolioService.updateValue(jwt, id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    portfolioService.delete(jwt, id);
  }
}
