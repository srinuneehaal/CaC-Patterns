package com.example.cacex.service;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.exception.PlanProcessingException;
import com.example.cacex.exception.UnsupportedFileCategoryException;
import com.example.cacex.exception.UnsupportedFilePathException;
import com.example.cacex.model.*;
import com.example.cacex.service.plan.rules.PlanOrderingRuleEngine;
import com.example.cacex.service.plan.stratagy.FileParsingStrategy;
import com.example.cacex.service.plan.stratagy.FileParsingStrategyFactory;
import com.finbourne.lusid.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

@Service
public class PlanService {

    private static final Logger log = LoggerFactory.getLogger(PlanService.class);
    private static final String JSON_EXTENSION = ".json";

    private final FileLocationProperties fileLocationProperties;
    private final FileParsingStrategyFactory strategyFactory;
    private final PlanOrderingRuleEngine planOrderingRuleEngine;
    private final StateFileService stateFileService;

    /**
     * Creates a plan service that builds master plans from changed files.
     *
     * @param strategyFactory       factory to resolve parsing strategies
     * @param planOrderingRuleEngine engine that orders plan items
     * @param fileLocationProperties filesystem configuration
     * @param stateFileService      service for reading existing state files
     */
    public PlanService(FileParsingStrategyFactory strategyFactory,
                       PlanOrderingRuleEngine planOrderingRuleEngine,
                       FileLocationProperties fileLocationProperties,
                       StateFileService stateFileService) {
        this.fileLocationProperties = fileLocationProperties;
        this.strategyFactory = strategyFactory;
        this.planOrderingRuleEngine = planOrderingRuleEngine;
        this.stateFileService = stateFileService;
    }

    /**
     * Builds an ordered master plan for the given set of changed file paths.
     *
     * @param changedPaths paths under the configured changed files root
     * @return ordered master plan
     */
    public MasterPlan buildPlan(List<Path> changedPaths) {
        Map<String, Object> statePayloads = new HashMap<>();

        MasterPlan plan = new MasterPlan();

        for (Path path : changedPaths) {
            try {
                processChangedFile(path, plan, statePayloads);
            } catch (UnsupportedFilePathException | UnsupportedFileCategoryException e) {
                log.warn("Skipping unsupported file {}: {}", path, e.getMessage());
            } catch (PlanProcessingException e) {
                log.error("Failed to process changed file {}: {}", path, e.getMessage(), e);
            } catch (Exception e) {
                log.error("Unexpected failure processing {}: {}", path, e.getMessage(), e);
            }
        }
        return planOrderingRuleEngine.applyOrdering(plan);
    }

    private void processChangedFile(Path path, MasterPlan plan, Map<String, Object> statePayloads) {
        if (!isChangedFilePath(path)) {
            return;
        }

        FileCategory category = deriveCategory(path);
        String scope = deriveScope(path, fileLocationProperties.getChangedFilesDir());
        log.debug("Processing scope {} for category {}", scope, category);
        String key = stateFileService.deriveKeyFromFilename(path);

        if (category == FileCategory.ABOR) {
            processScopedListCategory(path, plan, category, scope, key, AborFile.class,
                    this::toAborMap, "Failed to scan ABOR state files for scope {}: {}");
            return;
        }

        if (category == FileCategory.ABOR_CONFIGURATION) {
            processScopedListCategory(path, plan, category, scope, key, AborConfigurationFile.class,
                    this::toAborConfigurationMap, "Failed to scan ABOR configuration state files for scope {}: {}");
            return;
        }

        if (category == FileCategory.DERIVED_PORTFOLIO) {
            processScopedListCategory(path, plan, category, scope, key, DerivedPortfolioFile.class,
                    this::toDerivedMap, "Failed to scan derived portfolio state files for scope {}: {}");
            return;
        }

        if (category == FileCategory.PORTFOLIO_GROUP) {
            processScopedListCategory(path, plan, category, scope, key, PortfolioGroupFile.class,
                    this::toPortfolioGroupMap, "Failed to scan portfolio group state files for scope {}: {}");
            return;
        }

        if (category == FileCategory.ACCOUNT) {
            processScopedListCategory(path, plan, category, scope, key, AccountFile.class,
                    this::toAccountMap, "Failed to scan account state files for scope {}: {}");
            return;
        }

        handleGeneralCategory(path, plan, statePayloads, category, scope, key);
    }

    private <F> void processScopedListCategory(Path path,
                                               MasterPlan plan,
                                               FileCategory category,
                                               String scope,
                                               String key,
                                               Class<F> payloadType,
                                               Function<F, Map<String, ?>> mapExtractor,
                                               String errorMessage) {
        Set<String> filesSeen = new HashSet<>();
        filesSeen.add(scopeKey(scope, key));
        processPlanEntries(path, scope, key, plan, category, payloadType, mapExtractor);
        try {
            addDeletesForMissingChanges(category, filesSeen, plan, scope, payloadType, mapExtractor);
        } catch (Exception e) {
            log.error(errorMessage, scope, e.getMessage(), e);
        }
    }

