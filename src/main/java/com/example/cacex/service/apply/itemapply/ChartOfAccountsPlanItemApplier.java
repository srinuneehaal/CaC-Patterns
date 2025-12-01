package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.ChartOfAccountsApiService;
import com.finbourne.lusid.model.ChartOfAccountsRequest;
import org.springframework.stereotype.Component;

@Component
public class ChartOfAccountsPlanItemApplier extends AbstractPlanItemApplier<ChartOfAccountsRequest> {

    /**
     * Creates a chart of accounts plan item applier.
     *
     * @param apiService service that performs chart of accounts operations
     */
    public ChartOfAccountsPlanItemApplier(ChartOfAccountsApiService apiService) {
        super(ChartOfAccountsRequest.class, apiService);
    }

    /**
     * Supported file category for this applier.
     *
     * @return CHART_OF_ACCOUNTS
     */
    @Override
    public FileCategory supportedCategory() {
        return FileCategory.CHART_OF_ACCOUNTS;
    }
}
