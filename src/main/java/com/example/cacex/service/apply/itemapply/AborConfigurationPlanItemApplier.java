package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.AborConfigurationApiService;
import com.finbourne.lusid.model.AborConfigurationRequest;
import org.springframework.stereotype.Component;

@Component
public class AborConfigurationPlanItemApplier extends AbstractPlanItemApplier<AborConfigurationRequest> {

    public AborConfigurationPlanItemApplier(AborConfigurationApiService apiService) {
        super(AborConfigurationRequest.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.ABOR_CONFIGURATION;
    }
}
