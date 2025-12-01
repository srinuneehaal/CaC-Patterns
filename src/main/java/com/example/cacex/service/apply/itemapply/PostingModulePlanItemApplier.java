package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.PostingModuleApiService;
import com.finbourne.lusid.model.PostingModuleRequest;
import org.springframework.stereotype.Component;

@Component
public class PostingModulePlanItemApplier extends AbstractPlanItemApplier<PostingModuleRequest> {

    /**
     * Creates a posting module plan item applier.
     *
     * @param apiService service that performs posting module operations
     */
    public PostingModulePlanItemApplier(PostingModuleApiService apiService) {
        super(PostingModuleRequest.class, apiService);
    }

    /**
     * Supported file category for this applier.
     *
     * @return POSTING_RULE
     */
    @Override
    public FileCategory supportedCategory() {
        return FileCategory.POSTING_RULE;
    }
}
