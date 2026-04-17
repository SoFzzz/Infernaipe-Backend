package com.aiplatform.service.ai;

import com.aiplatform.model.dto.GenerationCommand;
import com.aiplatform.model.dto.GenerationResponse;

public interface AIGenerationService {
    GenerationResponse generate(GenerationCommand command);
}
