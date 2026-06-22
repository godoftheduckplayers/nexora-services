package com.nexora.xatu.daffy.summary.dto.response;

import java.time.LocalDate;
import java.util.List;

public record PeriodLedgerSummary(
    LocalDate from,
    LocalDate to,
    List<MonthlyLedgerSummary> months) {}
