package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.AborFile;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

@Component
public class AborFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;
    private final String folderMarker;
    private final String aborConfigMarker;

    /**
     * Creates an ABOR parsing strategy.
     *
     * @param mapper                 JSON mapper
     * @param fileLocationProperties file location properties
     */
    public AborFileParsingStrategy(JsonModelMapper mapper, FileLocationProperties fileLocationProperties) {
        this.mapper = mapper;
        this.folderMarker = fileLocationProperties.getAborDirName().toLowerCase(Locale.ROOT);
        this.aborConfigMarker = fileLocationProperties.getAborConfigurationsDirName().toLowerCase(Locale.ROOT);
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.ABOR;
    }

    /**
     * Determines whether the path belongs to an ABOR file (excluding ABOR configurations).
     *
     * @param path candidate path
     * @return true if supported
     */
    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase(Locale.ROOT);
        return value.contains(folderMarker) && !value.contains(aborConfigMarker) && value.endsWith(".json");
    }

    /**
     * Parses an ABOR JSON file into a {@link LoadedFile}.
     *
     * @param path path to parse
     * @return loaded file wrapper containing the ABOR payload
     * @throws IOException if reading fails
     */
    @Override
    public LoadedFile parse(Path path) throws IOException {
        AborFile file = mapper.read(path, AborFile.class);
        String key = PathUtils.baseName(path);
        return new LoadedFile(getCategory(), key, path, file);
    }
}
