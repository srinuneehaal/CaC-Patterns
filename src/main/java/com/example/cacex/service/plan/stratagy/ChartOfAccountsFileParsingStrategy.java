package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.ChartOfAccountsFile;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

@Component
public class ChartOfAccountsFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;
    private final String folderMarker;

    /**
     * Creates a chart of accounts parsing strategy.
     *
     * @param mapper                 JSON mapper
     * @param fileLocationProperties file location properties
     */
    public ChartOfAccountsFileParsingStrategy(JsonModelMapper mapper, FileLocationProperties fileLocationProperties) {
        this.mapper = mapper;
        this.folderMarker = fileLocationProperties.getChartOfAccountsDirName().toLowerCase(Locale.ROOT);
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.CHART_OF_ACCOUNTS;
    }

    /**
     * Determines whether the path belongs to a chart of accounts JSON file.
     *
     * @param path candidate path
     * @return true if supported
     */
    @Override
    public boolean supports(Path path) {
        String value = path.toString().toLowerCase(Locale.ROOT);
        boolean inCoaFolder = value.contains("\\" + folderMarker + "\\")
                || value.contains("/" + folderMarker + "/")
                || value.contains("chartofaccounts");
        return inCoaFolder && value.endsWith(".json");
    }

    /**
     * Parses a chart of accounts file into a {@link LoadedFile}.
     *
     * @param path path to parse
     * @return loaded file containing request payload
     * @throws IOException if reading fails
     */
    @Override
    public LoadedFile parse(Path path) throws IOException {
        ChartOfAccountsFile file = mapper.read(path, ChartOfAccountsFile.class);
        String scope = file.getScope() == null ? null : file.getScope().strip();
        String code = resolveCode(file);
        String key = buildKey(path, scope, code);
        return new LoadedFile(getCategory(), key, path, file.getChartOfAccountsRequest());
    }

    private String resolveCode(ChartOfAccountsFile file) {
        if (StringUtils.hasText(file.getChartOfAccountsCode())) {
            return file.getChartOfAccountsCode();
        }
        if (file.getChartOfAccountsRequest() != null && StringUtils.hasText(file.getChartOfAccountsRequest().getCode())) {
            return file.getChartOfAccountsRequest().getCode();
        }
        return null;
    }

    private String buildKey(Path path, String scope, String code) {
        if (StringUtils.hasText(code) && StringUtils.hasText(scope)) {
            return code + "-" + scope;
        }
        if (StringUtils.hasText(code)) {
            return code;
        }
        return PathUtils.baseName(path);
    }
}
