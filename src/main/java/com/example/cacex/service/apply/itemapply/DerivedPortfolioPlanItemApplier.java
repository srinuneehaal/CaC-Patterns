package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.DerivedPortfolioApiService;
import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;
import org.springframework.stereotype.Component;

@Component
public class DerivedPortfolioPlanItemApplier
        extends AbstractPlanItemApplier<CreateDerivedTransactionPortfolioRequest> {

    /**
     * Creates a derived portfolio plan item applier.
     *
     * @param apiService service that performs derived portfolio operations
     */
    public DerivedPortfolioPlanItemApplier(DerivedPortfolioApiService apiService) {
        super(CreateDerivedTransactionPortfolioRequest.class, apiService);
    }

    /**
     * Supported file category for this applier.
     *
     * @return DERIVED_PORTFOLIO
     */
    @Override
    public FileCategory supportedCategory() {
        return FileCategory.DERIVED_PORTFOLIO;
    }
}
