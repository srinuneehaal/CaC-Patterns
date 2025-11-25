package com.example.cacex.service.parse.stratagy;

import com.example.cacex.model.FileCategory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class FileParsingStrategyFactory {

    private final Map<FileCategory, FileParsingStrategy> strategies = new EnumMap<>(FileCategory.class);

    public FileParsingStrategyFactory(List<FileParsingStrategy> discoveredStrategies) {

        for (FileParsingStrategy strategy : discoveredStrategies) {
            strategies.put(strategy.getCategory(), strategy);
        }
    }

    public FileParsingStrategy resolve(Path path) {
        for (FileParsingStrategy strategy : strategies.values()) {
            if (strategy.supports(path)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Unsupported file path: " + path);
    }

    public FileParsingStrategy resolve(FileCategory category) {
        FileParsingStrategy strategy = strategies.get(category);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported file category: " + category);
        }
        return strategy;
    }
}
