package com.nexora.xatu.daffy.portfolio.repository;

import com.nexora.xatu.daffy.portfolio.model.PortfolioPosition;
import com.nexora.xatu.daffy.shared.enums.PortfolioType;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PortfolioPositionRepository extends MongoRepository<PortfolioPosition, String> {

  List<PortfolioPosition> findByUserIdOrderByNameAsc(String userId);

  List<PortfolioPosition> findByUserIdAndType(String userId, PortfolioType type);
}
