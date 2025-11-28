package com.example.cacex.service.plan.stratagy;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.model.PortfolioGroupFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class PortfolioGroupFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;

    public PortfolioGroupFileParsingStrategy(JsonModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.PORTFOLIO_GROUP;
    }

    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase();
        return value.contains("portfoliogroups") && value.endsWith(".json");
    }

    @Override
    public LoadedFile parse(Path path) throws IOException {
        PortfolioGroupFile file = mapper.read(path, PortfolioGroupFile.class);
        String key = PathUtils.baseName(path);
        return new LoadedFile(getCategory(), key, path, file);
    }
}
