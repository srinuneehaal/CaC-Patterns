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
        properties.setStateFilesDir(tempDir.resolve("statefiles").toString());
        properties.setPlanDir(tempDir.resolve("plan").toString());
        properties.setMasterPlanFile("masterplan.json");

        when(orderingRuleEngine.applyOrdering(any())).thenAnswer(inv -> inv.getArgument(0));
        when(stateFileService.deriveKeyFromFilename(any(Path.class))).thenAnswer(inv -> {
            Path p = inv.getArgument(0);
            String base = PathUtils.baseName(p);
            int dash = base.lastIndexOf('-');
            return dash > 0 ? base.substring(0, dash) : base;
        });
        when(stateFileService.resolveStatePath(any(FileCategory.class), any(), any())).thenAnswer(inv -> {
            FileCategory category = inv.getArgument(0);
            String scope = inv.getArgument(1);
            String key = inv.getArgument(2);
            String folder = switch (category) {
                case ABOR -> properties.getAborDirName();
                case ABOR_CONFIGURATION -> properties.getAborConfigurationsDirName();
                case DERIVED_PORTFOLIO -> properties.getDerivedPortfoliosDirName();
                case PORTFOLIO_GROUP -> properties.getPortfolioGroupsDirName();
                case ACCOUNT -> properties.getAccountsDirName();
                case SIDE -> properties.getSidesDirName();
                case TRANSACTION -> properties.getTransactionsDirName();
                case CHART_OF_ACCOUNTS -> properties.getChartOfAccountsDirName();
                case POSTING_RULE -> properties.getPostingRulesDirName();
            };
            Path base = (scope == null || scope.isBlank())
                    ? properties.stateFilesRoot()
                    : properties.stateFilesRoot().resolve(scope);
            return base.resolve(folder).resolve(key + ".json");
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
        Path stateAbor = createFile("statefiles/S/abor/main.json");
        Path orphanAbor = createFile("statefiles/S/abor/other.json");
        payloads.get(FileCategory.ABOR).put(changedAbor, new LoadedFile(FileCategory.ABOR, "main", changedAbor, aborFile("NEW")));
        payloads.get(FileCategory.ABOR).put(stateAbor, new LoadedFile(FileCategory.ABOR, "main", stateAbor, aborFile("OLD")));
        payloads.get(FileCategory.ABOR).put(orphanAbor, new LoadedFile(FileCategory.ABOR, "other", orphanAbor, aborFile("ORPHAN")));

        Path changedCfg = createFile("changedfiles/S/aborconfigs/cfg.json");
        Path stateCfg = createFile("statefiles/S/aborconfigs/cfg.json");
        Path orphanCfg = createFile("statefiles/S/aborconfigs/other.json");
        payloads.get(FileCategory.ABOR_CONFIGURATION)
                .put(changedCfg, new LoadedFile(FileCategory.ABOR_CONFIGURATION, "cfg", changedCfg, aborConfigFile("NEWCFG")));
        payloads.get(FileCategory.ABOR_CONFIGURATION)
                .put(stateCfg, new LoadedFile(FileCategory.ABOR_CONFIGURATION, "cfg", stateCfg, aborConfigFile("OLDCFG")));
        payloads.get(FileCategory.ABOR_CONFIGURATION)
                .put(orphanCfg, new LoadedFile(FileCategory.ABOR_CONFIGURATION, "other", orphanCfg, aborConfigFile("ORPHANCFG")));

        Path changedDerived = createFile("changedfiles/S/derivedportfolios/derived.json");
        Path stateDerived = createFile("statefiles/S/derivedportfolios/derived.json");
        Path orphanDerived = createFile("statefiles/S/derivedportfolios/other.json");
        payloads.get(FileCategory.DERIVED_PORTFOLIO)
                .put(changedDerived, new LoadedFile(FileCategory.DERIVED_PORTFOLIO, "derived", changedDerived, derivedFile("DP_NEW")));
        payloads.get(FileCategory.DERIVED_PORTFOLIO)
                .put(stateDerived, new LoadedFile(FileCategory.DERIVED_PORTFOLIO, "derived", stateDerived, derivedFile("DP_OLD")));
        payloads.get(FileCategory.DERIVED_PORTFOLIO)
                .put(orphanDerived, new LoadedFile(FileCategory.DERIVED_PORTFOLIO, "other", orphanDerived, derivedFile("DP_ORPHAN")));

        Path changedGroup = createFile("changedfiles/S/portfoliogroups/groups.json");
        Path stateGroup = createFile("statefiles/S/portfoliogroups/groups.json");
        Path orphanGroup = createFile("statefiles/portfoliogroups/other.json");
        payloads.get(FileCategory.PORTFOLIO_GROUP)
                .put(changedGroup, new LoadedFile(FileCategory.PORTFOLIO_GROUP, "groups", changedGroup, groupFile("PG_NEW")));
        payloads.get(FileCategory.PORTFOLIO_GROUP)
                .put(stateGroup, new LoadedFile(FileCategory.PORTFOLIO_GROUP, "groups", stateGroup, groupFile("PG_OLD")));
        payloads.get(FileCategory.PORTFOLIO_GROUP)
                .put(orphanGroup, new LoadedFile(FileCategory.PORTFOLIO_GROUP, "other", orphanGroup, groupFile("PG_ORPHAN")));

        Path changedAccount = createFile("changedfiles/S/gla/accounts.json");
        Path stateAccount = createFile("statefiles/S/gla/accounts.json");
        Path orphanAccount = createFile("statefiles/S/gla/orphan.json");
        payloads.get(FileCategory.ACCOUNT)
                .put(changedAccount, new LoadedFile(FileCategory.ACCOUNT, "accounts", changedAccount, accountFile("COA1", "ACC1")));
        payloads.get(FileCategory.ACCOUNT)
                .put(stateAccount, new LoadedFile(FileCategory.ACCOUNT, "accounts", stateAccount, accountFile("COA1", "ACC2")));
        payloads.get(FileCategory.ACCOUNT)
                .put(orphanAccount, new LoadedFile(FileCategory.ACCOUNT, "orphan", orphanAccount, accountFile("COA1", "ACC3")));

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