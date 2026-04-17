package com.aiplatform.model.dto;

import jakarta.validation.constraints.NotBlank;

public record GenerationRequest(
    @NotBlank(message = "userId is required") String userId,
    @NotBlank(message = "prompt is required") String prompt
) {
}
