package com.example.cacex.service.apply.apiservice;

import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DerivedPortfolioApiService implements PlanItemActionService<CreateDerivedTransactionPortfolioRequest> {

    private static final Logger log = LoggerFactory.getLogger(DerivedPortfolioApiService.class);

    /**
     * Creates a derived portfolio in the given scope.
     *
     * @param scope   scope name
     * @param key     portfolio key
     * @param payload derived portfolio request
     */
    @Override
    public void create(String scope, String key, CreateDerivedTransactionPortfolioRequest payload) {
        log.info("Create derived portfolio {} in scope {} using request {}", key, scope, payload);
        // Wire real LUSID SDK call here (e.g., DerivedTransactionPortfoliosApi).
    }

    /**
     * Updates a derived portfolio in the given scope.
     *
     * @param scope   scope name
     * @param key     portfolio key
     * @param payload derived portfolio request
     */
    @Override
    public void update(String scope, String key, CreateDerivedTransactionPortfolioRequest payload) {
        log.info("Update derived portfolio {} in scope {} using request {}", key, scope, payload.getCode());
        // Wire real LUSID SDK call here (e.g., DerivedTransactionPortfoliosApi).
    }

    /**
     * Deletes a derived portfolio in the given scope.
     *
     * @param scope   scope name
     * @param key     portfolio key
     * @param payload derived portfolio request
     */
    @Override
    public void delete(String scope, String key, CreateDerivedTransactionPortfolioRequest payload) {
        log.info("Delete derived portfolio {} in scope {}", key, scope);
        // Wire real LUSID SDK call here (e.g., DerivedTransactionPortfoliosApi).
    }
}
