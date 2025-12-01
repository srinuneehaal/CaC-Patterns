package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.PlanItem;

public interface PlanItemApplier {

    /**
     * Returns the file category this applier supports.
     *
     * @return supported category
     */
    FileCategory supportedCategory();

    /**
     * Applies the given plan item using the underlying API.
     *
     * @param item plan item to apply
     */
    void apply(PlanItem item);
}
