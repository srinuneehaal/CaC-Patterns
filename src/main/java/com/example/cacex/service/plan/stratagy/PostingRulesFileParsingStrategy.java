package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.model.PostingRulesFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

@Component
public class PostingRulesFileParsingStrategy implements FileParsingStrategy {

    private final JsonModelMapper mapper;
    private final String folderMarker;

    /**
     * Creates a posting rules parsing strategy.
     *
     * @param mapper                 JSON mapper
     * @param fileLocationProperties file location properties
     */
    public PostingRulesFileParsingStrategy(JsonModelMapper mapper, FileLocationProperties fileLocationProperties) {
        this.mapper = mapper;
        this.folderMarker = fileLocationProperties.getPostingRulesDirName().toLowerCase(Locale.ROOT);
    }

    @Override
    public FileCategory getCategory() {
        return FileCategory.POSTING_RULE;
    }

    /**
     * Determines whether the path represents a posting rules file.
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
     * Parses a posting rules JSON file into a {@link LoadedFile}.
     *
     * @param path path to the JSON file
     * @return loaded file wrapper
     * @throws IOException when reading fails
     */
    @Override
    public LoadedFile parse(Path path) throws IOException {
        PostingRulesFile file = mapper.read(path, PostingRulesFile.class);
        String scope = file.getScope() == null ? "" : file.getScope().strip();
        String key = buildKey(path, scope, file.getPostingModuleCode(), file.getChartOfAccountsCode(), file);
        return new LoadedFile(getCategory(), key, path, file.getPostingModuleRequest());
    }

    private String buildKey(Path path,
                            String scope,
                            String postingModuleCode,
                            String chartOfAccountsCode,
                            PostingRulesFile file) {
        String module = StringUtils.hasText(postingModuleCode)
                ? postingModuleCode.trim()
                : (file.getPostingModuleRequest() != null && StringUtils.hasText(file.getPostingModuleRequest().getCode())
                ? file.getPostingModuleRequest().getCode().trim()
                : null);
        String coa = StringUtils.hasText(chartOfAccountsCode) ? chartOfAccountsCode.trim() : null;

        if (StringUtils.hasText(module) && StringUtils.hasText(coa) && StringUtils.hasText(scope)) {
            return module + "-" + coa + "-" + scope;
        }
        if (StringUtils.hasText(module) && StringUtils.hasText(coa)) {
            return module + "-" + coa;
        }
        if (StringUtils.hasText(module) && StringUtils.hasText(scope)) {
            return module + "-" + scope;
        }
        if (StringUtils.hasText(module)) {
            return module;
        }
        return PathUtils.baseName(path);
    }
}
