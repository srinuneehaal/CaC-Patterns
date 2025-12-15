package com.example.cacex.service.plan;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.MasterPlan;
import com.example.cacex.model.PlanItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MasterPlanHtmlReportGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void writesStandaloneHtml() throws Exception {
        FileLocationProperties properties = new FileLocationProperties();
        properties.setPlanDir(tempDir.toString());
        properties.setMasterPlanFile("masterplan.json");

        MasterPlan masterPlan = new MasterPlan();
        masterPlan.addItem(new PlanItem(Action.NEW, FileCategory.SIDE,
                "SCOPE", "KEY-1", "changedfiles\\side.json", Map.of("name", "Buy", "scope", "SCOPE")));
        masterPlan.addItem(new PlanItem(Action.UPDATE, FileCategory.TRANSACTION,
                "TX", "TX-1", "changedfiles\\transaction.json", Map.of("code", "T1")));

        MasterPlanHtmlReportGenerator generator = new MasterPlanHtmlReportGenerator(properties, new ObjectMapper());

        Path report = generator.generateReport(masterPlan);

        assertEquals(tempDir.resolve("masterplan.html"), report);
        String html = Files.readString(report);
        assertTrue(html.contains("Master Plan Report"));
        assertTrue(html.contains("Action distribution"));
        assertTrue(html.contains("Category × action"));
        assertTrue(html.contains("Sort by action"));
        assertTrue(html.contains("TX-1"));
    }
}
