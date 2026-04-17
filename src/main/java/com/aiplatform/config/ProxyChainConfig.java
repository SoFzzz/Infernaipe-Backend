package com.aiplatform.config;

import com.aiplatform.service.ai.AIGenerationService;
import com.aiplatform.service.proxy.RateLimitProxyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyChainConfig {

    @Bean("aiProxyChain")
    public AIGenerationService aiProxyChain(RateLimitProxyService rateLimitProxyService) {
        return rateLimitProxyService;
    }
}
