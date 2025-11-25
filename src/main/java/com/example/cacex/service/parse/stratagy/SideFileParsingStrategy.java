package com.example.cacex.service.parse.stratagy;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.model.SideFile;
import com.example.cacex.service.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class SideFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;

    public SideFileParsingStrategy(JsonModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.SIDE;
    }

    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase();
        return value.contains("sides") && value.endsWith(".json");
    }

    @Override
    public LoadedFile parse(Path path) throws IOException {
        SideFile sideFile = mapper.read(path, SideFile.class);
        String key = StringUtils.hasText(sideFile.getSide()) ? (sideFile.getSide()+"-"+sideFile.getScope()) : PathUtils.baseName(path);
        return new LoadedFile(getCategory(), key, path, sideFile);
    }
}
