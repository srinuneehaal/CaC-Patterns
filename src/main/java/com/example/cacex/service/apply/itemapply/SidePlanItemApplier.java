package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.SideFile;
import com.example.cacex.service.apply.apiservice.SideDefinitionApiService;
import org.springframework.stereotype.Component;

@Component
public class SidePlanItemApplier extends AbstractPlanItemApplier<SideFile> {

    /**
     * Creates a side plan item applier.
     *
     * @param apiService service that performs side definition operations
     */
    public SidePlanItemApplier(SideDefinitionApiService apiService) {
        super(SideFile.class, apiService);
    }

    /**
     * Supported file category for this applier.
     *
     * @return SIDE
     */
    @Override
    public FileCategory supportedCategory() {
        return FileCategory.SIDE;
    }
}
