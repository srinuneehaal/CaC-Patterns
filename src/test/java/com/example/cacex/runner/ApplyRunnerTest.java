package com.example.cacex.runner;

import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.service.PlanApplyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplyRunnerTest {

    @Mock
    private PlanApplyService planApplyService;

    @InjectMocks
    private ApplyRunner runner;

    @Test
    void skipsWhenFlagMissing() {
        assertDoesNotThrow(() -> runner.run("--plan"));

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
        when(planApplyService.applyPlan()).thenThrow(new PlanApplyException("boom"));

        assertDoesNotThrow(() -> runner.run("--apply"));
        verify(planApplyService).applyPlan();
    }

    @Test
    void handlesUnexpectedException() {
        when(planApplyService.applyPlan()).thenThrow(new RuntimeException("boom"));

        assertDoesNotThrow(() -> runner.run("--apply"));
        verify(planApplyService).applyPlan();
    }
}
