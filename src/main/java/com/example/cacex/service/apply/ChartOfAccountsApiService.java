package com.example.cacex.service.apply;

import com.finbourne.lusid.model.ChartOfAccountsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChartOfAccountsApiService implements PlanItemActionService<ChartOfAccountsRequest> {

    private static final Logger log = LoggerFactory.getLogger(ChartOfAccountsApiService.class);

    @Override
    public void create(String scope, String key, ChartOfAccountsRequest payload) {
        log.info("Create chart of accounts {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., ChartOfAccountsApi.createChartOfAccounts).
    }

    @Override
    public void update(String scope, String key, ChartOfAccountsRequest payload) {
        log.info("Update chart of accounts {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., ChartOfAccountsApi.upsertChartOfAccounts).
    }

    @Override
    public void delete(String scope, String key, ChartOfAccountsRequest payload) {
        log.info("Delete chart of accounts {} in scope {}", key, scope);
        // Wire real LUSID SDK call here (e.g., ChartOfAccountsApi.deleteChartOfAccounts).
    }
}
