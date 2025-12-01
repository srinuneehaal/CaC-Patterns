package com.example.cacex.service.apply.apiservice;

import com.finbourne.lusid.model.AborConfigurationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AborConfigurationApiService implements PlanItemActionService<AborConfigurationRequest> {

    private static final Logger log = LoggerFactory.getLogger(AborConfigurationApiService.class);

    /**
     * Creates an ABOR configuration in the given scope.
     *
     * @param scope   scope name
     * @param key     configuration key
     * @param payload configuration payload
     */
    @Override
    public void create(String scope, String key, AborConfigurationRequest payload) {
        log.info("Create ABOR configuration {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., AborConfigurationsApi).
    }

    /**
     * Updates an ABOR configuration in the given scope.
     *
     * @param scope   scope name
     * @param key     configuration key
     * @param payload configuration payload
     */
    @Override
    public void update(String scope, String key, AborConfigurationRequest payload) {
        log.info("Update ABOR configuration {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., AborConfigurationsApi).
    }

    /**
     * Deletes an ABOR configuration in the given scope.
     *
     * @param scope   scope name
     * @param key     configuration key
     * @param payload configuration payload
     */
    @Override
    public void delete(String scope, String key, AborConfigurationRequest payload) {
        log.info("Delete ABOR configuration {} in scope {}", key, scope);
        // Wire real LUSID SDK call here (e.g., AborConfigurationsApi).
    }
}
