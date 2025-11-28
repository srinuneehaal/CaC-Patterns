package com.example.cacex.service.apply.apiservice;

import com.finbourne.lusid.model.PostingModuleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PostingModuleApiService implements PlanItemActionService<PostingModuleRequest> {

    private static final Logger log = LoggerFactory.getLogger(PostingModuleApiService.class);

    @Override
    public void create(String scope, String key, PostingModuleRequest payload) {
        log.info("Create posting module {} in scope {} using payload {}", key, scope, payload);
        // Wire LUSID SDK PostingModulesApi.createPostingModule here.
    }

    @Override
    public void update(String scope, String key, PostingModuleRequest payload) {
        log.info("Update posting module {} in scope {} using payload {}", key, scope, payload);
        // Wire LUSID SDK PostingModulesApi.upsertPostingModule here.
    }

    @Override
    public void delete(String scope, String key, PostingModuleRequest payload) {
        log.info("Delete posting module {} in scope {}", key, scope);
        // Wire LUSID SDK PostingModulesApi.deletePostingModule here.
    }
}
