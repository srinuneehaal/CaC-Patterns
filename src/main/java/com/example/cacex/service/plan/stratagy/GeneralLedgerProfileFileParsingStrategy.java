package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.GeneralLedgerProfileFile;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Parsing strategy for general ledger profile files.
 */
@Component
public class GeneralLedgerProfileFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;
    private final String folderMarker;

    /**
     * Creates a general ledger profile parsing strategy.
     *
     * @param mapper                 JSON mapper
     * @param fileLocationProperties file location configuration
     */
    public GeneralLedgerProfileFileParsingStrategy(JsonModelMapper mapper,
                                                   FileLocationProperties fileLocationProperties) {
        this.mapper = mapper;
        this.folderMarker = fileLocationProperties.getGeneralLedgerProfilesDirName().toLowerCase(Locale.ROOT);
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.GENERAL_LEDGER_PROFILE;
    }

    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase(Locale.ROOT);
        return value.contains(folderMarker) && value.endsWith(".json");
    }

    @Override
    public LoadedFile parse(Path path) throws IOException {
        GeneralLedgerProfileFile file = mapper.read(path, GeneralLedgerProfileFile.class);
        String scope = file.getScope() == null ? "" : file.getScope().strip();
        String key = buildKey(path, scope,
                file.getGeneralLedgerProfileCode(),
                file.getChartOfAccountsCode());
        return new LoadedFile(getCategory(), key, path, file.getGeneralLedgerProfileRequest());
    }

    private String buildKey(Path path,
                            String scope,
                            String profileCode,
                            String chartOfAccountsCode) {
        String profile = StringUtils.hasText(profileCode) ? profileCode.trim() : null;
        String chart = StringUtils.hasText(chartOfAccountsCode) ? chartOfAccountsCode.trim() : null;

        if (profile != null && chart != null && StringUtils.hasText(scope)) {
            return profile + "-" + chart + "-" + scope;
        }
        if (profile != null && chart != null) {
            return profile + "-" + chart;
        }
        if (profile != null && StringUtils.hasText(scope)) {
            return profile + "-" + scope;
        }
        if (profile != null) {
            return profile;
        }
        return PathUtils.baseName(path);
    }
}
