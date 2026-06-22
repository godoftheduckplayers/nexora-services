package com.nexora.xatu.daffy.fixedexpense.repository;

import com.nexora.xatu.daffy.fixedexpense.model.FixedExpense;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FixedExpenseRepository extends MongoRepository<FixedExpense, String> {

  List<FixedExpense> findByUserIdOrderByNameAsc(String userId);

  List<FixedExpense> findByUserIdAndActiveTrue(String userId);
}
