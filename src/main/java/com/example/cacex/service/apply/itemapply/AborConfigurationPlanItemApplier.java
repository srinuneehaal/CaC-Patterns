package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.AborConfigurationApiService;
import com.finbourne.lusid.model.AborConfigurationRequest;
import org.springframework.stereotype.Component;

@Component
public class AborConfigurationPlanItemApplier extends AbstractPlanItemApplier<AborConfigurationRequest> {

    /**
     * Creates an ABOR configuration plan item applier.
     *
     * @param apiService service that performs ABOR configuration operations
     */
    public AborConfigurationPlanItemApplier(AborConfigurationApiService apiService) {
        super(AborConfigurationRequest.class, apiService);
    }

    /**
     * Supported file category for this applier.
     *
     * @return ABOR_CONFIGURATION
     */
    @Override
    public FileCategory supportedCategory() {
        return FileCategory.ABOR_CONFIGURATION;
    }
}
