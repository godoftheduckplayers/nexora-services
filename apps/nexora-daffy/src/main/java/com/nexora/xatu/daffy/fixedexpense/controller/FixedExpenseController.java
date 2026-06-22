package com.nexora.xatu.daffy.fixedexpense.controller;

import com.nexora.xatu.daffy.fixedexpense.dto.request.CreateFixedExpenseRequest;
import com.nexora.xatu.daffy.fixedexpense.dto.request.UpdateFixedExpenseRequest;
import com.nexora.xatu.daffy.fixedexpense.dto.response.FixedExpenseResponse;
import com.nexora.xatu.daffy.fixedexpense.service.FixedExpenseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fixed-expenses")
public class FixedExpenseController {

  private final FixedExpenseService fixedExpenseService;

  public FixedExpenseController(FixedExpenseService fixedExpenseService) {
    this.fixedExpenseService = fixedExpenseService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FixedExpenseResponse create(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateFixedExpenseRequest request) {
    return fixedExpenseService.create(jwt, request);
  }

  @GetMapping
  public List<FixedExpenseResponse> findAll(@AuthenticationPrincipal Jwt jwt) {
    return fixedExpenseService.findAll(jwt);
  }

  @PutMapping("/{id}")
  public FixedExpenseResponse update(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String id,
      @Valid @RequestBody UpdateFixedExpenseRequest request) {
    return fixedExpenseService.update(jwt, id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    fixedExpenseService.delete(jwt, id);
  }
}
