package com.aiplatform.model.dto;

import java.time.LocalDate;

public record UsageHistoryResponse(
    LocalDate date,
    int tokensUsed,
    int requestCount
) {
}
