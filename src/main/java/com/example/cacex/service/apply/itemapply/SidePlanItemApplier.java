package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.SideFile;
import com.example.cacex.service.apply.apiservice.SideDefinitionApiService;
import org.springframework.stereotype.Component;

@Component
public class SidePlanItemApplier extends AbstractPlanItemApplier<SideFile> {

    public SidePlanItemApplier(SideDefinitionApiService apiService) {
        super(SideFile.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.SIDE;
    }
}
