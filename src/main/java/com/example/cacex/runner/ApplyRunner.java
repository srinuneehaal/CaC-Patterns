package com.example.cacex.runner;

import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.service.PlanApplyService;
import com.example.cacex.util.CommandLineFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplyRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ApplyRunner.class);
    private static final String ARG_APPLY = "--apply";

    private final PlanApplyService planApplyService;

    public ApplyRunner(PlanApplyService planApplyService) {
        this.planApplyService = planApplyService;
    }

    @Override
    public void run(String... args) {
        if (!CommandLineFlags.hasFlag(args, ARG_APPLY)) {
            log.debug("Apply flag not provided. Skipping plan application.");
            return;
        }
        if (args != null && args.length > 1) {
            log.warn("Extra arguments detected alongside {}: {}", ARG_APPLY, java.util.Arrays.toString(args));
        }
        try {
            planApplyService.applyPlan();
        } catch (PlanApplyException e) {
            log.error("Plan application failed: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected failure applying plan: {}", e.getMessage(), e);
        }
    }
}
