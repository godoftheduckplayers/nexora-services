package com.nexora.xatu.daffy.transaction.service;

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
}
