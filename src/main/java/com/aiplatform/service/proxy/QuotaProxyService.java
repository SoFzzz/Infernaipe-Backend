package com.aiplatform.service.proxy;

import com.aiplatform.exception.QuotaExceededException;
import com.aiplatform.model.dto.GenerationCommand;
import com.aiplatform.model.dto.GenerationResponse;
import com.aiplatform.model.entity.UserQuota;
import com.aiplatform.service.ai.AIGenerationService;
import com.aiplatform.service.quota.QuotaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("quotaProxy")
public class QuotaProxyService implements AIGenerationService {

    private final AIGenerationService next;
    private final QuotaService quotaService;

    public QuotaProxyService(
        @Qualifier("mockAiGenerationService") AIGenerationService next,
        QuotaService quotaService
    ) {
        this.next = next;
        this.quotaService = quotaService;
    }

    @Override
    public GenerationResponse generate(GenerationCommand command) {
        UserQuota quota = quotaService.getOrCreateQuota(command.userId());
        if (!quota.getPlan().isUnlimited()) {
            int estimatedInputTokens = estimateInputTokens(command.prompt());
            if (quota.getRemainingTokens() < estimatedInputTokens) {
                throw new QuotaExceededException(
                    "Monthly token quota exhausted. Remaining: " + quota.getRemainingTokens()
                );
            }
        }

        GenerationResponse response = next.generate(command);
        quotaService.deductAndLog(command.userId(), response.tokensUsed());
        return response;
    }

    private int estimateInputTokens(String prompt) {
        int words = prompt.trim().isEmpty() ? 0 : prompt.trim().split("\\s+").length;
        return (int) Math.ceil(words * 1.3);
    }
}
