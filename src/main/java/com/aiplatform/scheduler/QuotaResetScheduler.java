package com.aiplatform.scheduler;

import com.aiplatform.service.proxy.RateLimitProxyService;
import com.aiplatform.service.quota.QuotaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QuotaResetScheduler {

    private final RateLimitProxyService rateLimitProxy;
    private final QuotaService quotaService;

    public QuotaResetScheduler(
        @Qualifier("rateLimitProxyService") RateLimitProxyService rateLimitProxy,
        QuotaService quotaService
    ) {
        this.rateLimitProxy = rateLimitProxy;
        this.quotaService = quotaService;
    }

    @Scheduled(fixedRate = 60_000)
    public void resetRateLimitCounters() {
        rateLimitProxy.resetAllCounters();
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyQuotas() {
        quotaService.resetAllMonthlyQuotas();
    }
}
