package com.example.cacex.service;

import com.example.cacex.model.*;
import com.example.cacex.strategies.FileParsingStrategy;
import com.example.cacex.strategies.FileParsingStrategyFactory;
import com.example.cacex.util.PathUtils;
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

@Service
public class PlanService {

    private static final Logger log = LoggerFactory.getLogger(PlanService.class);

    private final FileParsingStrategyFactory strategyFactory;

    public PlanService(FileParsingStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public MasterPlan buildPlan(List<Path> changedPaths) {
        Map<String, LoadedFile> stateFiles = new HashMap<>();
        Set<String> derivedFilesSeen = new HashSet<>();
        Set<String> portfolioGroupFilesSeen = new HashSet<>();

        MasterPlan plan = new MasterPlan();

        for (Path path : changedPaths) {
            if (!isChangedFilePath(path)) {
                continue;
            }

            FileCategory category = deriveCategory(path);
            String scope = deriveScope(path, "changedfiles");
            System.out.println("scope: " + scope + ", category:" + category);
            String key = deriveKeyFromFilename(path);

            if (category == FileCategory.DERIVED_PORTFOLIO) {
                derivedFilesSeen.add(scopeKey(scope, key));
                processDerivedPortfolio(path, scope, key, plan);
                //addDerivedDeletesForMissingChanges(derivedFilesSeen, plan,scope);
                addDeletesForMissingChanges(
                        Path.of("statefiles", scope),
                        derivedFilesSeen,
                        plan,
                        scope,
                        "derivedportfolios",
                        FileCategory.DERIVED_PORTFOLIO,
                        DerivedPortfolioFile.class,
                        this::toDerivedMap);
                continue;
            }

            if (category == FileCategory.PORTFOLIO_GROUP) {
                portfolioGroupFilesSeen.add(scopeKey(scope, key));
                processPortfolioGroup(path, scope, key, plan);
                addDeletesForMissingChanges(
                        Path.of("statefiles"),
                        portfolioGroupFilesSeen,
                        plan,
                        scope,
                        "portfoliogroups",
                        FileCategory.PORTFOLIO_GROUP,
                        PortfolioGroupFile.class,
                        this::toPortfolioGroupMap);
                continue;
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
                    continue;
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


        return plan;
    }

    private LoadedFile loadStateFile(String scope, FileCategory category, String key) {
        Path statePath = resolveStatePath(category, scope, key);
        if (!Files.exists(statePath)) {
            return null;
        }
        return parsePath(statePath);
    }

    private void processDerivedPortfolio(Path changedPath, String scope, String key, MasterPlan plan) {
        Path statePath = resolveStatePath(FileCategory.DERIVED_PORTFOLIO, scope, key);

        DerivedPortfolioFile changedFile = null;
        DerivedPortfolioFile stateFile = null;
        if (Files.exists(changedPath)) {
            LoadedFile changed = parsePath(changedPath);
            if (changed != null && changed.getPayload() instanceof DerivedPortfolioFile) {
                changedFile = (DerivedPortfolioFile) changed.getPayload();
            }
        }

        if (Files.exists(statePath)) {
            LoadedFile state = parsePath(statePath);
            if (state != null && state.getPayload() instanceof DerivedPortfolioFile) {
                stateFile = (DerivedPortfolioFile) state.getPayload();
            }
        }

        Map<String, CreateDerivedTransactionPortfolioRequest> changedMap = toDerivedMap(changedFile);
        Map<String, CreateDerivedTransactionPortfolioRequest> stateMap = toDerivedMap(stateFile);

        for (Map.Entry<String, CreateDerivedTransactionPortfolioRequest> entry : changedMap.entrySet()) {
            String code = entry.getKey();
            CreateDerivedTransactionPortfolioRequest changed = entry.getValue();
            CreateDerivedTransactionPortfolioRequest existing = stateMap.get(code);
            if (existing == null) {
                plan.addItem(new PlanItem(Action.NEW, FileCategory.DERIVED_PORTFOLIO, scope, code,
                        changedPath.toString(), changed));
            } else if (!Objects.equals(changed, existing)) {
                plan.addItem(new PlanItem(Action.UPDATE, FileCategory.DERIVED_PORTFOLIO, scope, code,
                        changedPath.toString(), changed));
            }
        }

        for (Map.Entry<String, CreateDerivedTransactionPortfolioRequest> entry : stateMap.entrySet()) {
            String code = entry.getKey();
            if (!changedMap.containsKey(code)) {
                plan.addItem(new PlanItem(Action.DELETE, FileCategory.DERIVED_PORTFOLIO, scope, code,
                        statePath.toString(), entry.getValue()));
            }
        }
    }

    private void processPortfolioGroup(Path changedPath, String scope, String key, MasterPlan plan) {
        Path statePath = resolveStatePath(FileCategory.PORTFOLIO_GROUP, scope, key);

        PortfolioGroupFile changedFile = null;
        PortfolioGroupFile stateFile = null;
        if (Files.exists(changedPath)) {
            LoadedFile changed = parsePath(changedPath);
            if (changed != null && changed.getPayload() instanceof PortfolioGroupFile) {
                changedFile = (PortfolioGroupFile) changed.getPayload();
            }
        }

        if (Files.exists(statePath)) {
            LoadedFile state = parsePath(statePath);
            if (state != null && state.getPayload() instanceof PortfolioGroupFile) {
                stateFile = (PortfolioGroupFile) state.getPayload();
            }
        }

        Map<String, CreatePortfolioGroupRequest> changedMap = toPortfolioGroupMap(changedFile);
        Map<String, CreatePortfolioGroupRequest> stateMap = toPortfolioGroupMap(stateFile);

        for (Map.Entry<String, CreatePortfolioGroupRequest> entry : changedMap.entrySet()) {
            String code = entry.getKey();
            CreatePortfolioGroupRequest changed = entry.getValue();
            CreatePortfolioGroupRequest existing = stateMap.get(code);
            if (existing == null) {
                plan.addItem(new PlanItem(Action.NEW, FileCategory.PORTFOLIO_GROUP, scope, code,
                        changedPath.toString(), changed));
            } else if (!Objects.equals(changed, existing)) {
                plan.addItem(new PlanItem(Action.UPDATE, FileCategory.PORTFOLIO_GROUP, scope, code,
                        changedPath.toString(), changed));
            }
        }

        for (Map.Entry<String, CreatePortfolioGroupRequest> entry : stateMap.entrySet()) {
            String code = entry.getKey();
            if (!changedMap.containsKey(code)) {
                plan.addItem(new PlanItem(Action.DELETE, FileCategory.PORTFOLIO_GROUP, scope, code,
                        statePath.toString(), entry.getValue()));
            }
        }
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
        String normalized = path.toString().toLowerCase();
        if (!normalized.contains("changedfiles")) {
            log.debug("Ignoring path outside changedfiles: {}", path);
            return false;
        }
        return normalized.endsWith(".json");
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
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to determine category for " + path, e);
        }
    }

    private String deriveKeyFromFilename(Path path) {
        String base = PathUtils.baseName(path);
        int dash = base.lastIndexOf('-');
        if (dash > 0) {
            return base.substring(0, dash);
        }
        return base;
    }

    private Path resolveStatePath(FileCategory category, String scope, String key) {
        String folder;
        switch (category) {
            case SIDE:
                folder = "sides";
                break;
            case TRANSACTION:
                folder = "transactions";
                break;
            case DERIVED_PORTFOLIO:
                folder = "derivedportfolios";
                break;
            case PORTFOLIO_GROUP:
                folder = "portfoliogroups";
                break;
            default:
                throw new IllegalArgumentException("Unsupported category " + category);
        }
        boolean hasScope = scope != null && !scope.isEmpty();
        String filename = hasScope ? key + "-" + scope + ".json" : key + ".json";
        if (hasScope) {
            return Path.of("statefiles", scope, folder, filename);
        }
        return Path.of("statefiles", folder, filename);
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
        String lower = path.toString().toLowerCase();
        if (lower.contains("changedfiles")) {
            return "changedfiles";
        }
        if (lower.contains("statefiles")) {
            return "statefiles";
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

    private <T> void addDeletesForMissingChanges(
            Path stateRoot,
            Set<String> filesSeen,
            MasterPlan plan,
            String scope,
            String folderMarker,
            FileCategory category,
            Class<T> payloadType,
            Function<T, Map<String, ?>> mapExtractor) {
        if (!Files.exists(stateRoot)) {
            return;
        }
        try (java.util.stream.Stream<Path> stream = Files.walk(stateRoot)) {
            stream.filter(path -> Files.isRegularFile(path)
                            && path.toString().toLowerCase().contains(folderMarker)
                            && path.toString().toLowerCase().endsWith(".json"))
                    .forEach(statePath -> {
                        String key = deriveKeyFromFilename(statePath);
                        String scopedKey = scopeKey(scope, key);
                        if (filesSeen.contains(scopedKey)) {
                            return;
                        }
                        LoadedFile state = parsePath(statePath);
                        if (state == null || !payloadType.isInstance(state.getPayload())) {
                            return;
                        }
                        Map<String, ?> stateMap = mapExtractor.apply(payloadType.cast(state.getPayload()));
                        for (Map.Entry<String, ?> entry : stateMap.entrySet()) {
                            plan.addItem(new PlanItem(Action.DELETE, category, scope, entry.getKey(),
                                    statePath.toString(), entry.getValue()));
                        }
                    });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to scan " + folderMarker + " state files", e);
        }
    }


}
