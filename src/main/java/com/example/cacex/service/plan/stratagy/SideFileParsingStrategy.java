package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.model.SideFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

@Component
public class SideFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;
    private final String folderMarker;

    /**
     * Creates a side file parsing strategy.
     *
     * @param mapper                 JSON mapper
     * @param fileLocationProperties file location properties
     */
    public SideFileParsingStrategy(JsonModelMapper mapper, FileLocationProperties fileLocationProperties) {
        this.mapper = mapper;
        this.folderMarker = fileLocationProperties.getSidesDirName().toLowerCase(Locale.ROOT);
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.SIDE;
    }

    /**
     * Determines whether the given path belongs to a side file.
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
     * Parses a side JSON file into a {@link LoadedFile}.
     *
     * @param path path to parse
     * @return loaded file wrapper containing the side payload
     * @throws IOException if reading fails
     */
    @Override
    public LoadedFile parse(Path path) throws IOException {
        SideFile sideFile = mapper.read(path, SideFile.class);
        String key = StringUtils.hasText(sideFile.getSide()) ? (sideFile.getSide()+"-"+sideFile.getScope()) : PathUtils.baseName(path);
        return new LoadedFile(getCategory(), key, path, sideFile);
    }
}
