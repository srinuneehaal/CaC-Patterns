package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.DerivedPortfolioApiService;
import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;
import org.springframework.stereotype.Component;

@Component
public class DerivedPortfolioPlanItemApplier
        extends AbstractPlanItemApplier<CreateDerivedTransactionPortfolioRequest> {

    public DerivedPortfolioPlanItemApplier(DerivedPortfolioApiService apiService) {
        super(CreateDerivedTransactionPortfolioRequest.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.DERIVED_PORTFOLIO;
    }
}
