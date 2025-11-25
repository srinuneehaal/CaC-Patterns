package com.example.cacex.service.apply;

import com.example.cacex.model.FileCategory;
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
