package com.example.cacex.runner;

import com.example.cacex.model.MasterPlan;
import com.example.cacex.service.ChangedFilesProvider;
import com.example.cacex.service.PlanService;
import com.example.cacex.service.PlanWriter;
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

        if (args == null || args.length != 1) {
            log.error("Expected exactly one argument: {}", ARG_PLAN);
            return;
        }

        String decisionArg = args[0] == null ? "" : args[0].trim();
        if (decisionArg.isEmpty()) {
            log.error("Argument cannot be null or empty. Expected: {}", ARG_PLAN);
            return;
        }

        if (ARG_PLAN.equalsIgnoreCase(decisionArg)) {
            executePlan();
        } else {
            log.error("Unknown argument: {}. Expected: {}", decisionArg, ARG_PLAN);
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
