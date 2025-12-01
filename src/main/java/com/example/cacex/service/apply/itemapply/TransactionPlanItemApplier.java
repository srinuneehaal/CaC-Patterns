package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.TransactionFile;
import com.example.cacex.service.apply.apiservice.TransactionTypeApiService;
import org.springframework.stereotype.Component;

@Component
public class TransactionPlanItemApplier extends AbstractPlanItemApplier<TransactionFile> {

    /**
     * Creates a transaction plan item applier.
     *
     * @param apiService service that performs transaction type operations
     */
    public TransactionPlanItemApplier(TransactionTypeApiService apiService) {
        super(TransactionFile.class, apiService);
    }

    /**
     * Supported file category for this applier.
     *
     * @return TRANSACTION
     */
    @Override
    public FileCategory supportedCategory() {
        return FileCategory.TRANSACTION;
    }
}
