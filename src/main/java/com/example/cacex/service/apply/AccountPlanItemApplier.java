package com.example.cacex.service.apply;

import com.example.cacex.model.FileCategory;
import com.finbourne.lusid.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountPlanItemApplier extends AbstractPlanItemApplier<Account> {

    public AccountPlanItemApplier(AccountApiService apiService) {
        super(Account.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.ACCOUNT;
    }
}
