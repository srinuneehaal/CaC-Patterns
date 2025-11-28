package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.PostingModuleApiService;
import com.finbourne.lusid.model.PostingModuleRequest;
import org.springframework.stereotype.Component;

@Component
public class PostingModulePlanItemApplier extends AbstractPlanItemApplier<PostingModuleRequest> {

    public PostingModulePlanItemApplier(PostingModuleApiService apiService) {
        super(PostingModuleRequest.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.POSTING_RULE;
    }
}
