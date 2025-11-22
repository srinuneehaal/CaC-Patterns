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
        List<Path> changedPaths = changedFilesProvider.getChangedPaths();
        log.info("Received {} changed file path(s) from CHANGEDFILES", changedPaths.size());
        MasterPlan masterPlan = planService.buildPlan(changedPaths);
        Path output = planWriter.write(masterPlan);
        log.info("Master plan written to {}", output.toAbsolutePath());
    }
}