    private void handleGeneralCategory(Path path,
                                       MasterPlan plan,
                                       Map<String, Object> statePayloads,
                                       FileCategory category,
                                       String scope,
                                       String key) {
        String stateKey = mapKey(category, scope, key);
        if (!statePayloads.containsKey(stateKey)) {
            Object payload = stateFileService.loadPayload(category, scope, key, generalPayloadType(category));
            statePayloads.put(stateKey, payload);
        }
        Object state = statePayloads.get(stateKey);

        if (Files.exists(path)) {
            LoadedFile changed = parsePath(path);
            if (changed == null) {
                return;
            }
            if (state == null) {
                plan.addItem(new PlanItem(Action.NEW, changed.getCategory(), scope, key,
                        changed.getPath().toString(), changed.getPayload()));
            } else {
                Object comparableState = normalizeStatePayload(category, state);
                if (!stateFileService.payloadsEqual(changed.getPayload(), comparableState)) {
                    plan.addItem(new PlanItem(Action.UPDATE, changed.getCategory(), scope, key,
                            changed.getPath().toString(), changed.getPayload()));
                }
            }
        } else if (state != null) {
            plan.addItem(new PlanItem(Action.DELETE, category, scope, key,
                    cosmosStateReference(category, scope, key), state));
        }
    }

    private Object normalizeStatePayload(FileCategory category, Object state) {
        if (state == null) {
            return null;
        }
        return switch (category) {
            case CHART_OF_ACCOUNTS -> {
                if (state instanceof ChartOfAccountsFile file) {
                    yield file.getChartOfAccountsRequest();
                }
                yield state;
            }
            case POSTING_RULE -> {
                if (state instanceof PostingRulesFile file) {
                    yield file.getPostingModuleRequest();
                }
                yield state;
            }
            case GENERAL_LEDGER_PROFILE -> {
                if (state instanceof GeneralLedgerProfileFile file) {
                    yield file.getGeneralLedgerProfileRequest();
                }
                yield state;
            }
            default -> state;
        };
    }

    private <F> void addDeletesForMissingChanges(FileCategory category,
                                                    Set<String> filesSeen,
                                                    MasterPlan plan,
                                                    String scope,
                                                    Class<F> payloadType,
                                                    Function<F, Map<String, ?>> mapExtractor) {
        List<StateDocument> documents = stateFileService.listStateDocuments(category, scope);
        for (StateDocument document : documents) {
            String docKey = stateFileService.deriveKeyFromFilename(Path.of(document.getId()));
            String scopedKey = scopeKey(scope, docKey);
            if (filesSeen.contains(scopedKey)) {
                continue;
            }
            F statePayload = stateFileService.payloadFromDocument(document, payloadType);
            if (statePayload == null) {
                continue;
            }
            Map<String, ?> stateMap = mapExtractor.apply(statePayload);
            for (Map.Entry<String, ?> entry : stateMap.entrySet()) {
                plan.addItem(new PlanItem(Action.DELETE, category, scope, entry.getKey(),
                        formatDocumentReference(document), entry.getValue()));
            }
        }
    }

    private Class<?> generalPayloadType(FileCategory category) {
        return switch (category) {
            case SIDE -> SideFile.class;
            case TRANSACTION -> TransactionFile.class;
            case CHART_OF_ACCOUNTS -> ChartOfAccountsFile.class;
            case POSTING_RULE -> PostingRulesFile.class;
            case GENERAL_LEDGER_PROFILE -> GeneralLedgerProfileFile.class;
            default -> Object.class;
        };
    }

    private String cosmosStateReference(FileCategory category, String scope, String key) {
        String normalizedScope = scope == null ? "" : scope;
        return String.format("cosmos://%s/%s/%s", category.name(), normalizedScope, key);
    }

    private String formatDocumentReference(StateDocument document) {
        String normalizedScope = document.getScope() == null ? "" : document.getScope();
        return String.format("cosmos://%s/%s/%s", document.getTypeOfItem(), normalizedScope, document.getId());
    }

    private LoadedFile parsePath(Path path) {
        try {
            FileParsingStrategy strategy = strategyFactory.resolve(path);
            return strategy.parse(path);
        } catch (Exception e) {
            log.error("Failed to parse path {}: {}", path, e.getMessage(), e);
            return null;
        }
    }

    private boolean isChangedFilePath(Path path) {
        String changedRoot = changedFilesRootName();
        if (changedRoot.isEmpty()) {
            log.warn("Changed files root directory is not configured; skipping {}", path);
            return false;
        }
        String normalized = path.toString().toLowerCase(Locale.ROOT);
        if (!normalized.contains(changedRoot.toLowerCase(Locale.ROOT))) {
            log.debug("Ignoring path outside {}: {}", changedRoot, path);
            return false;
        }
        return normalized.endsWith(JSON_EXTENSION);
    }

    private String mapKey(FileCategory category, String scope, String key) {
        return category.name() + ":" + scope + ":" + key;
    }

