package com.nexora.xatu.daffy.fixedexpense.service;

import com.nexora.xatu.daffy.fixedexpense.dto.request.CreateFixedExpenseRequest;
import com.nexora.xatu.daffy.fixedexpense.dto.request.UpdateFixedExpenseRequest;
import com.nexora.xatu.daffy.fixedexpense.dto.response.FixedExpenseResponse;
import com.nexora.xatu.daffy.fixedexpense.model.FixedExpense;
import com.nexora.xatu.daffy.fixedexpense.repository.FixedExpenseRepository;
import com.nexora.xatu.daffy.shared.service.JwtUserService;
import com.nexora.xatu.daffy.transaction.dto.response.TransactionResponse;
import com.nexora.xatu.daffy.transaction.service.TransactionService;
import java.time.YearMonth;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FixedExpenseService {

  private final FixedExpenseRepository fixedExpenseRepository;
  private final JwtUserService jwtUserService;
  private final TransactionService transactionService;

  public FixedExpenseService(
      FixedExpenseRepository fixedExpenseRepository,
      JwtUserService jwtUserService,
      TransactionService transactionService) {
    this.fixedExpenseRepository = fixedExpenseRepository;
    this.jwtUserService = jwtUserService;
    this.transactionService = transactionService;
  }

  public FixedExpenseResponse create(Jwt jwt, CreateFixedExpenseRequest request) {
    String userId = jwtUserService.requireUserId(jwt);
    FixedExpense expense = FixedExpense.from(userId, request);

    return fixedExpenseRepository.save(expense).toDto();
  }

  public List<FixedExpenseResponse> findAll(Jwt jwt, Integer year, Integer month) {
    String userId = jwtUserService.requireUserId(jwt);
    YearMonth targetMonth = resolveYearMonth(year, month);

    return fixedExpenseRepository.findByUserIdOrderByNameAsc(userId).stream()
        .map(
            expense ->
                expense.toDto(
                    transactionService.isFixedExpensePaidForMonth(
                        userId, expense.getId(), targetMonth)))
        .toList();
  }

  public TransactionResponse generateTransaction(
      Jwt jwt, String id, Integer year, Integer month) {
    FixedExpense expense = findOwned(jwt, id);
    YearMonth targetMonth = resolveYearMonth(year, month);

    return transactionService.createFromFixedExpense(jwt, expense, targetMonth);
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

  private YearMonth resolveYearMonth(Integer year, Integer month) {
    return year == null || month == null ? YearMonth.now() : YearMonth.of(year, month);
  }
}
