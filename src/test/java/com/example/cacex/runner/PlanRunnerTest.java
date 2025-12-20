package com.example.cacex.runner;

import com.example.cacex.model.MasterPlan;
import com.example.cacex.service.PlanService;
import com.example.cacex.service.plan.ChangedFilesProvider;
import com.example.cacex.service.plan.MasterPlanHtmlReportGenerator;
import com.example.cacex.service.plan.PlanWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanRunnerTest {

    @Mock
    private ChangedFilesProvider changedFilesProvider;

    @Mock
    private PlanService planService;

    @Mock
    private PlanWriter planWriter;

    @Mock
    private MasterPlanHtmlReportGenerator masterPlanHtmlReportGenerator;

    private PlanRunner runner;

    @Test
    void skipsWhenFlagMissing() {
        runner = new PlanRunner(changedFilesProvider, planService, planWriter, masterPlanHtmlReportGenerator);
        assertDoesNotThrow(() -> runner.run("--apply"));

        verifyNoInteractions(changedFilesProvider, planService, planWriter, masterPlanHtmlReportGenerator);
    }

    @Test
    void abortsWhenNoChangedFilesProvided() {
        runner = new PlanRunner(changedFilesProvider, planService, planWriter, masterPlanHtmlReportGenerator);
        when(changedFilesProvider.getChangedPaths()).thenReturn(List.of());

        runner.run("--plan");

        verify(changedFilesProvider).getChangedPaths();
        verifyNoInteractions(planService, planWriter, masterPlanHtmlReportGenerator);
    }

    @Test
    void buildsAndWritesMasterPlanWhenFlagPresent() {
        List<Path> changedPaths = List.of(Path.of("changedfiles", "file.json"));
        MasterPlan masterPlan = new MasterPlan();
        Path output = Path.of("plan", "masterplan.json");
        Path report = Path.of("plan", "masterplan.html");

        when(changedFilesProvider.getChangedPaths()).thenReturn(changedPaths);
        when(planService.buildPlan(changedPaths)).thenReturn(masterPlan);
        when(planWriter.write(masterPlan)).thenReturn(output);
        when(masterPlanHtmlReportGenerator.generateReport(masterPlan)).thenReturn(report);

        runner = new PlanRunner(changedFilesProvider, planService, planWriter, masterPlanHtmlReportGenerator);
        runner.setEnvLookup(key -> null);

        runner.run("--plan");

        verify(changedFilesProvider).getChangedPaths();
        verify(planService).buildPlan(changedPaths);
        verify(planWriter).write(masterPlan);
        verify(masterPlanHtmlReportGenerator).generateReport(masterPlan);
    }

    @Test
    void skipsReportGenerationWhenDisabledByEnv() {
        List<Path> changedPaths = List.of(Path.of("changedfiles", "file.json"));
        MasterPlan masterPlan = new MasterPlan();
        Path output = Path.of("plan", "masterplan.json");

        when(changedFilesProvider.getChangedPaths()).thenReturn(changedPaths);
        when(planService.buildPlan(changedPaths)).thenReturn(masterPlan);
        when(planWriter.write(masterPlan)).thenReturn(output);

        runner = new PlanRunner(changedFilesProvider, planService, planWriter, masterPlanHtmlReportGenerator);
        runner.setEnvLookup(key -> PlanRunner.ENV_MASTER_PLAN_REPORT_ENABLED.equals(key) ? "false" : null);

        runner.run("--plan");

        verify(changedFilesProvider).getChangedPaths();
        verify(planService).buildPlan(changedPaths);
        verify(planWriter).write(masterPlan);
        verifyNoInteractions(masterPlanHtmlReportGenerator);
    }

    @Test
    void warnsWhenExtraArgsProvided() {
        runner = new PlanRunner(changedFilesProvider, planService, planWriter, masterPlanHtmlReportGenerator);
        when(changedFilesProvider.getChangedPaths()).thenReturn(List.of());

        runner.run("--plan", "--extra");

        verify(changedFilesProvider).getChangedPaths();
    }

    @Test
    void catchesExceptionsDuringPlanExecution() {
        runner = new PlanRunner(changedFilesProvider, planService, planWriter, masterPlanHtmlReportGenerator);
        List<Path> changedPaths = List.of(Path.of("changedfiles", "file.json"));
        when(changedFilesProvider.getChangedPaths()).thenReturn(changedPaths);
        when(planService.buildPlan(changedPaths)).thenThrow(new IllegalStateException("boom"));

        runner.run("--plan");

        verify(planService).buildPlan(changedPaths);
        verifyNoInteractions(planWriter, masterPlanHtmlReportGenerator);
    }
}
