package com.aiplatform.model.dto;

import com.aiplatform.model.enums.Plan;

public record GenerationCommand(
    String userId,
    String prompt,
    Plan plan
) {
}
