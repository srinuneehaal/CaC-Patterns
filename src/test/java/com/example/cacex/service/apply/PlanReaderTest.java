package com.example.cacex.service.apply;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.config.JacksonConfiguration;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.MasterPlan;
import com.example.cacex.model.PlanItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PlanReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void readsPlanItemsAndConvertsPayload() throws IOException {
        Path planFile = tempDir.resolve("masterplan.json");
        String json = """
                {
                  "items": [
                    {
                      "action": "NEW",
                      "fileCategory": "SIDE",
                      "scope": "SCP",
                      "key": "BUY",
                      "sourcePath": "path",
                      "payload": {
                        "scope": "SCP",
                        "side": "BUY",
                        "sideDefinitionRequest": { "currency": "USD" }
                      }
                    }
                  ]
                }
                """;
        Files.writeString(planFile, json);

        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir(tempDir.toString());
        props.setMasterPlanFile("masterplan.json");

        PlanReader reader = new PlanReader(props, JacksonConfiguration.createObjectMapper());

        MasterPlan plan = reader.read();
        assertEquals(1, plan.getItems().size());
        PlanItem item = plan.getItems().get(0);
        assertEquals(FileCategory.SIDE, item.getFileCategory());
        assertNotNull(item.getPayload());
        assertEquals("BUY", item.getKey());
    }

    @Test
    void missingItemsArrayReturnsEmptyPlan() throws IOException {
        Path planFile = tempDir.resolve("masterplan.json");
        Files.writeString(planFile, "{}");
        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir(tempDir.toString());
        props.setMasterPlanFile("masterplan.json");

        PlanReader reader = new PlanReader(props, JacksonConfiguration.createObjectMapper());

        MasterPlan plan = reader.read();
        assertTrue(plan.getItems().isEmpty());
    }

    @Test
    void missingFileThrowsIllegalStateException() {
        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir(tempDir.toString());
        props.setMasterPlanFile("missing.json");
        PlanReader reader = new PlanReader(props, JacksonConfiguration.createObjectMapper());

        assertThrows(IllegalStateException.class, reader::read);
    }

    @Test
    void convertsAllPayloadTypes() throws IOException {
        Path planFile = tempDir.resolve("masterplan.json");
        String json = """
                {
                  "items": [
                    {"action":"NEW","fileCategory":"ABOR_CONFIGURATION","scope":"S","key":"cfg","payload":{"code":"CFG"}},
                    {"action":"NEW","fileCategory":"ABOR","scope":"S","key":"abor","payload":{"code":"AB"}},
                    {"action":"NEW","fileCategory":"DERIVED_PORTFOLIO","scope":"S","key":"dp","payload":{"code":"DP"}},
                    {"action":"NEW","fileCategory":"PORTFOLIO_GROUP","scope":"S","key":"pg","payload":{"code":"PG"}},
                    {"action":"NEW","fileCategory":"CHART_OF_ACCOUNTS","scope":"S","key":"coa","payload":{"code":"COA"}},
                    {"action":"NEW","fileCategory":"ACCOUNT","scope":"S","key":"acc","payload":{"code":"ACC"}},
                    {"action":"NEW","fileCategory":"POSTING_RULE","scope":"S","key":"pr","payload":{"code":"PM"}},
                    {"action":"NEW","fileCategory":"TRANSACTION","scope":"S","key":"txn","payload":{"scope":"S","transactionType":"T","transactionClass":"C","source":"SRC"}},
                    {"action":"NEW","fileCategory":"SIDE","scope":"S","key":"side","payload":{"scope":"S","side":"BUY"}}
                  ]
                }
                """;
        Files.writeString(planFile, json);

        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir(tempDir.toString());
        props.setMasterPlanFile("masterplan.json");

        MasterPlan plan = new PlanReader(props, JacksonConfiguration.createObjectMapper()).read();

        assertEquals(9, plan.getItems().size());
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getPayload() instanceof com.finbourne.lusid.model.AborConfigurationRequest));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getPayload() instanceof com.finbourne.lusid.model.AborRequest));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getPayload() instanceof com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getPayload() instanceof com.finbourne.lusid.model.CreatePortfolioGroupRequest));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getPayload() instanceof com.finbourne.lusid.model.ChartOfAccountsRequest));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getPayload() instanceof com.finbourne.lusid.model.Account));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getPayload() instanceof com.finbourne.lusid.model.PostingModuleRequest));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getPayload() instanceof com.example.cacex.model.TransactionFile));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getPayload() instanceof com.example.cacex.model.SideFile));
    }

    @Test
    void malformedJsonThrowsIllegalStateException() throws IOException {
        Path planFile = tempDir.resolve("masterplan.json");
        Files.writeString(planFile, "{not-json");
        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir(tempDir.toString());
        props.setMasterPlanFile("masterplan.json");

        PlanReader reader = new PlanReader(props, JacksonConfiguration.createObjectMapper());

        assertThrows(IllegalStateException.class, reader::read);
    }

    @Test
    void nullCategoryPayloadReturnsNull() throws IOException {
        Path planFile = tempDir.resolve("masterplan.json");
        Files.writeString(planFile, """
                {
                  "items": [
                    {
                      "action": "NEW",
                      "fileCategory": null,
                      "scope": "S",
                      "key": "K",
                      "payload": { "some": "value" }
                    }
                  ]
                }
                """);
        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir(tempDir.toString());
        props.setMasterPlanFile("masterplan.json");

        MasterPlan plan = new PlanReader(props, JacksonConfiguration.createObjectMapper()).read();

        assertNull(plan.getItems().get(0).getPayload());
    }
}
