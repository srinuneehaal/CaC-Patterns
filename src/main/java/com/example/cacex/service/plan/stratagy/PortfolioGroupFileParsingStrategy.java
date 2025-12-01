package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.model.PortfolioGroupFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

@Component
public class PortfolioGroupFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;
    private final String folderMarker;

    /**
     * Creates a portfolio group parsing strategy.
     *
     * @param mapper                 JSON mapper
     * @param fileLocationProperties file location properties
     */
    public PortfolioGroupFileParsingStrategy(JsonModelMapper mapper, FileLocationProperties fileLocationProperties) {
        this.mapper = mapper;
        this.folderMarker = fileLocationProperties.getPortfolioGroupsDirName().toLowerCase(Locale.ROOT);
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.PORTFOLIO_GROUP;
    }

    /**
     * Determines whether the given path belongs to a portfolio group file.
     *
     * @param path candidate path
     * @return true if supported
     */
    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase(Locale.ROOT);
        return value.contains(folderMarker) && value.endsWith(".json");
    }

    /**
     * Parses a portfolio group JSON file into a {@link LoadedFile}.
     *
     * @param path path to parse
     * @return loaded file wrapper
     * @throws IOException if reading fails
     */
    @Override
    public LoadedFile parse(Path path) throws IOException {
        PortfolioGroupFile file = mapper.read(path, PortfolioGroupFile.class);
        String key = PathUtils.baseName(path);
        return new LoadedFile(getCategory(), key, path, file);
    }
}
