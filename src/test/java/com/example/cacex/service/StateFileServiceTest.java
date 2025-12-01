package com.example.cacex.service;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.PlanItem;
import com.example.cacex.model.SideFile;
import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.service.plan.JsonModelMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finbourne.lusid.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StateFileServiceTest {

    @TempDir
    Path tempDir;

    private StateFileService stateFileService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        FileLocationProperties props = new FileLocationProperties();
        props.setStateFilesDir(tempDir.toString());
        props.setChangedFilesDir(tempDir.resolve("changedfiles").toString());
        props.setPlanDir(tempDir.resolve("plan").toString());
        props.setMasterPlanFile("masterplan.json");
        props.setEnvLookup(__ -> null);
        stateFileService = new StateFileService(props, new JsonModelMapper());
        objectMapper = new ObjectMapper();
    }

    @Test
    void deriveKeyFromFilenameStripsSuffix() {
        Path path = tempDir.resolve("changedfiles/scope/sides/side1-scope.json");
        assertEquals("side1", stateFileService.deriveKeyFromFilename(path));
    }

    @Test
    void resolveStatePathAppendsScopeSuffix() {
        Path resolved = stateFileService.resolveStatePath(FileCategory.SIDE, "SCP", "side1");
        assertTrue(resolved.toString().contains("sides"));
        assertTrue(resolved.getFileName().toString().startsWith("side1-SCP"));
    }

    @Test
    void applySideCreateWritesFile() throws Exception {
        SideFile payload = new SideFile();
        payload.setScope("S1");
        payload.setSide("BUY");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.SIDE, "S1", "BUY", null, payload);

        stateFileService.applyStateChange(item);

        Path expected = tempDir.resolve("S1/sides/BUY-S1.json");
        assertTrue(Files.exists(expected));
        JsonNode node = objectMapper.readTree(expected.toFile());
        assertEquals("BUY", node.get("side").asText());
    }

    @Test
    void applyTransactionDeleteRemovesFileIfPresent() throws Exception {
        Path state = tempDir.resolve("S2/transactions/txn-S2.json");
        Files.createDirectories(state.getParent());
        Files.writeString(state, "{}");
        PlanItem item = new PlanItem(Action.DELETE, FileCategory.TRANSACTION, "S2", "txn-S2", null, null);

        stateFileService.applyStateChange(item);

        assertFalse(Files.exists(state));
    }

    @Test
    void applyChartOfAccountsWritesFile() throws Exception {
        ChartOfAccountsRequest payload = new ChartOfAccountsRequest();
        payload.setCode("COA1");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.CHART_OF_ACCOUNTS, "S3", "COA1-S3", null, payload);

        stateFileService.applyStateChange(item);

        Path path = tempDir.resolve("S3/coa/COA1-S3.json");
        JsonNode node = objectMapper.readTree(path.toFile());
        assertEquals("COA1", node.get("chartOfAccountsCode").asText());
    }

    @Test
    void applyPostingRulesCreateAndDelete() throws Exception {
        PostingModuleRequest payload = new PostingModuleRequest();
        payload.setCode("PM1");
        PlanItem create = new PlanItem(Action.NEW, FileCategory.POSTING_RULE, "S4", "PM1-COA1-S4", null, payload);
        stateFileService.applyStateChange(create);
        Path path = tempDir.resolve("S4/postingrules/PM1-COA1-S4.json");
        assertTrue(Files.exists(path));

        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.POSTING_RULE, "S4", "PM1-COA1-S4", null, payload);
        stateFileService.applyStateChange(delete);
        assertFalse(Files.exists(path));
    }

    @Test
    void applyAborAddAndDelete() throws Exception {
        AborRequest payload = new AborRequest();
        payload.setCode("AB1");
        PlanItem create = new PlanItem(Action.NEW, FileCategory.ABOR, "S5", "AB1", null, payload);
        stateFileService.applyStateChange(create);
        Path path = tempDir.resolve("S5/abor/AB1-S5.json");
        assertTrue(Files.exists(path));

        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.ABOR, "S5", "AB1", null, payload);
        stateFileService.applyStateChange(delete);
        assertFalse(Files.exists(path));
    }

    @Test
    void applyAborConfigurationCreatesEntry() throws Exception {
        AborConfigurationRequest payload = new AborConfigurationRequest();
        payload.setCode("CFG1");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.ABOR_CONFIGURATION, "S6", "CFG1", null, payload);
        stateFileService.applyStateChange(item);
        Path path = tempDir.resolve("S6/aborconfigs/CFG1-S6.json");
        JsonNode node = objectMapper.readTree(path.toFile());
        assertEquals("CFG1", node.get("aborConfigurationRequestList").get(0).get("code").asText());
    }

    @Test
    void applyDerivedPortfolioCreatesEntry() throws Exception {
        CreateDerivedTransactionPortfolioRequest payload = new CreateDerivedTransactionPortfolioRequest();
        payload.setCode("DP1");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.DERIVED_PORTFOLIO, "S7", "DP1", null, payload);
        stateFileService.applyStateChange(item);
        Path path = tempDir.resolve("S7/derivedportfolios/DP1-S7.json");
        JsonNode node = objectMapper.readTree(path.toFile());
        assertEquals("DP1", node.get("createDerivedPortfolioRequestList").get(0).get("code").asText());
    }

    @Test
    void applyPortfolioGroupCreatesEntry() throws Exception {
        CreatePortfolioGroupRequest payload = new CreatePortfolioGroupRequest();
        payload.setCode("G1");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.PORTFOLIO_GROUP, "S8", "G1", null, payload);
        stateFileService.applyStateChange(item);
        Path path = tempDir.resolve("S8/portfoliogroups/G1-S8.json");
        JsonNode node = objectMapper.readTree(path.toFile());
        assertEquals("G1", node.get("createPortfolioGroupRequestList").get(0).get("code").asText());
    }

    @Test
    void applyAccountAddAndDelete() throws Exception {
        Account payload = new Account();
        payload.setCode("AC1");
        payload.setDescription("desc");
        String key = "COA1-AC1-S9";
        PlanItem create = new PlanItem(Action.NEW, FileCategory.ACCOUNT, "S9", key, null, payload);
        stateFileService.applyStateChange(create);
        Path path = tempDir.resolve("S9/gla/COA1-AC1-S9.json");
        assertTrue(Files.exists(path));

        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.ACCOUNT, "S9", key, null, payload);
        stateFileService.applyStateChange(delete);
        assertFalse(Files.exists(path));
    }

    @Test
    void nullItemIsNoOpAndMissingMetadataThrows() {
        stateFileService.applyStateChange(null);

        PlanItem missingCategory = new PlanItem(Action.NEW, null, "S", "K", null, null);
        assertThrows(PlanApplyException.class, () -> stateFileService.applyStateChange(missingCategory));

        PlanItem missingAction = new PlanItem(null, FileCategory.SIDE, "S", "K", null, null);
        assertThrows(PlanApplyException.class, () -> stateFileService.applyStateChange(missingAction));
    }

    @Test
    void deriveKeyFromFilenameNullPathThrows() {
        assertThrows(PlanApplyException.class, () -> stateFileService.deriveKeyFromFilename(null));
    }

    @Test
    void applySideBackfillsScopeWhenMissing() throws Exception {
        SideFile payload = new SideFile();
        payload.setSide("SELL");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.SIDE, "S10", "SELL", null, payload);

        stateFileService.applyStateChange(item);

        Path path = tempDir.resolve("S10/sides/SELL-S10.json");
        JsonNode node = objectMapper.readTree(path.toFile());
        assertEquals("S10", node.get("scope").asText());
    }

    @Test
    void resolveStatePathDoesNotDuplicateScopeSuffix() {
        Path resolved = stateFileService.resolveStatePath(FileCategory.SIDE, "SCP", "name-SCP");
        assertTrue(resolved.getFileName().toString().startsWith("name-SCP"));
    }

    @Test
    void postingRuleKeyWithoutCoaUsesModuleAsFallback() throws Exception {
        PostingModuleRequest payload = new PostingModuleRequest();
        payload.setCode("PMX");
        PlanItem create = new PlanItem(Action.NEW, FileCategory.POSTING_RULE, "S11", "PMX-S11", null, payload);

        stateFileService.applyStateChange(create);

        JsonNode node = objectMapper.readTree(tempDir.resolve("S11/postingrules/PMX-S11.json").toFile());
        assertEquals("PMX", node.get("postingModuleCode").asText());
        assertEquals("PMX", node.get("chartOfAccountsCode").asText());
    }

    @Test
    void accountChartCodeDerivedWhenMissing() throws Exception {
        Account payload = new Account();
        payload.setCode("AC2");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.ACCOUNT, "S12", "COA2-AC2-S12", null, payload);

        stateFileService.applyStateChange(item);

        JsonNode node = objectMapper.readTree(tempDir.resolve("S12/gla/COA2-AC2-S12.json").toFile());
        assertEquals("COA2", node.get("chartOfAccountsCode").asText());
        assertEquals("AC2", node.get("glAccounts").get(0).get("code").asText());
    }
}
