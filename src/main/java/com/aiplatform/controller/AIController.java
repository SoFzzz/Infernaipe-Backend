package com.aiplatform.controller;

import com.aiplatform.model.dto.GenerationCommand;
import com.aiplatform.model.dto.GenerationRequest;
import com.aiplatform.model.dto.GenerationResponse;
import com.aiplatform.model.entity.UserQuota;
import com.aiplatform.service.ai.AIGenerationService;
import com.aiplatform.service.quota.QuotaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIGenerationService aiProxyChain;
    private final QuotaService quotaService;

    public AIController(@Qualifier("aiProxyChain") AIGenerationService aiProxyChain, QuotaService quotaService) {
        this.aiProxyChain = aiProxyChain;
        this.quotaService = quotaService;
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerationResponse> generate(@Valid @RequestBody GenerationRequest request) {
        UserQuota quota = quotaService.getOrCreateQuota(request.userId());
        GenerationCommand command = new GenerationCommand(request.userId(), request.prompt(), quota.getPlan());
        return ResponseEntity.ok(aiProxyChain.generate(command));
    }
}
