package com.example.cacex.service;

import com.example.cacex.model.*;
import com.example.cacex.strategies.FileParsingStrategy;
import com.example.cacex.strategies.FileParsingStrategyFactory;
import com.example.cacex.util.PathUtils;
import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

        MasterPlan plan = new MasterPlan();

        for (Path path : changedPaths) {
            if (!isChangedFilePath(path)) {
                continue;
            }

            String scope=path.toString().substring(path.toString().lastIndexOf("-")+1,path.toString().length());
            FileCategory category = deriveCategory(path);
            String key = deriveKeyFromPath(path);
            if (category == FileCategory.DERIVED_PORTFOLIO) {
                derivedFilesSeen.add(key);
                processDerivedPortfolio(path, key, plan);
                // Handle deletes for derived portfolios where no changed file was supplied.
                addDerivedDeletesForMissingChanges(derivedFilesSeen, plan,scope);
                continue;
            }

            // Load state for this key lazily to avoid scanning the whole statefiles tree.

            if (!stateFiles.containsKey(mapKey(category, key))) {
                stateFiles.putAll(loadStateFiles(key));
            }

            LoadedFile state = stateFiles.get(mapKey(category, deriveKeyFromPath(path)));

            if (Files.exists(path)) {
                LoadedFile changed = parsePath(path);
                if (changed == null) {
                    continue;
                }
                if (state == null) {
                    plan.addItem(new PlanItem(Action.NEW, changed.getCategory(),scope, changed.getKey(),
                            changed.getPath().toString(), changed.getPayload()));
                } else if (!Objects.equals(changed.getPayload(), state.getPayload())) {
                    plan.addItem(new PlanItem(Action.UPDATE, changed.getCategory(),scope, changed.getKey(),
                            changed.getPath().toString(), changed.getPayload()));
                }
            } else if (state != null) {
                plan.addItem(new PlanItem(Action.DELETE, state.getCategory(),scope, state.getKey(),
                        state.getPath().toString(), state.getPayload()));
            }
        }


        return plan;
    }
    
    private Map<String, LoadedFile> loadStateFiles(String key) {
        Map<String, LoadedFile> stateFiles = new HashMap<>();
        String scope=key.substring(key.lastIndexOf("-")+1,key.length());

        for (FileCategory category : FileCategory.values()) {
            if (category == FileCategory.DERIVED_PORTFOLIO) {
                continue;
            }
            Path statePath = resolveStatePath(category, key,scope);
            if (!Files.exists(statePath)) {
                continue;
            }
            LoadedFile loaded = parsePath(statePath);
            if (loaded != null) {
                stateFiles.put(mapKey(loaded), loaded);
            }
        }

        return stateFiles;
    }

    private void processDerivedPortfolio(Path changedPath, String key, MasterPlan plan) {

        String scope=key.substring(key.lastIndexOf("-")+1,key.length());

        Path statePath = resolveStatePath(FileCategory.DERIVED_PORTFOLIO, key, scope);

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
                plan.addItem(new PlanItem(Action.NEW, FileCategory.DERIVED_PORTFOLIO, scope,code,
                        changedPath.toString(), changed));
            } else if (!Objects.equals(changed, existing)) {
                plan.addItem(new PlanItem(Action.UPDATE, FileCategory.DERIVED_PORTFOLIO,scope, code,
                        changedPath.toString(), changed));
            }
        }

        for (Map.Entry<String, CreateDerivedTransactionPortfolioRequest> entry : stateMap.entrySet()) {
            String code = entry.getKey();
            if (!changedMap.containsKey(code)) {
                plan.addItem(new PlanItem(Action.DELETE, FileCategory.DERIVED_PORTFOLIO, scope,code,
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
        return mapKey(file.getCategory(), file.getKey());
    }

    private String mapKey(FileCategory category, String key) {
       return category.name() + ":" + key;
    }

    private FileCategory deriveCategory(Path path) {
        try {
            return strategyFactory.resolve(path).getCategory();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to determine category for " + path, e);
        }
    }

    private String deriveKeyFromPath(Path path) {
        return PathUtils.baseName(path);
    }

    private Path resolveStatePath(FileCategory category, String key, String scope) {
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
            default:
                throw new IllegalArgumentException("Unsupported category " + category);
        }
        return Path.of("statefiles", scope,folder, key + ".json");
    }

    private Map<String, CreateDerivedTransactionPortfolioRequest> toDerivedMap(
            com.example.cacex.model.DerivedPortfolioFile file) {
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

    private void addDerivedDeletesForMissingChanges(Set<String> derivedFilesSeen, MasterPlan plan, String scope) {
        Path dir = Path.of("statefiles", "derivedportfolios");
        if (!Files.exists(dir)) {
            return;
        }
        try {
            Files.list(dir)
                    .filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".json"))
                    .forEach(statePath -> {
                        String key = deriveKeyFromPath(statePath);
                        if (derivedFilesSeen.contains(key)) {
                            return;
                        }
                        LoadedFile state = parsePath(statePath);
                        if (state == null || !(state.getPayload() instanceof com.example.cacex.model.DerivedPortfolioFile)) {
                            return;
                        }
                        Map<String, CreateDerivedTransactionPortfolioRequest> stateMap =
                                toDerivedMap((com.example.cacex.model.DerivedPortfolioFile) state.getPayload());
                        for (Map.Entry<String, CreateDerivedTransactionPortfolioRequest> entry : stateMap.entrySet()) {
                            plan.addItem(new PlanItem(Action.DELETE, FileCategory.DERIVED_PORTFOLIO, scope,entry.getKey(),
                                    statePath.toString(), entry.getValue()));
                        }
                    });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to scan derived portfolio state files", e);
        }
    }
}
