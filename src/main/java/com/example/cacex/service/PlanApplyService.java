package com.example.cacex.service;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.MasterPlan;
import com.example.cacex.model.PlanItem;
import com.example.cacex.service.apply.PlanItemApplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlanApplyService {

    private static final Logger log = LoggerFactory.getLogger(PlanApplyService.class);

    private final PlanReader planReader;
    private final Map<FileCategory, PlanItemApplier> appliers;

    public PlanApplyService(PlanReader planReader, List<PlanItemApplier> discoveredAppliers) {
        this.planReader = planReader;
        this.appliers = discoveredAppliers.stream()
                .collect(Collectors.toMap(PlanItemApplier::supportedCategory, applier -> applier,
                        (a, b) -> a, () -> new EnumMap<>(FileCategory.class)));
    }

    public void applyPlan() {
        MasterPlan masterPlan = planReader.read();
        if (masterPlan.getItems().isEmpty()) {
            log.warn("No plan items found; nothing to apply.");
            return;
        }
        log.info("Applying {} plan item(s) from master plan", masterPlan.getItems().size());
        for (PlanItem item : masterPlan.getItems()) {
            PlanItemApplier applier = appliers.get(item.getFileCategory());
            if (applier == null) {
                log.error("No applier found for category {}. Skipping item {}", item.getFileCategory(), item.getKey());
                continue;
            }
            applier.apply(item);
        }
    }
}
