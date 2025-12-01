package com.example.cacex.service.apply.apiservice;

import com.finbourne.lusid.model.AborRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AborApiService implements PlanItemActionService<AborRequest> {

    private static final Logger log = LoggerFactory.getLogger(AborApiService.class);

    /**
     * Creates an ABOR in the given scope.
     *
     * @param scope   scope name
     * @param key     ABOR key
     * @param payload ABOR payload
     */
    @Override
    public void create(String scope, String key, AborRequest payload) {
        log.info("Create ABOR {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., AborsApi).
    }

    /**
     * Updates an ABOR in the given scope.
     *
     * @param scope   scope name
     * @param key     ABOR key
     * @param payload ABOR payload
     */
    @Override
    public void update(String scope, String key, AborRequest payload) {
        log.info("Update ABOR {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., AborsApi).
    }

    /**
     * Deletes an ABOR in the given scope.
     *
     * @param scope   scope name
     * @param key     ABOR key
     * @param payload ABOR payload
     */
    @Override
    public void delete(String scope, String key, AborRequest payload) {
        log.info("Delete ABOR {} in scope {}", key, scope);
        // Wire real LUSID SDK call here (e.g., AborsApi).
    }
}
