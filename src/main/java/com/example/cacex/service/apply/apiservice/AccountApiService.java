package com.example.cacex.service.apply.apiservice;

import com.finbourne.lusid.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountApiService implements PlanItemActionService<Account> {

    private static final Logger log = LoggerFactory.getLogger(AccountApiService.class);

    @Override
    public void create(String scope, String key, Account payload) {
        log.info("Create account {} in scope {} using payload {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., AccountsApi.createAccount).
    }

    @Override
    public void update(String scope, String key, Account payload) {
        log.info("Update account {} in scope {} using payload {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., AccountsApi.upsertAccount).
    }

    @Override
    public void delete(String scope, String key, Account payload) {
        log.info("Delete account {} in scope {}", key, scope);
        // Wire real LUSID SDK call here (e.g., AccountsApi.deleteAccount).
    }
}
