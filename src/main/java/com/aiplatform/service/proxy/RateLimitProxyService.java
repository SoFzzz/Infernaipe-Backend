package com.aiplatform.service.proxy;

import com.aiplatform.exception.RateLimitExceededException;
import com.aiplatform.model.dto.GenerationCommand;
import com.aiplatform.model.dto.GenerationResponse;
import com.aiplatform.service.ai.AIGenerationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitProxyService implements AIGenerationService {

    private final AIGenerationService next;
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public RateLimitProxyService(@Qualifier("quotaProxy") AIGenerationService next) {
        this.next = next;
    }

    @Override
    public GenerationResponse generate(GenerationCommand command) {
        int limit = command.plan().getRequestsPerMinute();
        if (limit != Integer.MAX_VALUE) {
            int current = counters.computeIfAbsent(command.userId(), ignored -> new AtomicInteger(0)).incrementAndGet();
            if (current > limit) {
                throw new RateLimitExceededException(
                    "Rate limit exceeded for plan " + command.plan(),
                    calculateRetryAfterSeconds()
                );
            }
        }

        return next.generate(command);
    }

    public void resetAllCounters() {
        counters.clear();
    }

    private long calculateRetryAfterSeconds() {
        return 60 - LocalDateTime.now().getSecond();
    }
}
