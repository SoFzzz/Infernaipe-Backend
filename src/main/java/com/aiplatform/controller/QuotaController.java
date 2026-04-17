package com.aiplatform.controller;

import com.aiplatform.model.dto.QuotaStatusResponse;
import com.aiplatform.model.dto.UpgradePlanRequest;
import com.aiplatform.model.dto.UsageHistoryResponse;
import com.aiplatform.service.quota.QuotaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    private final QuotaService quotaService;

    public QuotaController(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    @GetMapping("/status")
    public ResponseEntity<QuotaStatusResponse> status(@RequestParam String userId) {
        return ResponseEntity.ok(quotaService.getStatus(userId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<UsageHistoryResponse>> history(@RequestParam String userId) {
        return ResponseEntity.ok(quotaService.getLast7DaysHistory(userId));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<QuotaStatusResponse> upgrade(@Valid @RequestBody UpgradePlanRequest request) {
        quotaService.upgradePlan(request.userId(), request.targetPlan());
        return ResponseEntity.ok(quotaService.getStatus(request.userId()));
    }
}
