package com.example.cacex.service;

import com.example.cacex.exception.MissingApplierException;
import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.MasterPlan;
import com.example.cacex.model.PlanItem;
import com.example.cacex.service.apply.ApplyPlanItemResult;
import com.example.cacex.service.apply.ApplyPlanReport;
import com.example.cacex.service.apply.PlanReader;
import com.example.cacex.service.apply.itemapply.PlanItemApplier;
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
    private final StateFileService stateFileService;

    /**
     * Creates a plan apply service with discovered appliers and state updates.
     *
     * @param planReader         reader for the master plan
     * @param stateFileService   state file updater
     * @param discoveredAppliers appliers keyed by category
     */
    public PlanApplyService(PlanReader planReader,
                            StateFileService stateFileService,
                            List<PlanItemApplier> discoveredAppliers) {
        this.planReader = planReader;
        this.stateFileService = stateFileService;
        this.appliers = discoveredAppliers.stream()
                .collect(Collectors.toMap(PlanItemApplier::supportedCategory, applier -> applier,
                        (a, b) -> a, () -> new EnumMap<>(FileCategory.class)));
    }

    /**
     * Reads the master plan and applies each plan item using the matching applier and state updater.
     */
    public ApplyPlanReport applyPlan() {
        MasterPlan masterPlan = readMasterPlan();
        ApplyPlanReport report = new ApplyPlanReport();
        if (masterPlan.getItems().isEmpty()) {
            log.warn("No plan items found; nothing to apply.");
            log.info(report.summary());
            return report;
        }
        log.info("Applying {} plan item(s) from master plan", masterPlan.getItems().size());
        int index = 1;
        for (PlanItem item : masterPlan.getItems()) {
            report.add(applyItem(index++, item));
        }
        logReport(report);
        return report;
    }

    private MasterPlan readMasterPlan() {
        try {
            return planReader.read();
        } catch (Exception e) {
            throw new PlanApplyException("Failed to read master plan", e);
        }
    }

    private PlanItemApplier resolveApplier(FileCategory category) {
        PlanItemApplier applier = appliers.get(category);
        if (applier == null) {
            throw new MissingApplierException("No applier found for category " + category);
        }
        return applier;
    }

    private ApplyPlanItemResult applyItem(int index, PlanItem item) {
        try {
            PlanItemApplier applier = resolveApplier(item.getFileCategory());
            applier.apply(item);
            stateFileService.applyStateChange(item);
            return ApplyPlanItemResult.success(index, item);
        } catch (MissingApplierException e) {
            log.error("Skipping item {}: {}", item.getKey(), e.getMessage());
            return ApplyPlanItemResult.failure(index, item, e.getMessage());
        } catch (PlanApplyException e) {
            log.error("Failed to apply item {}: {}", item.getKey(), e.getMessage(), e);
            return ApplyPlanItemResult.failure(index, item, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error applying item {}: {}", item.getKey(), e.getMessage(), e);
            return ApplyPlanItemResult.failure(index, item, failureMessage(e));
        }
    }

    private void logReport(ApplyPlanReport report) {
        String summary = report.summary();
        if (report.hasFailures()) {
            log.warn(summary);
        } else {
            log.info(summary);
        }
    }

    private String failureMessage(Exception e) {
        if (e == null) {
            return "Unexpected error";
        }
        if (e.getMessage() == null || e.getMessage().isBlank()) {
            return e.getClass().getSimpleName();
        }
        return e.getMessage();
    }
}
