package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.AborConfigurationFile;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

@Component
public class AborConfigurationFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;
    private final String folderMarker;

    public AborConfigurationFileParsingStrategy(JsonModelMapper mapper, FileLocationProperties fileLocationProperties) {
        this.mapper = mapper;
        this.folderMarker = fileLocationProperties.getAborConfigurationsDirName().toLowerCase(Locale.ROOT);
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.ABOR_CONFIGURATION;
    }

    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase(Locale.ROOT);
        return value.contains(folderMarker) && value.endsWith(".json");
    }

    @Override
    public LoadedFile parse(Path path) throws IOException {
        AborConfigurationFile file = mapper.read(path, AborConfigurationFile.class);
        String key = PathUtils.baseName(path);
        return new LoadedFile(getCategory(), key, path, file);
    }
}
