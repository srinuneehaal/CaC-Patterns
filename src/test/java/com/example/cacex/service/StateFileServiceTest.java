package com.example.cacex.service;

import com.example.cacex.config.JacksonConfiguration;
import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.model.*;
import com.example.cacex.repository.StateRepository;
import com.example.cacex.service.plan.JsonModelMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finbourne.lusid.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StateFileServiceTest {

    @TempDir
    Path tempDir;

    private StateFileService stateFileService;
    private ObjectMapper objectMapper;
    private InMemoryStateRepository repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryStateRepository();
        stateFileService = new StateFileService(repository, new JsonModelMapper(), JacksonConfiguration.createObjectMapper());
        objectMapper = JacksonConfiguration.createObjectMapper();
    }

    @Test
    void deriveKeyFromFilenameStripsSuffix() {
        Path path = tempDir.resolve("changedfiles/S1/sides/side1-scope.json");
        assertEquals("side1", stateFileService.deriveKeyFromFilename(path));
    }

    @Test
    void applySideCreateStoresDocument() {
        SideFile payload = new SideFile();
        payload.setScope("S1");
        payload.setSide("BUY");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.SIDE, "S1", "BUY", null, payload);

        stateFileService.applyStateChange(item);

        StateDocument stored = repository.find(stateDocumentId("BUY", "S1"), FileCategory.SIDE.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("BUY", body.get("side").asText());
        assertEquals("S1", body.get("scope").asText());
    }

    @Test
    void applyTransactionDeleteRemovesDocument() {
        TransactionFile payload = new TransactionFile();
        payload.setScope("S2");
        PlanItem create = new PlanItem(Action.NEW, FileCategory.TRANSACTION, "S2", "txn", null, payload);
        stateFileService.applyStateChange(create);

        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.TRANSACTION, "S2", "txn", null, null);
        stateFileService.applyStateChange(delete);

        assertTrue(repository.find(stateDocumentId("txn", "S2"), FileCategory.TRANSACTION.name()).isEmpty());
    }

    @Test
    void applyChartOfAccountsStoresDocument() {
        ChartOfAccountsRequest payload = new ChartOfAccountsRequest();
        payload.setCode("COA1");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.CHART_OF_ACCOUNTS, "S3", "COA1", null, payload);

        stateFileService.applyStateChange(item);

        StateDocument stored = repository.find(stateDocumentId("COA1", "S3"), FileCategory.CHART_OF_ACCOUNTS.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("COA1", body.get("chartOfAccountsCode").asText());
    }

    @Test
    void applyPostingRulesCreateAndDelete() {
        PostingModuleRequest payload = new PostingModuleRequest();
        payload.setCode("PM1");
        PlanItem create = new PlanItem(Action.NEW, FileCategory.POSTING_RULE, "S4", "PM1-COA1", null, payload);
        stateFileService.applyStateChange(create);

        StateDocument stored = repository.find(stateDocumentId("PM1-COA1", "S4"), FileCategory.POSTING_RULE.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("PM1", body.get("postingModuleCode").asText());

        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.POSTING_RULE, "S4", "PM1-COA1", null, payload);
        stateFileService.applyStateChange(delete);
        assertTrue(repository.find(stateDocumentId("PM1-COA1", "S4"), FileCategory.POSTING_RULE.name()).isEmpty());
    }

    @Test
    void applyGeneralLedgerProfileCreatesAndDeletesEntry() {
        GeneralLedgerProfileRequest payload = new GeneralLedgerProfileRequest();
        payload.setGeneralLedgerProfileCode("GLP1");
        payload.setDisplayName("GL Profile");
        PlanItem create = new PlanItem(Action.NEW, FileCategory.GENERAL_LEDGER_PROFILE,
                "S13", "GLP1-COA1", null, payload);
        stateFileService.applyStateChange(create);

        StateDocument stored = repository.find(stateDocumentId("GLP1-COA1", "S13"), FileCategory.GENERAL_LEDGER_PROFILE.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("COA1", body.get("chartOfAccountsCode").asText());
        assertEquals("GLP1", body.get("generalLedgerProfileCode").asText());

        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.GENERAL_LEDGER_PROFILE,
                "S13", "GLP1-COA1", null, payload);
        stateFileService.applyStateChange(delete);
        assertTrue(repository.find(stateDocumentId("GLP1-COA1", "S13"), FileCategory.GENERAL_LEDGER_PROFILE.name()).isEmpty());
    }

    @Test
    void applyAborAddAndDelete() {
        AborRequest payload = aborRequest("AB1");
        PlanItem create = new PlanItem(Action.NEW, FileCategory.ABOR, "S5", "AB1", null, payload);
        stateFileService.applyStateChange(create);

        StateDocument stored = repository.find(stateDocumentId("AB1", "S5"), FileCategory.ABOR.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("AB1", body.get("aborRequestList").get(0).get("code").asText());

        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.ABOR, "S5", "AB1", null, payload);
        stateFileService.applyStateChange(delete);
        assertTrue(repository.find(stateDocumentId("AB1", "S5"), FileCategory.ABOR.name()).isEmpty());
    }

    @Test
    void applyAborConfigurationCreatesEntry() {
        AborConfigurationRequest payload = new AborConfigurationRequest();
        payload.setCode("CFG1");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.ABOR_CONFIGURATION, "S6", "CFG1", null, payload);
        stateFileService.applyStateChange(item);

        StateDocument stored = repository.find(stateDocumentId("CFG1", "S6"), FileCategory.ABOR_CONFIGURATION.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("CFG1", body.get("aborConfigurationRequestList").get(0).get("code").asText());
    }

    @Test
    void deleteAborConfigurationUsesFileKeyFromSourcePath() {
        AborConfigurationRequest payload = new AborConfigurationRequest();
        payload.setCode("AborCode-3");
        AborConfigurationFile file = new AborConfigurationFile();
        file.setScope("ATG");
        file.setAborConfigurations(List.of(payload));

        StateDocument document = new StateDocument();
        document.setId("aborconfig-ATG");
        document.setTypeOfItem(FileCategory.ABOR_CONFIGURATION.name());
        document.setScope("ATG");
        document.setData(objectMapper.valueToTree(file));
        repository.upsert(document);

        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.ABOR_CONFIGURATION, "ATG",
                payload.getCode(), "cosmos://ABOR_CONFIGURATION/ATG/aborconfig", payload);
        stateFileService.applyStateChange(delete);

        assertTrue(repository.find("aborconfig-ATG", FileCategory.ABOR_CONFIGURATION.name()).isEmpty());
    }

    @Test
    void applyDerivedPortfolioCreatesEntry() {
        CreateDerivedTransactionPortfolioRequest payload = new CreateDerivedTransactionPortfolioRequest();
        payload.setCode("DP1");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.DERIVED_PORTFOLIO, "S7", "DP1", null, payload);
        stateFileService.applyStateChange(item);

        StateDocument stored = repository.find(stateDocumentId("DP1", "S7"), FileCategory.DERIVED_PORTFOLIO.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("DP1", body.get("createDerivedPortfolioRequestList").get(0).get("code").asText());
    }

    @Test
    void applyPortfolioGroupCreatesEntry() {
        CreatePortfolioGroupRequest payload = new CreatePortfolioGroupRequest();
        payload.setCode("G1");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.PORTFOLIO_GROUP, "S8", "G1", null, payload);
        stateFileService.applyStateChange(item);

        StateDocument stored = repository.find(stateDocumentId("G1", "S8"), FileCategory.PORTFOLIO_GROUP.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("G1", body.get("createPortfolioGroupRequestList").get(0).get("code").asText());
    }

    @Test
    void applyAccountAddAndDelete() {
        Account payload = new Account();
        payload.setCode("AC1");
        payload.setDescription("desc");
        String key = "COA1-AC1";
        PlanItem create = new PlanItem(Action.NEW, FileCategory.ACCOUNT, "S9", key, null, payload);
        stateFileService.applyStateChange(create);

        StateDocument stored = repository.find(stateDocumentId(FileCategory.ACCOUNT, key, "S9"), FileCategory.ACCOUNT.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("COA1", body.get("chartOfAccountsCode").asText());
        assertEquals("AC1", body.get("glAccounts").get(0).get("code").asText());

        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.ACCOUNT, "S9", key, null, payload);
        stateFileService.applyStateChange(delete);
        assertTrue(repository.find(stateDocumentId(FileCategory.ACCOUNT, key, "S9"), FileCategory.ACCOUNT.name()).isEmpty());
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
    void applySideBackfillsScopeWhenMissing() {
        SideFile payload = new SideFile();
        payload.setSide("SELL");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.SIDE, "S10", "SELL", null, payload);

        stateFileService.applyStateChange(item);

        StateDocument stored = repository.find(stateDocumentId("SELL", "S10"), FileCategory.SIDE.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("S10", body.get("scope").asText());
    }

    @Test
    void postingRuleKeyWithoutCoaUsesModuleAsFallback() {
        PostingModuleRequest payload = new PostingModuleRequest();
        payload.setCode("PMX");
        PlanItem create = new PlanItem(Action.NEW, FileCategory.POSTING_RULE, "S11", "PMX", null, payload);

        stateFileService.applyStateChange(create);

        StateDocument stored = repository.find(stateDocumentId("PMX", "S11"), FileCategory.POSTING_RULE.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("PMX", body.get("postingModuleCode").asText());
        assertEquals("PMX", body.get("chartOfAccountsCode").asText());
    }

    @Test
    void accountChartCodeDerivedWhenMissing() {
        Account payload = new Account();
        payload.setCode("AC2");
        PlanItem item = new PlanItem(Action.NEW, FileCategory.ACCOUNT, "S12", "COA2-AC2", null, payload);

        stateFileService.applyStateChange(item);

        StateDocument stored = repository.find(stateDocumentId(FileCategory.ACCOUNT, "COA2-AC2", "S12"), FileCategory.ACCOUNT.name()).orElseThrow();
        JsonNode body = stored.getData();
        assertEquals("COA2", body.get("chartOfAccountsCode").asText());
        assertEquals("AC2", body.get("glAccounts").get(0).get("code").asText());
    }

    private String stateDocumentId(String key, String scope) {
        String normalizedKey = key == null ? "" : key.trim();
        if (scope != null && !scope.isBlank()) {
            String normalizedScope = scope.trim();
            String suffix = "-" + normalizedScope;
            if (!normalizedKey.isEmpty() && normalizedKey.toLowerCase(Locale.ROOT).endsWith(suffix.toLowerCase(Locale.ROOT))) {
                return normalizedKey;
            }
            return normalizedKey + suffix;
        }
        return normalizedKey;
    }

    private String stateDocumentId(FileCategory category, String key, String scope) {
        if (category == FileCategory.ACCOUNT) {
            String chartOfAccountsCode = chartOfAccountsFromKey(key, scope);
            if (chartOfAccountsCode.isBlank()) {
                return stateDocumentId(key, scope);
            }
            String normalizedScope = scope == null ? "" : scope.trim();
            if (normalizedScope.isEmpty()) {
                return chartOfAccountsCode;
            }
            return chartOfAccountsCode + "-" + normalizedScope;
        }
        return stateDocumentId(key, scope);
    }

    private String chartOfAccountsFromKey(String key, String scope) {
        if (key == null) {
            return "";
        }
        String normalizedKey = key.trim();
        if (scope != null && !scope.isBlank()) {
            String normalizedScope = scope.trim();
            String suffix = "-" + normalizedScope;
            if (normalizedKey.endsWith(suffix)) {
                normalizedKey = normalizedKey.substring(0, normalizedKey.length() - suffix.length());
            }
        }
        int dash = normalizedKey.indexOf('-');
        if (dash > 0) {
            return normalizedKey.substring(0, dash);
        }
        return normalizedKey;
    }

    private AborRequest aborRequest(String code) {
        AborRequest request = new AborRequest();
        request.setCode(code);
        return request;
    }

    private static final class InMemoryStateRepository implements StateRepository {

        private final Map<String, StateDocument> store = new HashMap<>();

        private static String key(String id, String typeOfItem) {
            return typeOfItem + ":" + id;
        }

        @Override
        public Optional<StateDocument> find(String id, String typeOfItem) {
            return Optional.ofNullable(store.get(key(id, typeOfItem)));
        }

        @Override
        public List<StateDocument> list(String typeOfItem, String scope) {
            List<StateDocument> documents = new ArrayList<>();
            for (StateDocument document : store.values()) {
                if (!document.getTypeOfItem().equals(typeOfItem)) {
                    continue;
                }
                if (scope == null || scope.isBlank() || scope.equals(document.getScope())) {
                    documents.add(document);
                }
            }
            return documents;
        }

        @Override
        public void upsert(StateDocument document) {
            store.put(key(document.getId(), document.getTypeOfItem()), document);
        }

        @Override
        public void delete(String id, String typeOfItem) {
            store.remove(key(id, typeOfItem));
        }
    }
}
