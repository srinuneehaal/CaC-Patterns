package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.AccountFile;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

@Component
public class AccountFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;
    private final String folderMarker;

    public AccountFileParsingStrategy(JsonModelMapper mapper, FileLocationProperties fileLocationProperties) {
        this.mapper = mapper;
        this.folderMarker = fileLocationProperties.getAccountsDirName().toLowerCase(Locale.ROOT);
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.ACCOUNT;
    }

    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase(Locale.ROOT);
        if (!value.endsWith(".json")) {
            return false;
        }
        for (Path part : path.normalize()) {
            if (folderMarker.equalsIgnoreCase(part.toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LoadedFile parse(Path path) throws IOException {
        AccountFile file = mapper.read(path, AccountFile.class);
        String key = PathUtils.baseName(path);
        System.out.println("Account key-->"+key);
        return new LoadedFile(getCategory(), key, path, file);
    }
}
