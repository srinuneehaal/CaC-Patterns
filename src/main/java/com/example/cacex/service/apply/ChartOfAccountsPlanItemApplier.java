package com.example.cacex.service.apply;

import com.example.cacex.model.FileCategory;
import com.finbourne.lusid.model.ChartOfAccountsRequest;
import org.springframework.stereotype.Component;

@Component
public class ChartOfAccountsPlanItemApplier extends AbstractPlanItemApplier<ChartOfAccountsRequest> {

    public ChartOfAccountsPlanItemApplier(ChartOfAccountsApiService apiService) {
        super(ChartOfAccountsRequest.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.CHART_OF_ACCOUNTS;
    }
}
