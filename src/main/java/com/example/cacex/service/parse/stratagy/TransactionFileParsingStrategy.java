package com.example.cacex.service.parse.stratagy;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.model.TransactionFile;
import com.example.cacex.service.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class TransactionFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;

    public TransactionFileParsingStrategy(JsonModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.TRANSACTION;
    }

    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase();
        return value.contains("transactions") && value.endsWith(".json");
    }

    @Override
    public LoadedFile parse(Path path) throws IOException {
        TransactionFile transactionFile = mapper.read(path, TransactionFile.class);
        String key = StringUtils.hasText(transactionFile.getType()) ?
                (transactionFile.getTransactionClass() + "-" + transactionFile.getSource() + "-" + transactionFile.getScope()) :
                PathUtils.baseName(path);
        return new LoadedFile(getCategory(), key, path, transactionFile);
    }
}
