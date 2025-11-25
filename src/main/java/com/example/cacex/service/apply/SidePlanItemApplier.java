package com.example.cacex.service.apply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.SideFile;
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
