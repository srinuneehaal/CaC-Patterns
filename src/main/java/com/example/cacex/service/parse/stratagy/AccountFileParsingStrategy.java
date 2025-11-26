package com.example.cacex.service.parse.stratagy;

import com.example.cacex.model.AccountFile;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class AccountFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;

    public AccountFileParsingStrategy(JsonModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.ACCOUNT;
    }

    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase();
        if (!value.endsWith(".json")) {
            return false;
        }
        for (Path part : path.normalize()) {
            if ("gla".equalsIgnoreCase(part.toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LoadedFile parse(Path path) throws IOException {
        AccountFile file = mapper.read(path, AccountFile.class);
        String key = PathUtils.baseName(path);
        return new LoadedFile(getCategory(), key, path, file);
    }
}
