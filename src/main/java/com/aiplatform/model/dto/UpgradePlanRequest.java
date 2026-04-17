package com.aiplatform.model.dto;

import com.aiplatform.model.enums.Plan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpgradePlanRequest(
    @NotBlank(message = "userId is required") String userId,
    @NotNull(message = "targetPlan is required") Plan targetPlan
) {
}
