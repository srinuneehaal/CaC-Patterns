package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.TransactionFile;
import com.example.cacex.service.apply.apiservice.TransactionTypeApiService;
import org.springframework.stereotype.Component;

@Component
public class TransactionPlanItemApplier extends AbstractPlanItemApplier<TransactionFile> {

    public TransactionPlanItemApplier(TransactionTypeApiService apiService) {
        super(TransactionFile.class, apiService);
    }

    @Override
    public FileCategory supportedCategory() {
        return FileCategory.TRANSACTION;
    }
}
