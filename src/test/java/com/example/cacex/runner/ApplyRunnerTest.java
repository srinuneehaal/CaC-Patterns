package com.example.cacex.runner;

import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.service.PlanApplyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ApplyRunnerTest {

    @Mock
    private PlanApplyService planApplyService;

    @InjectMocks
    private ApplyRunner runner;

    @Test
    void skipsWhenFlagMissing() {
        runner.run("--plan");

        verifyNoInteractions(planApplyService);
    }

    @Test
    void triggersApplyWhenFlagPresent() {
        runner.run("--apply");

        verify(planApplyService).applyPlan();
    }

    @Test
    void warnsOnExtraArgsButStillApplies() {
        runner.run("--apply", "other");

        verify(planApplyService).applyPlan();
    }

    @Test
    void swallowsPlanApplyException() {
        doThrow(new PlanApplyException("boom")).when(planApplyService).applyPlan();

        assertDoesNotThrow(() -> runner.run("--apply"));
        verify(planApplyService).applyPlan();
    }

    @Test
    void handlesUnexpectedException() {
        doThrow(new RuntimeException("boom")).when(planApplyService).applyPlan();

        assertDoesNotThrow(() -> runner.run("--apply"));
        verify(planApplyService).applyPlan();
    }

    @Test
    void extraArgumentsAreWarnedButApplyRuns() {
        runner.run("--apply", "other");

        verify(planApplyService).applyPlan();
    }
}
