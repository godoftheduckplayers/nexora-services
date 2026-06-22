package com.nexora.xatu.daffy.transaction.repository;

import com.nexora.xatu.daffy.shared.enums.TransactionType;
import com.nexora.xatu.daffy.transaction.model.Transaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

  Page<Transaction> findByUserIdAndOccurredOnBetween(
      String userId, LocalDate from, LocalDate to, Pageable pageable);

  List<Transaction> findByUserIdAndOccurredOnBetween(
      String userId, LocalDate from, LocalDate to);

  List<Transaction> findByUserIdAndTypeAndOccurredOnBetween(
      String userId, TransactionType type, LocalDate from, LocalDate to);
}
