package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.AborApiService;
import com.finbourne.lusid.model.AborRequest;
import org.springframework.stereotype.Component;

@Component
public class AborPlanItemApplier extends AbstractPlanItemApplier<AborRequest> {

    public AborPlanItemApplier(AborApiService apiService) {
        super(AborRequest.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.ABOR;
    }
}
