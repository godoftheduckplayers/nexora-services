package com.nexora.xatu.daffy.transaction.service;

import com.nexora.xatu.daffy.fixedexpense.model.FixedExpense;
import com.nexora.xatu.daffy.shared.dto.PageResponse;
import com.nexora.xatu.daffy.shared.service.JwtUserService;
import com.nexora.xatu.daffy.transaction.dto.request.CreateTransactionRequest;
import com.nexora.xatu.daffy.transaction.dto.request.UpdateTransactionRequest;
import com.nexora.xatu.daffy.transaction.dto.response.TransactionResponse;
import com.nexora.xatu.daffy.transaction.model.Transaction;
import com.nexora.xatu.daffy.transaction.repository.TransactionRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final JwtUserService jwtUserService;

  public TransactionService(
      TransactionRepository transactionRepository, JwtUserService jwtUserService) {
    this.transactionRepository = transactionRepository;
    this.jwtUserService = jwtUserService;
  }

  public TransactionResponse createFromFixedExpense(
      Jwt jwt, FixedExpense expense, YearMonth targetMonth) {
    String userId = jwtUserService.requireUserId(jwt);

    if (!expense.isActive()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Fixed expense is not active.");
    }

    if (!userId.equals(expense.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fixed expense not found.");
    }

    LocalDate occurredOn = resolveFixedExpenseDate(expense.getDayOfMonth(), targetMonth);

    if (transactionRepository.existsByUserIdAndFixedExpenseIdAndOccurredOnBetween(
        userId,
        expense.getId(),
        targetMonth.atDay(1),
        targetMonth.atEndOfMonth())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Fixed expense already paid for this month.");
    }

    Transaction transaction = Transaction.fromFixedExpense(userId, expense, occurredOn);

    return transactionRepository.save(transaction).toDto();
  }

  public boolean isFixedExpensePaidForMonth(
      String userId, String fixedExpenseId, YearMonth targetMonth) {
    return transactionRepository.existsByUserIdAndFixedExpenseIdAndOccurredOnBetween(
        userId,
        fixedExpenseId,
        targetMonth.atDay(1),
        targetMonth.atEndOfMonth());
  }

  public TransactionResponse create(Jwt jwt, CreateTransactionRequest request) {
    String userId = jwtUserService.requireUserId(jwt);
    Transaction transaction = Transaction.from(userId, request);

    return transactionRepository.save(transaction).toDto();
  }

  public PageResponse<TransactionResponse> findAll(
      Jwt jwt, Integer year, Integer month, Pageable pageable) {
    String userId = jwtUserService.requireUserId(jwt);
    LocalDate[] range = resolveMonthRange(year, month);
    Page<Transaction> page =
        transactionRepository.findByUserIdAndOccurredOnBetween(
            userId, range[0], range[1], pageable);

    return new PageResponse<>(
        page.getContent().stream().map(Transaction::toDto).toList(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  public TransactionResponse findById(Jwt jwt, String id) {
    return findOwned(jwt, id).toDto();
  }

  public TransactionResponse update(Jwt jwt, String id, UpdateTransactionRequest request) {
    Transaction transaction = findOwned(jwt, id);
    transaction.update(request);

    return transactionRepository.save(transaction).toDto();
  }

  public void delete(Jwt jwt, String id) {
    Transaction transaction = findOwned(jwt, id);
    transactionRepository.delete(transaction);
  }

  private Transaction findOwned(Jwt jwt, String id) {
    String userId = jwtUserService.requireUserId(jwt);
    Transaction transaction =
        transactionRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found."));

    if (!userId.equals(transaction.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found.");
    }

    return transaction;
  }

  private LocalDate[] resolveMonthRange(Integer year, Integer month) {
    YearMonth target =
        year == null || month == null
            ? YearMonth.now()
            : YearMonth.of(year, month);

    return new LocalDate[] {target.atDay(1), target.atEndOfMonth()};
  }

  private LocalDate resolveFixedExpenseDate(int dayOfMonth, YearMonth targetMonth) {
    int safeDay = Math.min(Math.max(dayOfMonth, 1), targetMonth.lengthOfMonth());
    return targetMonth.atDay(safeDay);
  }
}
