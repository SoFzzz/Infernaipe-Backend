package com.aiplatform.model.dto;

import com.aiplatform.model.enums.Plan;

import java.time.LocalDate;

public record QuotaStatusResponse(
    String userId,
    Plan plan,
    int tokensUsed,
    int tokensRemaining,
    int monthlyLimit,
    LocalDate resetDate,
    double percentageUsed
) {
}
