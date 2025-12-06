package com.example.cacex.service;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.*;
import com.example.cacex.service.plan.rules.PlanOrderingRuleEngine;
import com.example.cacex.service.plan.stratagy.FileParsingStrategy;
import com.example.cacex.service.plan.stratagy.FileParsingStrategyFactory;
import com.example.cacex.util.PathUtils;
import com.finbourne.lusid.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PlanServiceSpecialCategoriesTest {

    @TempDir
    Path tempDir;

    @Mock
    private StateFileService stateFileService;

    @Mock
    private PlanOrderingRuleEngine orderingRuleEngine;

    private FileLocationProperties properties;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        properties = new FileLocationProperties();
        properties.setChangedFilesDir(tempDir.resolve("changedfiles").toString());
        properties.setPlanDir(tempDir.resolve("plan").toString());
        properties.setMasterPlanFile("masterplan.json");

        when(orderingRuleEngine.applyOrdering(any())).thenAnswer(inv -> inv.getArgument(0));
        when(stateFileService.deriveKeyFromFilename(any(Path.class))).thenAnswer(inv -> {
            Path p = inv.getArgument(0);
            String base = PathUtils.baseName(p);
            int dash = base.lastIndexOf('-');
            return dash > 0 ? base.substring(0, dash) : base;
        });
    }

    @Test
    void buildsPlanForSpecialCategoriesAndAddsDeletes() throws IOException {
        Map<FileCategory, Map<Path, LoadedFile>> payloads = new EnumMap<>(FileCategory.class);
        payloads.put(FileCategory.ABOR, new HashMap<>());
        payloads.put(FileCategory.ABOR_CONFIGURATION, new HashMap<>());
        payloads.put(FileCategory.DERIVED_PORTFOLIO, new HashMap<>());
        payloads.put(FileCategory.PORTFOLIO_GROUP, new HashMap<>());
        payloads.put(FileCategory.ACCOUNT, new HashMap<>());

        Path changedAbor = createFile("changedfiles/S/abor/main.json");
        payloads.get(FileCategory.ABOR).put(changedAbor, new LoadedFile(FileCategory.ABOR, "main", changedAbor, aborFile("NEW")));

        Path changedCfg = createFile("changedfiles/S/aborconfigs/cfg.json");
        payloads.get(FileCategory.ABOR_CONFIGURATION)
                .put(changedCfg, new LoadedFile(FileCategory.ABOR_CONFIGURATION, "cfg", changedCfg, aborConfigFile("NEWCFG")));

        Path changedDerived = createFile("changedfiles/S/derivedportfolios/derived.json");
        payloads.get(FileCategory.DERIVED_PORTFOLIO)
                .put(changedDerived, new LoadedFile(FileCategory.DERIVED_PORTFOLIO, "derived", changedDerived, derivedFile("DP_NEW")));

        Path changedGroup = createFile("changedfiles/S/portfoliogroups/groups.json");
        payloads.get(FileCategory.PORTFOLIO_GROUP)
                .put(changedGroup, new LoadedFile(FileCategory.PORTFOLIO_GROUP, "groups", changedGroup, groupFile("PG_NEW")));

        Path changedAccount = createFile("changedfiles/S/gla/accounts.json");
        payloads.get(FileCategory.ACCOUNT)
                .put(changedAccount, new LoadedFile(FileCategory.ACCOUNT, "accounts", changedAccount, accountFile("COA1", "ACC1")));

        Map<FileCategory, List<StateDocument>> docMap = new EnumMap<>(FileCategory.class);
        Map<String, Object> payloadMap = new HashMap<>();

        docMap.put(FileCategory.ABOR, List.of(
                stateDocument("ABOR", "main-S", "S"),
                stateDocument("ABOR", "other-S", "S")));
        payloadMap.put("ABOR:main-S", aborFile("OLD"));
        payloadMap.put("ABOR:other-S", aborFile("ORPHAN"));

        docMap.put(FileCategory.ABOR_CONFIGURATION, List.of(
                stateDocument("ABOR_CONFIGURATION", "cfg-S", "S"),
                stateDocument("ABOR_CONFIGURATION", "other-S", "S")));
        payloadMap.put("ABOR_CONFIGURATION:cfg-S", aborConfigFile("OLDCFG"));
        payloadMap.put("ABOR_CONFIGURATION:other-S", aborConfigFile("ORPHANCFG"));

        docMap.put(FileCategory.DERIVED_PORTFOLIO, List.of(
                stateDocument("DERIVED_PORTFOLIO", "derived-S", "S"),
                stateDocument("DERIVED_PORTFOLIO", "other-S", "S")));
        payloadMap.put("DERIVED_PORTFOLIO:derived-S", derivedFile("DP_OLD"));
        payloadMap.put("DERIVED_PORTFOLIO:other-S", derivedFile("DP_ORPHAN"));

        docMap.put(FileCategory.PORTFOLIO_GROUP, List.of(
                stateDocument("PORTFOLIO_GROUP", "groups-S", "S"),
                stateDocument("PORTFOLIO_GROUP", "other-S", "S")));
        payloadMap.put("PORTFOLIO_GROUP:groups-S", groupFile("PG_OLD"));
        payloadMap.put("PORTFOLIO_GROUP:other-S", groupFile("PG_ORPHAN"));

        docMap.put(FileCategory.ACCOUNT, List.of(
                stateDocument("ACCOUNT", "accounts-S", "S"),
                stateDocument("ACCOUNT", "orphan-S", "S")));
        payloadMap.put("ACCOUNT:accounts-S", accountFile("COA1", "ACC2"));
        payloadMap.put("ACCOUNT:orphan-S", accountFile("COA1", "ACC3"));

        when(stateFileService.listStateDocuments(any(FileCategory.class), anyString()))
                .thenAnswer(inv -> docMap.getOrDefault(inv.getArgument(0), List.of()));
        when(stateFileService.payloadFromDocument(any(StateDocument.class), any()))
                .thenAnswer(inv -> {
                    StateDocument document = inv.getArgument(0);
                    return payloadMap.get(document.getTypeOfItem() + ":" + document.getId());
                });

        FileParsingStrategyFactory factory = buildFactory(payloads);
        PlanService planService = new PlanService(factory, orderingRuleEngine, properties, stateFileService);

        MasterPlan plan = planService.buildPlan(List.of(changedAbor, changedCfg, changedDerived, changedGroup, changedAccount));

        assertTrue(plan.getItems().size() >= 10);
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getFileCategory() == FileCategory.ABOR && i.getAction() == Action.DELETE));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getFileCategory() == FileCategory.ABOR_CONFIGURATION && i.getAction() == Action.DELETE));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getFileCategory() == FileCategory.DERIVED_PORTFOLIO && i.getAction() == Action.DELETE));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getFileCategory() == FileCategory.PORTFOLIO_GROUP && i.getAction() == Action.DELETE));
        assertTrue(plan.getItems().stream().anyMatch(i -> i.getFileCategory() == FileCategory.ACCOUNT && i.getAction() == Action.DELETE));
        assertTrue(plan.getItems().stream().anyMatch(i -> "ORPHAN".equals(i.getKey())));
        assertTrue(plan.getItems().stream().anyMatch(i -> "ORPHANCFG".equals(i.getKey())));
        assertTrue(plan.getItems().stream().anyMatch(i -> "DP_ORPHAN".equals(i.getKey())));
        assertTrue(plan.getItems().stream().anyMatch(i -> "PG_ORPHAN".equals(i.getKey())));
        assertTrue(plan.getItems().stream().anyMatch(i -> "COA1-ACC3".equals(i.getKey())));
        assertTrue(plan.getItems().stream().anyMatch(i -> "NEW".equals(i.getKey())));
        assertTrue(plan.getItems().stream().anyMatch(i -> "NEWCFG".equals(i.getKey())));
        assertTrue(plan.getItems().stream().anyMatch(i -> "DP_NEW".equals(i.getKey())));
        assertTrue(plan.getItems().stream().anyMatch(i -> "PG_NEW".equals(i.getKey())));
        assertTrue(plan.getItems().stream().anyMatch(i -> "COA1-ACC1".equals(i.getKey())));
    }

    @Test
    void skipsPathsWhenChangedRootMissing() throws IOException {
        properties.setChangedFilesDir(null);
        FileParsingStrategyFactory factory = new FileParsingStrategyFactory(List.of());
        PlanService planService = new PlanService(factory, orderingRuleEngine, properties, stateFileService);

        Path path = createFile("other/file.json");
        MasterPlan plan = planService.buildPlan(List.of(path));

        assertTrue(plan.getItems().isEmpty());
    }

    private FileParsingStrategyFactory buildFactory(Map<FileCategory, Map<Path, LoadedFile>> payloads) {
        List<FileParsingStrategy> strategies = payloads.entrySet().stream()
                .map(entry -> (FileParsingStrategy) new MapBackedStrategy(entry.getKey(), entry.getValue()))
                .toList();
        return new FileParsingStrategyFactory(strategies);
    }

    private Path createFile(String relative) throws IOException {
        Path path = tempDir.resolve(relative);
        Files.createDirectories(path.getParent());
        Files.writeString(path, "{}");
        return path;
    }

    private AborFile aborFile(String... codes) {
        AborFile file = new AborFile();
        file.setScope("S");
        List<AborRequest> requests = java.util.Arrays.stream(codes).map(code -> {
            AborRequest req = new AborRequest();
            req.setCode(code);
            return req;
        }).toList();
        file.setAborRequests(requests);
        return file;
    }

    private AborConfigurationFile aborConfigFile(String... codes) {
        AborConfigurationFile file = new AborConfigurationFile();
        file.setScope("S");
        List<AborConfigurationRequest> requests = java.util.Arrays.stream(codes).map(code -> {
            AborConfigurationRequest req = new AborConfigurationRequest();
            req.setCode(code);
            return req;
        }).toList();
        file.setAborConfigurations(requests);
        return file;
    }

    private DerivedPortfolioFile derivedFile(String... codes) {
        DerivedPortfolioFile file = new DerivedPortfolioFile();
        file.setScope("S");
        List<CreateDerivedTransactionPortfolioRequest> list = java.util.Arrays.stream(codes).map(code -> {
            CreateDerivedTransactionPortfolioRequest req = new CreateDerivedTransactionPortfolioRequest();
            req.setCode(code);
            return req;
        }).toList();
        file.setDerivedPortfolios(list);
        return file;
    }

    private PortfolioGroupFile groupFile(String... codes) {
        PortfolioGroupFile file = new PortfolioGroupFile();
        file.setScope("S");
        List<CreatePortfolioGroupRequest> list = java.util.Arrays.stream(codes).map(code -> {
            CreatePortfolioGroupRequest req = new CreatePortfolioGroupRequest();
            req.setCode(code);
            return req;
        }).toList();
        file.setGroups(list);
        return file;
    }

    private AccountFile accountFile(String coaCode, String... accountCodes) {
        AccountFile file = new AccountFile();
        file.setScope("S");
        file.setChartOfAccountsCode(coaCode);
        List<Account> accounts = java.util.Arrays.stream(accountCodes).map(code -> {
            Account account = new Account();
            account.setCode(code);
            return account;
        }).toList();
        file.setAccounts(accounts);
        return file;
    }

    private StateDocument stateDocument(String typeOfItem, String id, String scope) {
        StateDocument document = new StateDocument();
        document.setTypeOfItem(typeOfItem);
        document.setId(id);
        document.setScope(scope);
        return document;
    }

    private record MapBackedStrategy(FileCategory category, Map<Path, LoadedFile> payloads) implements FileParsingStrategy {
        @Override
        public FileCategory getCategory() {
            return category;
        }

        @Override
        public boolean supports(Path path) {
            return payloads.containsKey(path);
        }

        @Override
        public LoadedFile parse(Path path) {
            return payloads.get(path);
        }
    }
}
