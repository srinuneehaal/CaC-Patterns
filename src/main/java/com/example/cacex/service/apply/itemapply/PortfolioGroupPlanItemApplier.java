package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.PortfolioGroupApiService;
import com.finbourne.lusid.model.CreatePortfolioGroupRequest;
import org.springframework.stereotype.Component;

@Component
public class PortfolioGroupPlanItemApplier extends AbstractPlanItemApplier<CreatePortfolioGroupRequest> {

    public PortfolioGroupPlanItemApplier(PortfolioGroupApiService apiService) {
        super(CreatePortfolioGroupRequest.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.PORTFOLIO_GROUP;
    }
}
