package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.GeneralLedgerProfileApiService;
import com.finbourne.lusid.model.GeneralLedgerProfileRequest;
import org.springframework.stereotype.Component;

@Component
public class GeneralLedgerProfilePlanItemApplier extends AbstractPlanItemApplier<GeneralLedgerProfileRequest> {

    /**
     * Creates a plan item applier for general ledger profiles.
     *
     * @param apiService service that handles general ledger profile CRUD
     */
    public GeneralLedgerProfilePlanItemApplier(GeneralLedgerProfileApiService apiService) {
        super(GeneralLedgerProfileRequest.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.GENERAL_LEDGER_PROFILE;
    }
}
