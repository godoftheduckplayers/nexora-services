package com.nexora.xatu.daffy.transaction.controller;

import com.nexora.xatu.daffy.shared.dto.PageResponse;
import com.nexora.xatu.daffy.transaction.dto.request.CreateTransactionRequest;
import com.nexora.xatu.daffy.transaction.dto.request.UpdateTransactionRequest;
import com.nexora.xatu.daffy.transaction.dto.response.TransactionResponse;
import com.nexora.xatu.daffy.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransactionResponse create(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateTransactionRequest request) {
    return transactionService.create(jwt, request);
  }

  @GetMapping
  public PageResponse<TransactionResponse> findAll(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @PageableDefault(size = 20, sort = "occurredOn", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return transactionService.findAll(jwt, year, month, pageable);
  }

  @GetMapping("/{id}")
  public TransactionResponse findById(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    return transactionService.findById(jwt, id);
  }

  @PutMapping("/{id}")
  public TransactionResponse update(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String id,
      @Valid @RequestBody UpdateTransactionRequest request) {
    return transactionService.update(jwt, id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
    transactionService.delete(jwt, id);
  }
}
