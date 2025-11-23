package com.example.cacex.strategies;

import com.example.cacex.model.DerivedPortfolioFile;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class DerivedPortfolioFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;

    public DerivedPortfolioFileParsingStrategy(JsonModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.DERIVED_PORTFOLIO;
    }

    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase();
        return value.contains("derivedportfolios") && value.endsWith(".json");
    }

    @Override
    public LoadedFile parse(Path path) throws IOException {
        DerivedPortfolioFile file = mapper.read(path, DerivedPortfolioFile.class);
        String key = PathUtils.baseName(path);
        return new LoadedFile(getCategory(), key, path, file);
    }
}
