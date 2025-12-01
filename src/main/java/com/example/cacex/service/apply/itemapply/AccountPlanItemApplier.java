package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.service.apply.apiservice.AccountApiService;
import com.finbourne.lusid.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountPlanItemApplier extends AbstractPlanItemApplier<Account> {

    /**
     * Creates an account plan item applier.
     *
     * @param apiService service that performs account operations
     */
    public AccountPlanItemApplier(AccountApiService apiService) {
        super(Account.class, apiService);
    }

    /**
     * Supported file category for this applier.
     *
     * @return ACCOUNT
     */
    @Override
    public FileCategory supportedCategory() {
        return FileCategory.ACCOUNT;
    }
}
