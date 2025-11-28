package com.example.cacex.service.apply.apiservice;

import com.example.cacex.model.TransactionFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionTypeApiService implements PlanItemActionService<TransactionFile> {

    private static final Logger log = LoggerFactory.getLogger(TransactionTypeApiService.class);

    @Override
    public void create(String scope, String key, TransactionFile payload) {
        log.info("Create transaction type {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., TransactionConfigurationApi for transaction types).
    }

    @Override
    public void update(String scope, String key, TransactionFile payload) {
        log.info("Update transaction type {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., TransactionConfigurationApi for transaction types).
    }

    @Override
    public void delete(String scope, String key, TransactionFile payload) {
        log.info("Delete transaction type {} in scope {}", key, scope);
        // Wire real LUSID SDK call here (e.g., TransactionConfigurationApi for transaction types).
    }
}
