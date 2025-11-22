package com.example.cacex.service;

import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.model.MasterPlan;
import com.example.cacex.model.PlanItem;
import com.example.cacex.util.PathUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlanService {

    private static final Logger log = LoggerFactory.getLogger(PlanService.class);

    private final FileParsingStrategyFactory strategyFactory;

    public PlanService(FileParsingStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public MasterPlan buildPlan(List<Path> changedPaths) {
        Map<String, LoadedFile> stateFiles = new HashMap<>();

        MasterPlan plan = new MasterPlan();

        for (Path path : changedPaths) {
            if (!isChangedFilePath(path)) {
                continue;
            }
            FileCategory category = deriveCategory(path);

            String key = deriveKeyFromPath(path);

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
                    plan.addItem(new PlanItem(Action.NEW, changed.getCategory(), changed.getKey(),
                            changed.getPath().toString(), changed.getPayload()));
                } else if (!Objects.equals(changed.getPayload(), state.getPayload())) {
                    plan.addItem(new PlanItem(Action.UPDATE, changed.getCategory(), changed.getKey(),
                            changed.getPath().toString(), changed.getPayload()));
                }
            } else if (state != null) {
                plan.addItem(new PlanItem(Action.DELETE, state.getCategory(), state.getKey(),
                        state.getPath().toString(), state.getPayload()));
            }
        }

        return plan;
    }
    
    private Map<String, LoadedFile> loadStateFiles(String key) {
        Map<String, LoadedFile> stateFiles = new HashMap<>();

        for (FileCategory category : FileCategory.values()) {
            Path statePath = resolveStatePath(category, key);
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

    private Path resolveStatePath(FileCategory category, String key) {
        String folder = category == FileCategory.SIDE ? "sides" : "transactions";
        return Path.of("statefiles", folder, key + ".json");
    }
}
