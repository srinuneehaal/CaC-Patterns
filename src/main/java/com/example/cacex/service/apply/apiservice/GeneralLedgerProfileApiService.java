package com.example.cacex.service.apply.apiservice;

import com.finbourne.lusid.model.GeneralLedgerProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GeneralLedgerProfileApiService implements PlanItemActionService<GeneralLedgerProfileRequest> {

    private static final Logger log = LoggerFactory.getLogger(GeneralLedgerProfileApiService.class);

    @Override
    public void create(String scope, String key, GeneralLedgerProfileRequest payload) {
        log.info("Create general ledger profile {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here when available (e.g., GeneralLedgerProfilesApi.createGeneralLedgerProfile).
    }

    @Override
    public void update(String scope, String key, GeneralLedgerProfileRequest payload) {
        log.info("Update general ledger profile {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., GeneralLedgerProfilesApi.upsertGeneralLedgerProfile).
    }

    @Override
    public void delete(String scope, String key, GeneralLedgerProfileRequest payload) {
        log.info("Delete general ledger profile {} in scope {}", key, scope);
        // Wire real LUSID SDK call here (e.g., GeneralLedgerProfilesApi.deleteGeneralLedgerProfile).
    }
}
