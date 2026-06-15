package com.nexora.xatu.chansey.weight.dto.response;

import com.nexora.xatu.chansey.shared.enums.ProgressGranularity;
import java.time.LocalDate;
import java.util.List;

public record WeightProgressResponse(
    ProgressGranularity granularity, LocalDate from, LocalDate to, List<WeightProgressPoint> points) {}
