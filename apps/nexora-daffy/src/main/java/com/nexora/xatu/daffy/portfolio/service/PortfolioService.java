package com.nexora.xatu.daffy.portfolio.service;

import com.nexora.xatu.daffy.portfolio.dto.request.CreatePortfolioPositionRequest;
import com.nexora.xatu.daffy.portfolio.dto.request.UpdatePortfolioPositionRequest;
import com.nexora.xatu.daffy.portfolio.dto.request.UpdatePortfolioValueRequest;
import com.nexora.xatu.daffy.portfolio.dto.response.PortfolioPositionResponse;
import com.nexora.xatu.daffy.portfolio.model.PortfolioPosition;
import com.nexora.xatu.daffy.portfolio.repository.PortfolioPositionRepository;
import com.nexora.xatu.daffy.shared.enums.PortfolioType;
import com.nexora.xatu.daffy.shared.service.JwtUserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PortfolioService {

  private final PortfolioPositionRepository portfolioPositionRepository;
  private final JwtUserService jwtUserService;

  public PortfolioService(
      PortfolioPositionRepository portfolioPositionRepository, JwtUserService jwtUserService) {
    this.portfolioPositionRepository = portfolioPositionRepository;
    this.jwtUserService = jwtUserService;
  }

  public PortfolioPositionResponse create(Jwt jwt, CreatePortfolioPositionRequest request) {
    String userId = jwtUserService.requireUserId(jwt);
    PortfolioPosition position = PortfolioPosition.from(userId, request);

    return portfolioPositionRepository.save(position).toDto();
  }

  public List<PortfolioPositionResponse> findAll(Jwt jwt, PortfolioType type) {
    String userId = jwtUserService.requireUserId(jwt);
    List<PortfolioPosition> positions =
        type == null
            ? portfolioPositionRepository.findByUserIdOrderByNameAsc(userId)
            : portfolioPositionRepository.findByUserIdAndType(userId, type);

    return positions.stream().map(PortfolioPosition::toDto).toList();
  }

  public PortfolioPositionResponse updateValue(
      Jwt jwt, String id, UpdatePortfolioValueRequest request) {
    PortfolioPosition position = findOwned(jwt, id);
    position.updateValue(request);

    return portfolioPositionRepository.save(position).toDto();
  }

  public PortfolioPositionResponse update(
      Jwt jwt, String id, UpdatePortfolioPositionRequest request) {
    PortfolioPosition position = findOwned(jwt, id);
    position.update(request);

    return portfolioPositionRepository.save(position).toDto();
  }

  public void delete(Jwt jwt, String id) {
    PortfolioPosition position = findOwned(jwt, id);
    portfolioPositionRepository.delete(position);
  }

  public List<PortfolioPosition> findAllForUser(String userId) {
    return portfolioPositionRepository.findByUserIdOrderByNameAsc(userId);
  }

  private PortfolioPosition findOwned(Jwt jwt, String id) {
    String userId = jwtUserService.requireUserId(jwt);
    PortfolioPosition position =
        portfolioPositionRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Portfolio position not found."));

    if (!userId.equals(position.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio position not found.");
    }

    return position;
  }
}
