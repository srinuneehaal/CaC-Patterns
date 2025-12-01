package com.example.cacex.runner;

import com.example.cacex.model.MasterPlan;
import com.example.cacex.service.PlanService;
import com.example.cacex.service.plan.ChangedFilesProvider;
import com.example.cacex.service.plan.PlanWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanRunnerTest {

    @Mock
    private ChangedFilesProvider changedFilesProvider;

    @Mock
    private PlanService planService;

    @Mock
    private PlanWriter planWriter;

    @InjectMocks
    private PlanRunner runner;

    @Test
    void skipsWhenFlagMissing() {
        runner.run("--apply");

        verifyNoInteractions(changedFilesProvider, planService, planWriter);
    }

    @Test
    void abortsWhenNoChangedFilesProvided() {
        when(changedFilesProvider.getChangedPaths()).thenReturn(List.of());

        runner.run("--plan");

        verify(changedFilesProvider).getChangedPaths();
        verifyNoInteractions(planService, planWriter);
    }

    @Test
    void buildsAndWritesMasterPlanWhenFlagPresent() {
        List<Path> changedPaths = List.of(Path.of("changedfiles", "file.json"));
        MasterPlan masterPlan = new MasterPlan();
        Path output = Path.of("plan", "masterplan.json");

        when(changedFilesProvider.getChangedPaths()).thenReturn(changedPaths);
        when(planService.buildPlan(changedPaths)).thenReturn(masterPlan);
        when(planWriter.write(masterPlan)).thenReturn(output);

        runner.run("--plan");

        verify(changedFilesProvider).getChangedPaths();
        verify(planService).buildPlan(changedPaths);
        verify(planWriter).write(masterPlan);
    }

    @Test
    void warnsWhenExtraArgsProvided() {
        when(changedFilesProvider.getChangedPaths()).thenReturn(List.of());

        runner.run("--plan", "--extra");

        verify(changedFilesProvider).getChangedPaths();
    }

    @Test
    void catchesExceptionsDuringPlanExecution() {
        List<Path> changedPaths = List.of(Path.of("changedfiles", "file.json"));
        when(changedFilesProvider.getChangedPaths()).thenReturn(changedPaths);
        when(planService.buildPlan(changedPaths)).thenThrow(new IllegalStateException("boom"));

        runner.run("--plan");

        verify(planService).buildPlan(changedPaths);
        verifyNoInteractions(planWriter);
    }
}
