package com.nexora.xatu.porky.integration.daffy;

import java.time.LocalDate;
import java.util.List;

public record DaffyPeriodLedgerSummary(
    LocalDate from, LocalDate to, List<DaffyMonthlyLedgerSummary> months) {}
