package com.nexora.xatu.porky.dashboard.service;

import com.nexora.xatu.porky.budget.model.BudgetProfile;
import com.nexora.xatu.porky.budget.model.BudgetProfile.CategoryAllocation;
import com.nexora.xatu.porky.budget.service.BudgetProfileService;
import com.nexora.xatu.porky.dashboard.dto.response.CategoryPeriodTotal;
import com.nexora.xatu.porky.dashboard.dto.response.CategoryProgress;
import com.nexora.xatu.porky.dashboard.dto.response.MonthlyDashboardResponse;
import com.nexora.xatu.porky.dashboard.dto.response.PeriodDashboardResponse;
import com.nexora.xatu.porky.dashboard.dto.response.PeriodMonthPoint;
import com.nexora.xatu.porky.dashboard.dto.response.PeriodTotals;
import com.nexora.xatu.porky.dashboard.dto.response.PortfolioMovementTotals;
import com.nexora.xatu.porky.dashboard.dto.response.PortfolioProgress;
import com.nexora.xatu.porky.integration.daffy.DaffyClient;
import com.nexora.xatu.porky.integration.daffy.DaffyMonthlyLedgerSummary;
import com.nexora.xatu.porky.integration.daffy.DaffyPeriodLedgerSummary;
import com.nexora.xatu.porky.integration.daffy.DaffyPortfolioMovementSummary;
import com.nexora.xatu.porky.shared.enums.BudgetCategory;
import com.nexora.xatu.porky.shared.enums.BudgetStatus;
import com.nexora.xatu.porky.shared.util.GoalPurposeDefaults;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class DashboardService {

  private static final Map<BudgetCategory, String> CATEGORY_LABELS =
      Map.of(
          BudgetCategory.HOUSING, "Moradia",
          BudgetCategory.FOOD, "Alimentação",
          BudgetCategory.TRANSPORT, "Transporte",
          BudgetCategory.HEALTH, "Saúde",
          BudgetCategory.INVESTMENT, "Investimentos",
          BudgetCategory.SAVINGS, "Reserva",
          BudgetCategory.LEISURE, "Lazer",
          BudgetCategory.BETTING, "Apostas",
          BudgetCategory.OTHER, "Outros");

  private final BudgetProfileService budgetProfileService;
  private final DaffyClient daffyClient;

  public DashboardService(BudgetProfileService budgetProfileService, DaffyClient daffyClient) {
    this.budgetProfileService = budgetProfileService;
    this.daffyClient = daffyClient;
  }

  public MonthlyDashboardResponse findMonthly(Jwt jwt, Integer year, Integer month) {
    BudgetProfile profile = budgetProfileService.findEntity(jwt);
    DaffyMonthlyLedgerSummary ledger =
        daffyClient.fetchMonthlySummary(resolveAuthorizationHeader(), year, month);

    BigDecimal monthlyIncome =
        profile == null || profile.getMonthlyIncome() == null
            ? BigDecimal.ZERO
            : profile.getMonthlyIncome();
    Map<BudgetCategory, BigDecimal> allocationMap = buildAllocationMap(profile);
    List<CategoryProgress> categories = buildCategoryProgressList(profile, monthlyIncome, ledger);

    BigDecimal effectiveIncome =
        ledger.totalIncome().compareTo(BigDecimal.ZERO) > 0
            ? ledger.totalIncome()
            : monthlyIncome;
    BigDecimal balance = effectiveIncome.subtract(ledger.totalExpense());

    return new MonthlyDashboardResponse(
        ledger.year(),
        ledger.month(),
        monthlyIncome,
        ledger.totalIncome(),
        ledger.totalExpense(),
        balance,
        profile != null && profile.getMonthlyIncome() != null,
        categories,
        buildPortfolioProgress("INVESTMENT", "Investimentos", ledger.investment(), monthlyIncome, allocationMap),
        buildPortfolioProgress("BETTING", "Apostas", ledger.betting(), monthlyIncome, allocationMap));
  }

  public PeriodDashboardResponse findPeriod(Jwt jwt, LocalDate from, LocalDate to) {
    BudgetProfile profile = budgetProfileService.findEntity(jwt);
    DaffyPeriodLedgerSummary ledger =
        daffyClient.fetchPeriodSummary(resolveAuthorizationHeader(), from, to);

    BigDecimal monthlyIncome =
        profile == null || profile.getMonthlyIncome() == null
            ? BigDecimal.ZERO
            : profile.getMonthlyIncome();
    boolean profileComplete = profile != null && profile.getMonthlyIncome() != null;

    List<PeriodMonthPoint> months =
        ledger.months().stream()
            .map(month -> buildPeriodMonthPoint(profile, monthlyIncome, month))
            .toList();

    return new PeriodDashboardResponse(
        ledger.from(), ledger.to(), monthlyIncome, profileComplete, months, buildPeriodTotals(months));
  }

  private PeriodMonthPoint buildPeriodMonthPoint(
      BudgetProfile profile, BigDecimal monthlyIncome, DaffyMonthlyLedgerSummary ledger) {
    List<CategoryProgress> categories = buildCategoryProgressList(profile, monthlyIncome, ledger);
    BigDecimal totalBudgetAllocated =
        categories.stream()
            .map(CategoryProgress::budgetAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalCategoryUsed =
        categories.stream()
            .map(CategoryProgress::totalUsed)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal effectiveIncome =
        ledger.totalIncome().compareTo(BigDecimal.ZERO) > 0
            ? ledger.totalIncome()
            : monthlyIncome;
    BigDecimal balance = effectiveIncome.subtract(ledger.totalExpense());

    return new PeriodMonthPoint(
        ledger.year(),
        ledger.month(),
        formatMonthLabel(ledger.year(), ledger.month()),
        ledger.totalIncome(),
        ledger.totalExpense(),
        balance,
        totalBudgetAllocated,
        totalCategoryUsed,
        categories,
        toMovementTotals(ledger.investment()),
        toMovementTotals(ledger.betting()));
  }

  private PeriodTotals buildPeriodTotals(List<PeriodMonthPoint> months) {
    BigDecimal totalIncome = BigDecimal.ZERO;
    BigDecimal totalExpense = BigDecimal.ZERO;
    BigDecimal totalBudgetAllocated = BigDecimal.ZERO;
    BigDecimal totalCategoryUsed = BigDecimal.ZERO;
    PortfolioMovementTotals investment =
        new PortfolioMovementTotals(
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    PortfolioMovementTotals betting =
        new PortfolioMovementTotals(
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    Map<String, CategoryPeriodTotal> categoryTotals = new HashMap<>();

    for (PeriodMonthPoint month : months) {
      totalIncome = totalIncome.add(month.totalIncome());
      totalExpense = totalExpense.add(month.totalExpense());
      totalBudgetAllocated = totalBudgetAllocated.add(month.totalBudgetAllocated());
      totalCategoryUsed = totalCategoryUsed.add(month.totalCategoryUsed());
      investment = mergeMovementTotals(investment, month.investment());
      betting = mergeMovementTotals(betting, month.betting());

      for (CategoryProgress category : month.categories()) {
        categoryTotals.merge(
            category.label(),
            new CategoryPeriodTotal(
                category.label(),
                category.purpose(),
                category.budgetAmount(),
                category.totalUsed(),
                category.remaining()),
            this::mergeCategoryTotals);
      }
    }

    return new PeriodTotals(
        totalIncome,
        totalExpense,
        totalIncome.subtract(totalExpense),
        totalBudgetAllocated,
        totalCategoryUsed,
        categoryTotals.values().stream().sorted((left, right) -> left.label().compareToIgnoreCase(right.label())).toList(),
        investment,
        betting);
  }

  private CategoryPeriodTotal mergeCategoryTotals(
      CategoryPeriodTotal left, CategoryPeriodTotal right) {
    BigDecimal budgetAmount = left.budgetAmount().add(right.budgetAmount());
    BigDecimal usedAmount = left.usedAmount().add(right.usedAmount());

    return new CategoryPeriodTotal(
        left.label(),
        left.purpose(),
        budgetAmount,
        usedAmount,
        budgetAmount.subtract(usedAmount));
  }

  private PortfolioMovementTotals mergeMovementTotals(
      PortfolioMovementTotals left, PortfolioMovementTotals right) {
    BigDecimal deposits = left.deposits().add(right.deposits());
    BigDecimal withdrawals = left.withdrawals().add(right.withdrawals());
    BigDecimal gains = left.gains().add(right.gains());
    BigDecimal losses = left.losses().add(right.losses());

    return new PortfolioMovementTotals(
        deposits,
        withdrawals,
        gains,
        losses,
        deposits.subtract(withdrawals).add(gains).subtract(losses));
  }

  private PortfolioMovementTotals toMovementTotals(DaffyPortfolioMovementSummary summary) {
    return new PortfolioMovementTotals(
        summary.deposits(),
        summary.withdrawals(),
        summary.gains(),
        summary.losses(),
        summary.netFlow());
  }

  private List<CategoryProgress> buildCategoryProgressList(
      BudgetProfile profile,
      BigDecimal monthlyIncome,
      DaffyMonthlyLedgerSummary ledger) {
    if (profile == null || profile.getAllocations() == null) {
      return List.of();
    }

    return profile.getAllocations().stream()
        .filter(
            allocation ->
                allocation.getPercentage() != null
                    && allocation.getPercentage().compareTo(BigDecimal.ZERO) > 0)
        .map(allocation -> buildCategoryProgress(allocation, monthlyIncome, ledger))
        .toList();
  }

  private CategoryProgress buildCategoryProgress(
      CategoryAllocation allocation,
      BigDecimal monthlyIncome,
      DaffyMonthlyLedgerSummary ledger) {
    BudgetCategory category = allocation.getCategory();
    BigDecimal percentage = allocation.getPercentage();
    BigDecimal budgetAmount = calculateBudgetAmount(monthlyIncome, percentage);
    BigDecimal spentAmount =
        ledger.spentByCategory().getOrDefault(category, BigDecimal.ZERO);
    BigDecimal totalUsed = spentAmount;
    BigDecimal remaining = budgetAmount.subtract(totalUsed);
    Integer progressPercent = buildProgressPercent(totalUsed, budgetAmount);
    BudgetStatus status = resolveStatus(progressPercent);

    return new CategoryProgress(
        category,
        resolveAllocationLabel(allocation),
        GoalPurposeDefaults.resolve(allocation.getCategory(), allocation.getPurpose()),
        budgetAmount,
        spentAmount,
        BigDecimal.ZERO,
        totalUsed,
        remaining,
        progressPercent,
        status);
  }

  private String resolveAllocationLabel(CategoryAllocation allocation) {
    if (allocation.getLabel() != null && !allocation.getLabel().isBlank()) {
      return allocation.getLabel().trim();
    }

    return CATEGORY_LABELS.getOrDefault(allocation.getCategory(), allocation.getCategory().name());
  }

  private PortfolioProgress buildPortfolioProgress(
      String type,
      String label,
      DaffyPortfolioMovementSummary summary,
      BigDecimal monthlyIncome,
      Map<BudgetCategory, BigDecimal> allocationMap) {
    BudgetCategory category =
        "BETTING".equals(type) ? BudgetCategory.BETTING : BudgetCategory.INVESTMENT;
    BigDecimal budgetAmount =
        calculateBudgetAmount(monthlyIncome, allocationMap.getOrDefault(category, BigDecimal.ZERO));

    BigDecimal pnl = summary.gains().subtract(summary.losses());
    BigDecimal pnlPercent = BigDecimal.ZERO;

    if (summary.deposits().compareTo(BigDecimal.ZERO) > 0) {
      pnlPercent =
          pnl.multiply(BigDecimal.valueOf(100))
              .divide(summary.deposits(), 2, RoundingMode.HALF_UP);
    }

    return new PortfolioProgress(
        type,
        label,
        summary.deposits(),
        summary.netFlow(),
        pnl,
        pnlPercent,
        budgetAmount);
  }

  private BigDecimal calculateBudgetAmount(BigDecimal monthlyIncome, BigDecimal percentage) {
    if (monthlyIncome == null || percentage == null) {
      return BigDecimal.ZERO;
    }

    return monthlyIncome
        .multiply(percentage)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
  }

  private Integer buildProgressPercent(BigDecimal used, BigDecimal budget) {
    if (budget == null || budget.compareTo(BigDecimal.ZERO) <= 0) {
      return 0;
    }

    return used.multiply(BigDecimal.valueOf(100))
        .divide(budget, 0, RoundingMode.HALF_UP)
        .intValue();
  }

  private BudgetStatus resolveStatus(Integer progressPercent) {
    if (progressPercent == null || progressPercent < 80) {
      return BudgetStatus.ON_TRACK;
    }

    if (progressPercent < 100) {
      return BudgetStatus.NEAR_LIMIT;
    }

    return BudgetStatus.EXCEEDED;
  }

  private Map<BudgetCategory, BigDecimal> buildAllocationMap(BudgetProfile profile) {
    Map<BudgetCategory, BigDecimal> map = new EnumMap<>(BudgetCategory.class);

    for (BudgetCategory category : BudgetCategory.values()) {
      map.put(category, BigDecimal.ZERO);
    }

    if (profile == null || profile.getAllocations() == null) {
      return map;
    }

    profile.getAllocations().forEach(allocation -> map.put(allocation.getCategory(), allocation.getPercentage()));

    return map;
  }

  private String formatMonthLabel(int year, int month) {
    String monthName =
        Month.of(month).getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
    return monthName.substring(0, 1).toUpperCase() + monthName.substring(1) + "/" + year;
  }

  private String resolveAuthorizationHeader() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      throw new IllegalStateException("Request context is unavailable.");
    }

    HttpServletRequest request = attributes.getRequest();
    String authorization = request.getHeader("Authorization");

    if (authorization == null || authorization.isBlank()) {
      throw new IllegalArgumentException("Authorization header is required.");
    }

    return authorization;
  }
}
