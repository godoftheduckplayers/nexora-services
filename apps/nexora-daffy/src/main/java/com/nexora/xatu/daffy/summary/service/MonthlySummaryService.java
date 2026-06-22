package com.nexora.xatu.daffy.summary.service;

import com.nexora.xatu.daffy.fixedexpense.model.FixedExpense;
import com.nexora.xatu.daffy.fixedexpense.service.FixedExpenseService;
import com.nexora.xatu.daffy.portfolio.model.PortfolioPosition;
import com.nexora.xatu.daffy.portfolio.service.PortfolioService;
import com.nexora.xatu.daffy.shared.enums.BudgetCategory;
import com.nexora.xatu.daffy.shared.enums.PortfolioType;
import com.nexora.xatu.daffy.shared.enums.TransactionType;
import com.nexora.xatu.daffy.shared.service.JwtUserService;
import com.nexora.xatu.daffy.summary.dto.response.MonthlyLedgerSummary;
import com.nexora.xatu.daffy.summary.dto.response.PeriodLedgerSummary;
import com.nexora.xatu.daffy.summary.dto.response.PortfolioMovementSummary;
import com.nexora.xatu.daffy.transaction.model.Transaction;
import com.nexora.xatu.daffy.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class MonthlySummaryService {

  private static final int MAX_PERIOD_MONTHS = 24;

  private final TransactionRepository transactionRepository;
  private final FixedExpenseService fixedExpenseService;
  private final PortfolioService portfolioService;
  private final JwtUserService jwtUserService;

  public MonthlySummaryService(
      TransactionRepository transactionRepository,
      FixedExpenseService fixedExpenseService,
      PortfolioService portfolioService,
      JwtUserService jwtUserService) {
    this.transactionRepository = transactionRepository;
    this.fixedExpenseService = fixedExpenseService;
    this.portfolioService = portfolioService;
    this.jwtUserService = jwtUserService;
  }

  public MonthlyLedgerSummary findMonthly(Jwt jwt, Integer year, Integer month) {
    String userId = jwtUserService.requireUserId(jwt);
    YearMonth target =
        year == null || month == null ? YearMonth.now() : YearMonth.of(year, month);

    return buildMonthlySummary(userId, target);
  }

  public PeriodLedgerSummary findPeriod(Jwt jwt, LocalDate from, LocalDate to) {
    String userId = jwtUserService.requireUserId(jwt);
    LocalDate start = from == null ? YearMonth.now().minusMonths(5).atDay(1) : from;
    LocalDate end = to == null ? LocalDate.now() : to;

    if (start.isAfter(end)) {
      throw new IllegalArgumentException("A data inicial deve ser anterior ou igual à data final.");
    }

    YearMonth startMonth = YearMonth.from(start);
    YearMonth endMonth = YearMonth.from(end);
    long monthCount = startMonth.until(endMonth, java.time.temporal.ChronoUnit.MONTHS) + 1;

    if (monthCount > MAX_PERIOD_MONTHS) {
      throw new IllegalArgumentException("O período máximo permitido é de " + MAX_PERIOD_MONTHS + " meses.");
    }

    List<MonthlyLedgerSummary> months = new ArrayList<>();
    YearMonth current = startMonth;

    while (!current.isAfter(endMonth)) {
      months.add(buildMonthlySummary(userId, current));
      current = current.plusMonths(1);
    }

    return new PeriodLedgerSummary(start, end, months);
  }

  private MonthlyLedgerSummary buildMonthlySummary(String userId, YearMonth target) {
    LocalDate from = target.atDay(1);
    LocalDate to = target.atEndOfMonth();

    List<Transaction> transactions =
        transactionRepository.findByUserIdAndOccurredOnBetween(userId, from, to);
    List<FixedExpense> fixedExpenses = fixedExpenseService.findActiveForUser(userId);
    List<PortfolioPosition> positions = portfolioService.findAllForUser(userId);

    BigDecimal totalIncome = sumByType(transactions, TransactionType.INCOME);
    BigDecimal totalExpense = sumByType(transactions, TransactionType.EXPENSE);
    Map<BudgetCategory, BigDecimal> spentByCategory = sumExpensesByCategory(transactions);
    Map<BudgetCategory, BigDecimal> fixedByCategory =
        sumPendingFixedExpensesByCategory(userId, fixedExpenses, target);
    applyPortfolioDepositsToSpentByCategory(spentByCategory, positions, target);

    return new MonthlyLedgerSummary(
        target.getYear(),
        target.getMonthValue(),
        totalIncome,
        totalExpense,
        spentByCategory,
        fixedByCategory,
        summarizeInvestmentMovements(positions, target),
        summarizeBettingMovements(positions, target));
  }

  private PortfolioMovementSummary summarizeInvestmentMovements(
      List<PortfolioPosition> positions, YearMonth target) {
    return summarizeMovements(
        positions,
        target,
        PortfolioType.INVESTMENT_DEPOSIT,
        PortfolioType.INVESTMENT_WITHDRAWAL,
        PortfolioType.INVESTMENT_GAIN,
        PortfolioType.INVESTMENT_LOSS,
        PortfolioType.INVESTMENT);
  }

  private PortfolioMovementSummary summarizeBettingMovements(
      List<PortfolioPosition> positions, YearMonth target) {
    return summarizeMovements(
        positions,
        target,
        PortfolioType.DEPOSIT,
        PortfolioType.WITHDRAWAL,
        PortfolioType.GAIN,
        PortfolioType.LOSS,
        PortfolioType.BETTING);
  }

  private PortfolioMovementSummary summarizeMovements(
      List<PortfolioPosition> positions,
      YearMonth target,
      PortfolioType depositType,
      PortfolioType withdrawalType,
      PortfolioType gainType,
      PortfolioType lossType,
      PortfolioType legacyType) {
    BigDecimal deposits =
        sumMovementAmount(positions, target, depositType)
            .add(sumLegacyDeposits(positions, target, legacyType));
    BigDecimal withdrawals = sumMovementAmount(positions, target, withdrawalType);
    BigDecimal gains = sumMovementAmount(positions, target, gainType);
    BigDecimal losses = sumMovementAmount(positions, target, lossType);
    BigDecimal netFlow = deposits.subtract(withdrawals).add(gains).subtract(losses);

    return new PortfolioMovementSummary(deposits, withdrawals, gains, losses, netFlow);
  }

  private BigDecimal sumLegacyDeposits(
      List<PortfolioPosition> positions, YearMonth target, PortfolioType legacyType) {
    return positions.stream()
        .filter(position -> position.getType() == legacyType)
        .filter(position -> isCreatedInMonth(position, target))
        .map(position -> defaultAmount(position.getInvestedAmount()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal sumMovementAmount(
      List<PortfolioPosition> positions, YearMonth target, PortfolioType type) {
    return positions.stream()
        .filter(position -> position.getType() == type)
        .filter(position -> isCreatedInMonth(position, target))
        .map(position -> movementAmount(type, position))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void applyPortfolioDepositsToSpentByCategory(
      Map<BudgetCategory, BigDecimal> spentByCategory,
      List<PortfolioPosition> positions,
      YearMonth target) {
    positions.stream()
        .filter(position -> isCreatedInMonth(position, target))
        .forEach(
            position -> {
              BudgetCategory category = resolveDepositCategory(position.getType());

              if (category == null) {
                return;
              }

              spentByCategory.merge(category, depositAmount(position), BigDecimal::add);
            });
  }

  private boolean isCreatedInMonth(PortfolioPosition position, YearMonth target) {
    if (position.getCreatedAt() == null) {
      return false;
    }

    YearMonth createdMonth =
        YearMonth.from(position.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate());

    return createdMonth.equals(target);
  }

  private BudgetCategory resolveDepositCategory(PortfolioType type) {
    return switch (type) {
      case INVESTMENT_DEPOSIT, INVESTMENT -> BudgetCategory.INVESTMENT;
      case DEPOSIT, BETTING -> BudgetCategory.BETTING;
      default -> null;
    };
  }

  private BigDecimal depositAmount(PortfolioPosition position) {
    return defaultAmount(position.getInvestedAmount());
  }

  private BigDecimal movementAmount(PortfolioType type, PortfolioPosition position) {
    return switch (type) {
      case GAIN, INVESTMENT_GAIN -> defaultAmount(position.getCurrentValue());
      case DEPOSIT,
          WITHDRAWAL,
          LOSS,
          INVESTMENT_DEPOSIT,
          INVESTMENT_WITHDRAWAL,
          INVESTMENT_LOSS -> defaultAmount(position.getInvestedAmount());
      default -> defaultAmount(position.getInvestedAmount());
    };
  }

  private BigDecimal defaultAmount(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {
    return transactions.stream()
        .filter(transaction -> transaction.getType() == type)
        .map(Transaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private Map<BudgetCategory, BigDecimal> sumExpensesByCategory(List<Transaction> transactions) {
    Map<BudgetCategory, BigDecimal> totals = emptyCategoryMap();

    transactions.stream()
        .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
        .forEach(
            transaction ->
                totals.merge(
                    transaction.getCategory(),
                    transaction.getAmount(),
                    BigDecimal::add));

    return totals;
  }

  private Map<BudgetCategory, BigDecimal> sumPendingFixedExpensesByCategory(
      String userId, List<FixedExpense> fixedExpenses, YearMonth target) {
    Map<BudgetCategory, BigDecimal> totals = emptyCategoryMap();
    LocalDate from = target.atDay(1);
    LocalDate to = target.atEndOfMonth();

    fixedExpenses.forEach(
        expense -> {
          boolean alreadyPaid =
              transactionRepository.existsByUserIdAndFixedExpenseIdAndOccurredOnBetween(
                  userId, expense.getId(), from, to);

          if (!alreadyPaid) {
            totals.merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
          }
        });

    return totals;
  }

  private Map<BudgetCategory, BigDecimal> emptyCategoryMap() {
    Map<BudgetCategory, BigDecimal> map = new EnumMap<>(BudgetCategory.class);

    for (BudgetCategory category : BudgetCategory.values()) {
      map.put(category, BigDecimal.ZERO);
    }

    return map;
  }
}
