package com.aiplatform.service.quota;

import com.aiplatform.exception.InvalidPlanUpgradeException;
import com.aiplatform.model.dto.QuotaStatusResponse;
import com.aiplatform.model.dto.UpgradePlanResponse;
import com.aiplatform.model.dto.UsageHistoryResponse;
import com.aiplatform.model.entity.UsageHistory;
import com.aiplatform.model.entity.UserQuota;
import com.aiplatform.model.enums.Plan;
import com.aiplatform.repository.UsageHistoryRepository;
import com.aiplatform.repository.UserQuotaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuotaService {

    private final UserQuotaRepository userQuotaRepository;
    private final UsageHistoryRepository usageHistoryRepository;

    public QuotaService(UserQuotaRepository userQuotaRepository, UsageHistoryRepository usageHistoryRepository) {
        this.userQuotaRepository = userQuotaRepository;
        this.usageHistoryRepository = usageHistoryRepository;
    }

    @Transactional
    public UserQuota getOrCreateQuota(String userId) {
        return userQuotaRepository.findByUserId(userId)
            .orElseGet(() -> userQuotaRepository.save(newUserQuota(userId, Plan.FREE)));
    }

    @Transactional(readOnly = true)
    public Optional<UserQuota> findByUserId(String userId) {
        return userQuotaRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public QuotaStatusResponse getStatus(String userId) {
        UserQuota quota = getOrCreateQuota(userId);
        int remaining = quota.getRemainingTokens();
        double percentageUsed = quota.getPlan().isUnlimited()
            ? 0.0
            : ((double) quota.getTokensUsed() / quota.getMonthlyLimit()) * 100.0;

        return new QuotaStatusResponse(
            quota.getUserId(),
            quota.getPlan(),
            quota.getTokensUsed(),
            remaining,
            quota.getMonthlyLimit(),
            quota.getResetDate(),
            Math.min(100.0, Math.round(percentageUsed * 100.0) / 100.0)
        );
    }

    @Transactional(readOnly = true)
    public List<UsageHistoryResponse> getLast7DaysHistory(String userId) {
        getOrCreateQuota(userId);
        return usageHistoryRepository.findByUserIdOrderByDateDesc(userId, PageRequest.of(0, 7)).stream()
            .map(entry -> new UsageHistoryResponse(entry.getDate(), entry.getTokensUsed(), entry.getRequestCount()))
            .toList();
    }

    @Transactional
    public void deductAndLog(String userId, int usedTokens) {
        UserQuota quota = getOrCreateQuota(userId);
        if (!quota.getPlan().isUnlimited()) {
            quota.setTokensUsed(quota.getTokensUsed() + usedTokens);
            userQuotaRepository.save(quota);
        }

        LocalDate today = LocalDate.now();
        UsageHistory history = usageHistoryRepository.findByUserIdAndDate(userId, today)
            .orElseGet(() -> {
                UsageHistory created = new UsageHistory();
                created.setUserId(userId);
                created.setDate(today);
                created.setTokensUsed(0);
                created.setRequestCount(0);
                return created;
            });

        history.setTokensUsed(history.getTokensUsed() + usedTokens);
        history.setRequestCount(history.getRequestCount() + 1);
        usageHistoryRepository.save(history);
    }

    @Transactional
    public UpgradePlanResponse upgradePlan(String userId, Plan targetPlan) {
        UserQuota quota = getOrCreateQuota(userId);
        Plan previousPlan = quota.getPlan();

        validateUpgrade(targetPlan);

        quota.setPlan(targetPlan);
        quota.setMonthlyLimit(targetPlan.getMonthlyTokens());
        quota.setTokensUsed(0);
        userQuotaRepository.save(quota);

        return new UpgradePlanResponse(
            userId,
            previousPlan,
            targetPlan,
            targetPlan.getMonthlyTokens(),
            LocalDateTime.now()
        );
    }

    @Transactional
    public void resetAllMonthlyQuotas() {
        LocalDate nextResetDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        List<UserQuota> quotas = userQuotaRepository.findAll();
        for (UserQuota quota : quotas) {
            quota.setTokensUsed(0);
            quota.setResetDate(nextResetDate);
        }
        userQuotaRepository.saveAll(quotas);
    }

    private void validateUpgrade(Plan targetPlan) {
        if (targetPlan == null) {
            throw new InvalidPlanUpgradeException("targetPlan is required");
        }
    }

    private UserQuota newUserQuota(String userId, Plan plan) {
        UserQuota quota = new UserQuota();
        quota.setUserId(userId);
        quota.setPlan(plan);
        quota.setTokensUsed(0);
        quota.setMonthlyLimit(plan.getMonthlyTokens());
        quota.setResetDate(LocalDate.now().withDayOfMonth(1).plusMonths(1));
        return quota;
    }
}
