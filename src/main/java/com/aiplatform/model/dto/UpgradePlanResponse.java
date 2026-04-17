package com.aiplatform.model.dto;

import com.aiplatform.model.enums.Plan;

import java.time.LocalDateTime;

public record UpgradePlanResponse(
    String userId,
    Plan previousPlan,
    Plan newPlan,
    int newMonthlyLimit,
    LocalDateTime upgradedAt
) {
}
