package com.nexora.xatu.daffy.fixedexpense.service;

import com.nexora.xatu.daffy.fixedexpense.dto.request.CreateFixedExpenseRequest;
import com.nexora.xatu.daffy.fixedexpense.dto.request.UpdateFixedExpenseRequest;
import com.nexora.xatu.daffy.fixedexpense.dto.response.FixedExpenseResponse;
import com.nexora.xatu.daffy.fixedexpense.model.FixedExpense;
import com.nexora.xatu.daffy.fixedexpense.repository.FixedExpenseRepository;
import com.nexora.xatu.daffy.shared.service.JwtUserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FixedExpenseService {

  private final FixedExpenseRepository fixedExpenseRepository;
  private final JwtUserService jwtUserService;

  public FixedExpenseService(
      FixedExpenseRepository fixedExpenseRepository, JwtUserService jwtUserService) {
    this.fixedExpenseRepository = fixedExpenseRepository;
    this.jwtUserService = jwtUserService;
  }

  public FixedExpenseResponse create(Jwt jwt, CreateFixedExpenseRequest request) {
    String userId = jwtUserService.requireUserId(jwt);
    FixedExpense expense = FixedExpense.from(userId, request);

    return fixedExpenseRepository.save(expense).toDto();
  }

  public List<FixedExpenseResponse> findAll(Jwt jwt) {
    String userId = jwtUserService.requireUserId(jwt);

    return fixedExpenseRepository.findByUserIdOrderByNameAsc(userId).stream()
        .map(FixedExpense::toDto)
        .toList();
  }

  public FixedExpenseResponse update(Jwt jwt, String id, UpdateFixedExpenseRequest request) {
    FixedExpense expense = findOwned(jwt, id);
    expense.update(request);

    return fixedExpenseRepository.save(expense).toDto();
  }

  public void delete(Jwt jwt, String id) {
    FixedExpense expense = findOwned(jwt, id);
    fixedExpenseRepository.delete(expense);
  }

  public List<FixedExpense> findActiveForUser(String userId) {
    return fixedExpenseRepository.findByUserIdAndActiveTrue(userId);
  }

  private FixedExpense findOwned(Jwt jwt, String id) {
    String userId = jwtUserService.requireUserId(jwt);
    FixedExpense expense =
        fixedExpenseRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Fixed expense not found."));

    if (!userId.equals(expense.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fixed expense not found.");
    }

    return expense;
  }
}
