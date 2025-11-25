package com.example.cacex.service.apply;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.PlanItem;

public interface PlanItemApplier {

    FileCategory supportedCategory();

    void apply(PlanItem item);
}
