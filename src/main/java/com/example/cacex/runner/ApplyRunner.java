package com.example.cacex.runner;

import com.example.cacex.service.PlanApplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
        System.out.println(this.getClass()+":"+Arrays.toString(args));
        if (!hasFlag(args, ARG_APPLY)) {
            log.debug("Apply flag not provided. Skipping plan application.");
            return;
        }
        if (args != null && args.length > 1) {
            log.warn("Extra arguments detected alongside {}: {}", ARG_APPLY, Arrays.toString(args));
        }
        planApplyService.applyPlan();
    }

    private boolean hasFlag(String[] args, String flag) {
        if (args == null || args.length == 0) {
            return false;
        }
        return Arrays.stream(args)
                .filter(arg -> arg != null)
                .map(String::trim)
                .anyMatch(flag::equalsIgnoreCase);
    }
}
