package com.example.cacex.service;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.exception.PlanProcessingException;
import com.example.cacex.exception.UnsupportedFileCategoryException;
import com.example.cacex.exception.UnsupportedFilePathException;
import com.example.cacex.model.*;
import com.example.cacex.service.plan.rules.PlanOrderingRuleEngine;
import com.example.cacex.service.plan.stratagy.FileParsingStrategy;
import com.example.cacex.service.plan.stratagy.FileParsingStrategyFactory;
import com.finbourne.lusid.model.AborConfigurationRequest;
import com.finbourne.lusid.model.AborRequest;
import com.finbourne.lusid.model.Account;
import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;
import com.finbourne.lusid.model.CreatePortfolioGroupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class PlanService {

    private static final Logger log = LoggerFactory.getLogger(PlanService.class);
    private static final String JSON_EXTENSION = ".json";

    private final FileLocationProperties fileLocationProperties;
    private final FileParsingStrategyFactory strategyFactory;
    private final PlanOrderingRuleEngine planOrderingRuleEngine;
    private final StateFileService stateFileService;

    public PlanService(FileParsingStrategyFactory strategyFactory,
                       PlanOrderingRuleEngine planOrderingRuleEngine,
                       FileLocationProperties fileLocationProperties,
                       StateFileService stateFileService) {
        this.fileLocationProperties = fileLocationProperties;
        this.strategyFactory = strategyFactory;
        this.planOrderingRuleEngine = planOrderingRuleEngine;
        this.stateFileService = stateFileService;
    }

    public MasterPlan buildPlan(List<Path> changedPaths) {
        Map<String, LoadedFile> stateFiles = new HashMap<>();

        MasterPlan plan = new MasterPlan();

        for (Path path : changedPaths) {
            try {
                processChangedFile(path, plan, stateFiles);
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

    private void processChangedFile(Path path, MasterPlan plan, Map<String, LoadedFile> stateFiles) {
        if (!isChangedFilePath(path)) {
            return;
        }

        FileCategory category = deriveCategory(path);
        String scope = deriveScope(path, fileLocationProperties.getChangedFilesDir());
        log.debug("Processing scope {} for category {}", scope, category);
        String key = stateFileService.deriveKeyFromFilename(path);

        if (category == FileCategory.ABOR) {
            Set<String> filesSeen = new HashSet<>();
            filesSeen.add(scopeKey(scope, key));
            processPlanEntries(path, scope, key, plan, FileCategory.ABOR, AborFile.class,
                    this::toAborMap);
            try {
                addDeletesForMissingChanges(
                        DeleteScanSpec.of(fileLocationProperties.stateFilesRoot().resolve(scope),
                                "abor",
                                FileCategory.ABOR,
                                AborFile.class,
                                this::toAborMap),
                        filesSeen,
                        plan,
                        scope);
            } catch (Exception e) {
                log.error("Failed to scan ABOR state files for scope {}: {}", scope, e.getMessage(), e);
            }
            return;
        }

        if (category == FileCategory.ABOR_CONFIGURATION) {
            Set<String> filesSeen = new HashSet<>();
            filesSeen.add(scopeKey(scope, key));
            processPlanEntries(path, scope, key, plan, FileCategory.ABOR_CONFIGURATION, AborConfigurationFile.class,
                    this::toAborConfigurationMap);
            try {
                addDeletesForMissingChanges(
                        DeleteScanSpec.of(fileLocationProperties.stateFilesRoot().resolve(scope),
                                "aborconfigs",
                                FileCategory.ABOR_CONFIGURATION,
                                AborConfigurationFile.class,
                                this::toAborConfigurationMap),
                        filesSeen,
                        plan,
                        scope);
            } catch (Exception e) {
                log.error("Failed to scan ABOR configuration state files for scope {}: {}", scope, e.getMessage(), e);
            }
            return;
        }

        if (category == FileCategory.DERIVED_PORTFOLIO) {
            Set<String> filesSeen = new HashSet<>();
            filesSeen.add(scopeKey(scope, key));
            processPlanEntries(path, scope, key, plan, FileCategory.DERIVED_PORTFOLIO, DerivedPortfolioFile.class,
                    this::toDerivedMap);
            try {
                addDeletesForMissingChanges(
                        DeleteScanSpec.of(fileLocationProperties.stateFilesRoot().resolve(scope),
                                "derivedportfolios",
                                FileCategory.DERIVED_PORTFOLIO,
                                DerivedPortfolioFile.class,
                                this::toDerivedMap),
                        filesSeen,
                        plan,
                        scope);
            } catch (Exception e) {
                log.error("Failed to scan derived portfolio state files for scope {}: {}", scope, e.getMessage(), e);
            }
            return;
        }

        if (category == FileCategory.PORTFOLIO_GROUP) {
            Set<String> filesSeen = new HashSet<>();
            filesSeen.add(scopeKey(scope, key));
            processPlanEntries(path, scope, key, plan, FileCategory.PORTFOLIO_GROUP, PortfolioGroupFile.class,
                    this::toPortfolioGroupMap);
            try {
                addDeletesForMissingChanges(
                        DeleteScanSpec.of(fileLocationProperties.stateFilesRoot(),
                                "portfoliogroups",
                                FileCategory.PORTFOLIO_GROUP,
                                PortfolioGroupFile.class,
                                this::toPortfolioGroupMap),
                        filesSeen,
                        plan,
                        scope);
            } catch (Exception e) {
                log.error("Failed to scan portfolio group state files for scope {}: {}", scope, e.getMessage(), e);
            }
            return;
        }

        if (category == FileCategory.ACCOUNT) {
            Set<String> filesSeen = new HashSet<>();
            filesSeen.add(scopeKey(scope, key));
            System.out.println("Account------>1  "+key);
            processPlanEntries(path, scope, key, plan, FileCategory.ACCOUNT, AccountFile.class,
                    this::toAccountMap);
            try {
                addDeletesForMissingChanges(
                        DeleteScanSpec.of(fileLocationProperties.stateFilesRoot().resolve(scope),
                                "gla",
                                FileCategory.ACCOUNT,
                                AccountFile.class,
                                this::toAccountMap),
                        filesSeen,
                        plan,
                        scope);
            } catch (Exception e) {
                log.error("Failed to scan account state files for scope {}: {}", scope, e.getMessage(), e);
            }
            return;
        }

        String stateKey = mapKey(category, scope, key);

        if (!stateFiles.containsKey(stateKey)) {
            LoadedFile loaded = loadStateFile(scope, category, key);
            if (loaded != null) {
                stateFiles.put(mapKey(loaded), loaded);
            }
        }

        LoadedFile state = stateFiles.get(stateKey);

        if (Files.exists(path)) {
            LoadedFile changed = parsePath(path);
            if (changed == null) {
                return;
            }
            if (state == null) {
                plan.addItem(new PlanItem(Action.NEW, changed.getCategory(), scope, key,
                        changed.getPath().toString(), changed.getPayload()));
            } else if (!Objects.equals(changed.getPayload(), state.getPayload())) {
                plan.addItem(new PlanItem(Action.UPDATE, changed.getCategory(), scope, key,
                        changed.getPath().toString(), changed.getPayload()));
            }
        } else if (state != null) {
            plan.addItem(new PlanItem(Action.DELETE, state.getCategory(), scope, key,
                    state.getPath().toString(), state.getPayload()));
        }
    }

    private LoadedFile loadStateFile(String scope, FileCategory category, String key) {
        Path statePath = stateFileService.resolveStatePath(category, scope, key);
        if (!Files.exists(statePath)) {
            return null;
        }
        return parsePath(statePath);
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

    private String mapKey(LoadedFile file) {
        String root = detectRoot(file.getPath());
        String scope = deriveScope(file.getPath(), root);
        String normalizedKey = normalizeKey(file.getKey(), scope);
        return mapKey(file.getCategory(), scope, normalizedKey);
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

    private String detectRoot(Path path) {
        String lower = path.toString().toLowerCase(Locale.ROOT);
        String changedRoot = changedFilesRootName();
        if (!changedRoot.isEmpty() && lower.contains(changedRoot.toLowerCase(Locale.ROOT))) {
            return changedRoot;
        }
        String stateRoot = stateFilesRootName();
        if (!stateRoot.isEmpty() && lower.contains(stateRoot.toLowerCase(Locale.ROOT))) {
            return stateRoot;
        }
        return "";
    }

    private String scopeKey(String scope, String key) {
        return scope + ":" + key;
    }

    private String normalizeKey(String key, String scope) {
        if (scope != null && !scope.isEmpty()) {
            String suffix = "-" + scope;
            if (key != null && key.endsWith(suffix)) {
                return key.substring(0, key.length() - suffix.length());
            }
        }
        return key;
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

    private <T> void addDeletesForMissingChanges(DeleteScanSpec<T> spec,
                                                 Set<String> filesSeen,
                                                 MasterPlan plan,
                                                 String scope) {
        if (!Files.exists(spec.stateRoot())) {
            return;
        }
        try (Stream<Path> stream = Files.walk(spec.stateRoot())) {
            stream.filter(path -> Files.isRegularFile(path)
                            && path.toString().toLowerCase().contains(spec.folderMarker())
                            && path.toString().toLowerCase().endsWith(JSON_EXTENSION))
                    .forEach(statePath -> {
                        String key = stateFileService.deriveKeyFromFilename(statePath);
                        String scopedKey = scopeKey(scope, key);
                        if (filesSeen.contains(scopedKey)) {
                            return;
                        }
                        LoadedFile state = parsePath(statePath);
                        if (state == null || !spec.payloadType().isInstance(state.getPayload())) {
                            return;
                        }
                        Map<String, ?> stateMap = spec.mapExtractor().apply(spec.payloadType().cast(state.getPayload()));
                        for (Map.Entry<String, ?> entry : stateMap.entrySet()) {
                            plan.addItem(new PlanItem(Action.DELETE, spec.category(), scope, entry.getKey(),
                                    statePath.toString(), entry.getValue()));
                        }
                    });
        } catch (IOException e) {
            throw new PlanProcessingException("Failed to scan " + spec.folderMarker() + " state files", e);
        }
    }

    private <F, R> void processPlanEntries(
            Path changedPath,
            String scope,
            String key,
            MasterPlan plan,
            FileCategory category,
            Class<F> payloadType,
            Function<F, Map<String, R>> mapExtractor) {
        Path statePath = stateFileService.resolveStatePath(category, scope, key);

        F changedFile = parsePayload(changedPath, payloadType);
        F stateFile = parsePayload(statePath, payloadType);

        Map<String, R> changedMap = mapExtractor.apply(changedFile);
        Map<String, R> stateMap = mapExtractor.apply(stateFile);

        for (Map.Entry<String, R> entry : changedMap.entrySet()) {
            String code = entry.getKey();
            R changed = entry.getValue();
            R existing = stateMap.get(code);
            if (existing == null) {
                plan.addItem(new PlanItem(Action.NEW, category, scope, code, changedPath.toString(), changed));
            } else if (!Objects.equals(changed, existing)) {
                plan.addItem(new PlanItem(Action.UPDATE, category, scope, code, changedPath.toString(), changed));
            }
        }

        for (Map.Entry<String, R> entry : stateMap.entrySet()) {
            String code = entry.getKey();
            if (!changedMap.containsKey(code)) {
                plan.addItem(new PlanItem(Action.DELETE, category, scope, code, statePath.toString(), entry.getValue()));
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

    private String stateFilesRootName() {
        return Optional.ofNullable(fileLocationProperties.getStateFilesDir()).orElse("");
    }

    private record DeleteScanSpec<T>(Path stateRoot,
                                     String folderMarker,
                                     FileCategory category,
                                     Class<T> payloadType,
                                     Function<T, Map<String, ?>> mapExtractor) {
        static <T> DeleteScanSpec<T> of(Path stateRoot,
                                        String folderMarker,
                                        FileCategory category,
                                        Class<T> payloadType,
                                        Function<T, Map<String, ?>> mapExtractor) {
            return new DeleteScanSpec<>(stateRoot, folderMarker, category, payloadType, mapExtractor);
        }
    }

}