    private FileCategory deriveCategory(Path path) {
        try {
            return strategyFactory.resolve(path).getCategory();
        } catch (UnsupportedFilePathException e) {
            throw e;
        } catch (Exception e) {
            throw new PlanProcessingException("Unable to determine category for " + path, e);
        }
    }

    private String deriveScope(Path path, String rootFolder) {
        Path normalized = path.toAbsolutePath().normalize();
        for (int i = 0; i < normalized.getNameCount(); i++) {
            if (normalized.getName(i).toString().equalsIgnoreCase(rootFolder) && i + 1 < normalized.getNameCount()) {
                return normalized.getName(i + 1).toString();
            }
        }
        return "";
    }

    private String scopeKey(String scope, String key) {
        return scope + ":" + key;
    }

    private Map<String, CreateDerivedTransactionPortfolioRequest> toDerivedMap(DerivedPortfolioFile file) {
        Map<String, CreateDerivedTransactionPortfolioRequest> map = new HashMap<>();
        if (file == null || file.getDerivedPortfolios() == null) {
            return map;
        }
        for (CreateDerivedTransactionPortfolioRequest request : file.getDerivedPortfolios()) {
            if (request != null && request.getCode() != null) {
                map.put(request.getCode(), request);
            }
        }
        return map;
    }

    private Map<String, CreatePortfolioGroupRequest> toPortfolioGroupMap(PortfolioGroupFile file) {
        Map<String, CreatePortfolioGroupRequest> map = new HashMap<>();
        if (file == null || file.getGroups() == null) {
            return map;
        }
        for (CreatePortfolioGroupRequest request : file.getGroups()) {
            if (request != null && request.getCode() != null) {
                map.put(request.getCode(), request);
            }
        }
        return map;
    }

    private Map<String, Account> toAccountMap(AccountFile file) {
        Map<String, Account> map = new HashMap<>();
        if (file == null || file.getAccounts() == null) {
            return map;
        }
        String chartOfAccountsCode = Optional.ofNullable(file.getChartOfAccountsCode())
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .orElse(null);
        for (Account account : file.getAccounts()) {
            if (account != null && account.getCode() != null) {
                String accountCode = account.getCode();
                String key = chartOfAccountsCode != null ? chartOfAccountsCode + "-" + accountCode : accountCode;
                map.put(key, account);
            }
        }
        return map;
    }

    private Map<String, AborRequest> toAborMap(AborFile file) {
        Map<String, AborRequest> map = new HashMap<>();
        if (file == null || file.getAborRequests() == null) {
            return map;
        }
        for (AborRequest request : file.getAborRequests()) {
            if (request != null && request.getCode() != null) {
                map.put(request.getCode(), request);
            }
        }
        return map;
    }

    private Map<String, AborConfigurationRequest> toAborConfigurationMap(AborConfigurationFile file) {
        Map<String, AborConfigurationRequest> map = new HashMap<>();
        if (file == null || file.getAborConfigurations() == null) {
            return map;
        }
        for (AborConfigurationRequest request : file.getAborConfigurations()) {
            if (request != null && request.getCode() != null) {
                map.put(request.getCode(), request);
            }
        }
        return map;
    }

    private <F> void processPlanEntries(
            Path changedPath,
            String scope,
            String key,
            MasterPlan plan,
            FileCategory category,
            Class<F> payloadType,
            Function<F, Map<String, ?>> mapExtractor) {
        F changedFile = parsePayload(changedPath, payloadType);
        F stateFile = stateFileService.loadPayload(category, scope, key, payloadType);

        Map<String, ?> changedMap = mapExtractor.apply(changedFile);
        Map<String, ?> stateMap = mapExtractor.apply(stateFile);

        for (Map.Entry<String, ?> entry : changedMap.entrySet()) {
            String code = entry.getKey();
            Object changed = entry.getValue();
            Object existing = stateMap.get(code);
            if (existing == null) {
                plan.addItem(new PlanItem(Action.NEW, category, scope, code, changedPath.toString(), changed));
            } else if (!Objects.equals(changed, existing)) {
                plan.addItem(new PlanItem(Action.UPDATE, category, scope, code, changedPath.toString(), changed));
            }
        }

        for (Map.Entry<String, ?> entry : stateMap.entrySet()) {
            String code = entry.getKey();
            if (!changedMap.containsKey(code)) {
                plan.addItem(new PlanItem(Action.DELETE, category, scope, code, cosmosStateReference(category, scope, key), entry.getValue()));
            }
        }
    }

    private <F> F parsePayload(Path path, Class<F> payloadType) {
        if (!Files.exists(path)) {
            return null;
        }
        LoadedFile loaded = parsePath(path);
        if (loaded == null || loaded.getPayload() == null) {
            return null;
        }
        if (!payloadType.isInstance(loaded.getPayload())) {
            return null;
        }
        return payloadType.cast(loaded.getPayload());
    }

    private String changedFilesRootName() {
        return Optional.ofNullable(fileLocationProperties.getChangedFilesDir()).orElse("");
    }

}
