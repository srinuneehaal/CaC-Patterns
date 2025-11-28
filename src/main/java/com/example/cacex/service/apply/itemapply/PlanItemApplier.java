package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.PlanItem;

public interface PlanItemApplier {

    FileCategory supportedCategory();

    void apply(PlanItem item);
}
