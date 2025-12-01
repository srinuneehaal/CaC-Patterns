package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.AborApiService;
import com.finbourne.lusid.model.AborRequest;
import org.springframework.stereotype.Component;

@Component
public class AborPlanItemApplier extends AbstractPlanItemApplier<AborRequest> {

    /**
     * Creates an ABOR plan item applier.
     *
     * @param apiService service that performs ABOR operations
     */
    public AborPlanItemApplier(AborApiService apiService) {
        super(AborRequest.class, apiService);
    }

    /**
     * Supported file category for this applier.
     *
     * @return ABOR
     */
    @Override
    public FileCategory supportedCategory() {
        return FileCategory.ABOR;
    }
}
