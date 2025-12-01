package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.PortfolioGroupApiService;
import com.finbourne.lusid.model.CreatePortfolioGroupRequest;
import org.springframework.stereotype.Component;

@Component
public class PortfolioGroupPlanItemApplier extends AbstractPlanItemApplier<CreatePortfolioGroupRequest> {

    /**
     * Creates a portfolio group plan item applier.
     *
     * @param apiService service that performs portfolio group operations
     */
    public PortfolioGroupPlanItemApplier(PortfolioGroupApiService apiService) {
        super(CreatePortfolioGroupRequest.class, apiService);
    }

    /**
     * Supported file category for this applier.
     *
     * @return PORTFOLIO_GROUP
     */
    @Override
    public FileCategory supportedCategory() {
        return FileCategory.PORTFOLIO_GROUP;
    }
}
