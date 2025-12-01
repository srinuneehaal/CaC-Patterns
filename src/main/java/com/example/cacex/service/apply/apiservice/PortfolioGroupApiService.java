package com.example.cacex.service.apply.apiservice;

import com.finbourne.lusid.model.CreatePortfolioGroupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortfolioGroupApiService implements PlanItemActionService<CreatePortfolioGroupRequest> {

    private static final Logger log = LoggerFactory.getLogger(PortfolioGroupApiService.class);

    /**
     * Creates a portfolio group in the given scope.
     *
     * @param scope   scope name
     * @param key     group key
     * @param payload portfolio group request
     */
    @Override
    public void create(String scope, String key, CreatePortfolioGroupRequest payload) {
        log.info("Create portfolio group {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., PortfolioGroupsApi).
    }

    /**
     * Updates a portfolio group in the given scope.
     *
     * @param scope   scope name
     * @param key     group key
     * @param payload portfolio group request
     */
    @Override
    public void update(String scope, String key, CreatePortfolioGroupRequest payload) {
        log.info("Update portfolio group {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., PortfolioGroupsApi).
    }

    /**
     * Deletes a portfolio group in the given scope.
     *
     * @param scope   scope name
     * @param key     group key
     * @param payload portfolio group request
     */
    @Override
    public void delete(String scope, String key, CreatePortfolioGroupRequest payload) {
        log.info("Delete portfolio group {} in scope {}", key, scope);
        // Wire real LUSID SDK call here (e.g., PortfolioGroupsApi).
    }
}
