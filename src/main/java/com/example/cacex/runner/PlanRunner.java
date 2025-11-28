package com.example.cacex.runner;

import com.example.cacex.model.MasterPlan;
import com.example.cacex.service.PlanService;
import com.example.cacex.service.plan.ChangedFilesProvider;
import com.example.cacex.service.plan.PlanWriter;
import com.example.cacex.util.CommandLineFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class PlanRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PlanRunner.class);
    private static final String ARG_PLAN = "--plan";

    private final ChangedFilesProvider changedFilesProvider;
    private final PlanService planService;
    private final PlanWriter planWriter;

    public PlanRunner(ChangedFilesProvider changedFilesProvider, PlanService planService, PlanWriter planWriter) {
        this.changedFilesProvider = changedFilesProvider;
        this.planService = planService;
        this.planWriter = planWriter;
    }

    @Override
    public void run(String... args) {
        if (!CommandLineFlags.hasFlag(args, ARG_PLAN)) {
            log.info("Plan flag not provided. Skipping plan generation.");
            return;
        }
        if (args != null && args.length > 1) {
            log.warn("Extra arguments detected alongside {}: {}", ARG_PLAN, List.of(args));
        }
        try {
            executePlan();
        } catch (Exception e) {
            log.error("Plan generation failed: {}", e.getMessage(), e);
        }
    }

    private void executePlan() {
        List<Path> changedPaths = changedFilesProvider.getChangedPaths();
        log.info("Received {} changed file path(s) from CHANGED_FILES", changedPaths.size());
        if (changedPaths.isEmpty()) {
            log.warn("No changed files provided. Nothing to plan.");
            return;
        }
        MasterPlan masterPlan = planService.buildPlan(changedPaths);
        Path output = planWriter.write(masterPlan);
        log.info("Master plan written to {}", output.toAbsolutePath());
    }
}
