package com.aiplatform.service.ai;

import com.aiplatform.model.dto.GenerationCommand;
import com.aiplatform.model.dto.GenerationResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service("mockAiGenerationService")
public class MockAIGenerationService implements AIGenerationService {

    private static final List<String> RESPONSES = List.of(
        "The architecture should prioritize clear boundaries and measured scalability.",
        "A robust implementation starts with predictable contracts and strict validation.",
        "For AI workloads, combining quota control with back-pressure keeps costs stable."
    );

    @Override
    public GenerationResponse generate(GenerationCommand command) {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        String text = RESPONSES.get(ThreadLocalRandom.current().nextInt(RESPONSES.size()));
        int promptTokens = command.prompt().trim().isEmpty() ? 0 : command.prompt().trim().split("\\s+").length;
        int outputTokens = text.split("\\s+").length;
        return new GenerationResponse(text, promptTokens + outputTokens, LocalDateTime.now());
    }
}
