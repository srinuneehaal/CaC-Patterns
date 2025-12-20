package com.example.cacex.runner;

import com.example.cacex.config.EnvironmentLookup;
import com.example.cacex.model.MasterPlan;
import com.example.cacex.service.PlanService;
import com.example.cacex.service.plan.ChangedFilesProvider;
import com.example.cacex.service.plan.MasterPlanHtmlReportGenerator;
import com.example.cacex.service.plan.PlanWriter;
import com.example.cacex.util.CommandLineFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Component
public class PlanRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PlanRunner.class);
    private static final String ARG_PLAN = "--plan";
    static final String ENV_MASTER_PLAN_REPORT_ENABLED = "MASTER_PLAN_REPORT_ENABLED";

    private final ChangedFilesProvider changedFilesProvider;
    private final PlanService planService;
    private final PlanWriter planWriter;
    private final MasterPlanHtmlReportGenerator masterPlanHtmlReportGenerator;
    private EnvironmentLookup envLookup = System::getenv;

    /**
     * Creates a plan runner with required collaborators.
     *
     * @param changedFilesProvider provider for changed paths
     * @param planService          builder for master plans
     * @param planWriter           writer for output plans
     * @param masterPlanHtmlReportGenerator report generator that creates `masterplan.html`
     */
    public PlanRunner(ChangedFilesProvider changedFilesProvider,
                      PlanService planService,
                      PlanWriter planWriter,
                      MasterPlanHtmlReportGenerator masterPlanHtmlReportGenerator) {
        this.changedFilesProvider = changedFilesProvider;
        this.planService = planService;
        this.planWriter = planWriter;
        this.masterPlanHtmlReportGenerator = masterPlanHtmlReportGenerator;
    }

    /**
     * Executes plan generation when the --plan flag is present.
     *
     * @param args command-line arguments
     */
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

    /**
     * Builds and writes a master plan using changed file inputs.
     */
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
        if (isReportGenerationEnabled()) {
            Path report = masterPlanHtmlReportGenerator.generateReport(masterPlan);
            log.info("Master plan report written to {}", report.toAbsolutePath());
        } else {
            log.info("Master plan report generation disabled via env {}", ENV_MASTER_PLAN_REPORT_ENABLED);
        }
    }

    private boolean isReportGenerationEnabled() {
        String toggle = envLookup.lookup(ENV_MASTER_PLAN_REPORT_ENABLED);
        System.out.println("MASTER_PLAN_REPORT_ENABLED--->"+toggle+" ");

        if (toggle == null || toggle.isBlank()) {
            return true;
        }
        String normalized = toggle.trim();
        return !normalized.equalsIgnoreCase("false")
                && !normalized.equalsIgnoreCase("off")
                && !normalized.equalsIgnoreCase("no")
                && !normalized.equals("0");
    }

    /**
     * Test hook to override environment lookup without touching real env vars.
     */
    void setEnvLookup(EnvironmentLookup envLookup) {
        this.envLookup = Objects.requireNonNull(envLookup);
    }
}
