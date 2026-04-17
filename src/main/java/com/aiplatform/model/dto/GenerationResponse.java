package com.aiplatform.model.dto;

import java.time.LocalDateTime;

public record GenerationResponse(
    String text,
    int tokensUsed,
    LocalDateTime generatedAt
) {
}
