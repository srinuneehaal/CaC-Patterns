package com.example.cacex.service.plan.stratagy;

import com.example.cacex.exception.UnsupportedFileCategoryException;
import com.example.cacex.exception.UnsupportedFilePathException;
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
        throw new UnsupportedFilePathException("Unsupported file path: " + path);
    }

    public FileParsingStrategy resolve(FileCategory category) {
        FileParsingStrategy strategy = strategies.get(category);
        if (strategy == null) {
            throw new UnsupportedFileCategoryException("Unsupported file category: " + category);
        }
        return strategy;
    }
}
