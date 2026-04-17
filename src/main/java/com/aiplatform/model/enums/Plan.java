package com.aiplatform.model.enums;

public enum Plan {
    FREE(10, 200),
    PRO(60, 500),
    ENTERPRISE(999, 800);

    private final int requestsPerMinute;
    private final int monthlyTokens;

    Plan(int requestsPerMinute, int monthlyTokens) {
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokens = monthlyTokens;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public int getMonthlyTokens() {
        return monthlyTokens;
    }

    public boolean isUnlimited() {
        return this.monthlyTokens == Integer.MAX_VALUE;
    }
}
