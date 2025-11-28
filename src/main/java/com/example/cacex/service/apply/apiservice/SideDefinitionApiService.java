package com.example.cacex.service.apply.apiservice;

import com.example.cacex.model.SideFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SideDefinitionApiService implements PlanItemActionService<SideFile> {

    private static final Logger log = LoggerFactory.getLogger(SideDefinitionApiService.class);

    @Override
    public void create(String scope, String key, SideFile payload) {
        log.info("Create side {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., TransactionConfigurationApi for sides).
    }

    @Override
    public void update(String scope, String key, SideFile payload) {
        log.info("Update side {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., TransactionConfigurationApi for sides).
    }

    @Override
    public void delete(String scope, String key, SideFile payload) {
        log.info("Delete side {} in scope {}", key, scope);
        // Wire real LUSID SDK call here (e.g., TransactionConfigurationApi for sides).
    }
}
