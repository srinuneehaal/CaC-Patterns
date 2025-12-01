package com.example.cacex.service;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.exception.UnsupportedFileCategoryException;
import com.example.cacex.model.*;
import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.util.PathUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finbourne.lusid.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class StateFileService {

    private static final Logger log = LoggerFactory.getLogger(StateFileService.class);
    private static final String JSON_EXTENSION = ".json";

    private final FileLocationProperties fileLocationProperties;
    private final JsonModelMapper jsonModelMapper;
    private final ObjectMapper objectMapper;

    /**
     * Creates a state file service for reading and writing plan state.
     *
     * @param fileLocationProperties filesystem configuration
     * @param jsonModelMapper        mapper for domain payloads
     */
    public StateFileService(FileLocationProperties fileLocationProperties, JsonModelMapper jsonModelMapper) {
        this.fileLocationProperties = fileLocationProperties;
        this.jsonModelMapper = jsonModelMapper;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Applies the given plan item to the state files (create/update/delete).
     *
     * @param item plan item to apply; null items are ignored
     */
    public void applyStateChange(PlanItem item) {
        if (item == null) {
            return;
        }
        if (item.getFileCategory() == null || item.getAction() == null) {
            throw new PlanApplyException("Plan item missing category or action; cannot update state file");
        }
        Path statePath = resolveStatePath(item);
        switch (item.getFileCategory()) {
            case SIDE -> applySide(item, statePath);
            case TRANSACTION -> applyTransaction(item, statePath);
            case CHART_OF_ACCOUNTS -> applyChartOfAccounts(item, statePath);
            case POSTING_RULE -> applyPostingRule(item, statePath);
            case ABOR -> applyAbor(item, statePath);
            case ABOR_CONFIGURATION -> applyAborConfiguration(item, statePath);
            case DERIVED_PORTFOLIO -> applyDerivedPortfolio(item, statePath);
            case PORTFOLIO_GROUP -> applyPortfolioGroup(item, statePath);
            case ACCOUNT -> applyAccount(item, statePath);
            default -> log.warn("State file update not implemented for category {}", item.getFileCategory());
        }
    }

    /**
     * Derives the logical key from a file path, stripping any scope suffix.
     *
     * @param path file path
     * @return derived key
     */
    public String deriveKeyFromFilename(Path path) {
        if (path == null) {
            throw new PlanApplyException("Cannot derive key from null path");
        }
        String base = PathUtils.baseName(path);
        int dash = base.lastIndexOf('-');
        if (dash > 0) {
            return base.substring(0, dash);
        }
        return base;
    }

    /**
     * Resolves the state file path for the given category, scope, and key.
     *
     * @param category file category
     * @param scope    scope value
     * @param key      logical key (may be missing scope suffix)
     * @return path to the state file
     */
    public Path resolveStatePath(FileCategory category, String scope, String key) {
        String folder = folderFor(category);
        boolean hasScope = scope != null && !scope.isBlank();
        String normalizedKey = key == null ? "" : key.trim();
        if (hasScope) {
            String suffix = "-" + scope;
            if (!normalizedKey.toLowerCase(Locale.ROOT).endsWith(suffix.toLowerCase(Locale.ROOT))) {
                normalizedKey = normalizedKey + suffix;
            }
        }
        String filename = normalizedKey + JSON_EXTENSION;
        if (hasScope) {
            return fileLocationProperties.stateFilesRoot()
                    .resolve(scope)
                    .resolve(folder)
                    .resolve(filename);
        }
        return fileLocationProperties.stateFilesRoot()
                .resolve(folder)
                .resolve(filename);
    }

    private void applySide(PlanItem item, Path statePath) {
        if (item.getAction() == Action.DELETE) {
            deleteStateFile(statePath);
            return;
        }
        SideFile payload = castPayload(item, SideFile.class);
        if (payload.getScope() == null) {
            payload.setScope(item.getScope());
        }
        writeStateFile(statePath, payload);
    }

    private void applyTransaction(PlanItem item, Path statePath) {
        if (item.getAction() == Action.DELETE) {
            deleteStateFile(statePath);
            return;
        }
        TransactionFile payload = castPayload(item, TransactionFile.class);
        if (payload.getScope() == null) {
            payload.setScope(item.getScope());
        }
        writeStateFile(statePath, payload);
    }

    private void applyChartOfAccounts(PlanItem item, Path statePath) {
        if (item.getAction() == Action.DELETE) {
            deleteStateFile(statePath);
            return;
        }
        ChartOfAccountsRequest payload = castPayload(item, ChartOfAccountsRequest.class);
        ChartOfAccountsFile file = new ChartOfAccountsFile();
        file.setScope(item.getScope());
        file.setChartOfAccountsCode(stripScopeSuffix(item.getKey(), item.getScope()));
        file.setChartOfAccountsRequest(payload);
        writeStateFile(statePath, file);
    }

    private void applyPostingRule(PlanItem item, Path statePath) {
        if (item.getAction() == Action.DELETE) {
            deleteStateFile(statePath);
            return;
        }
        PostingModuleRequest payload = castPayload(item, PostingModuleRequest.class);
        String trimmedKey = stripScopeSuffix(item.getKey(), item.getScope());
        String[] parts = trimmedKey.split("-");
        String moduleCode = parts.length > 0 ? parts[0] : trimmedKey;
        String coaCode = parts.length > 1 ? parts[1] : moduleCode;

        PostingRulesFile file = new PostingRulesFile();
        file.setScope(item.getScope());
        file.setPostingModuleCode(moduleCode);
        file.setChartOfAccountsCode(coaCode);
        file.setPostingModuleRequest(payload);
        writeStateFile(statePath, file);
    }

    private void applyAbor(PlanItem item, Path statePath) {
        AborFile file = readOrDefault(statePath, AborFile.class, AborFile::new);
        if (file.getScope() == null) {
            file.setScope(item.getScope());
        }
        List<AborRequest> abors = new ArrayList<>(optionalList(file.getAborRequests()));
        if (item.getAction() == Action.DELETE) {
            removeByKey(abors, item.getKey(), AborRequest::getCode);
        } else {
            AborRequest payload = castPayload(item, AborRequest.class);
            upsertByKey(abors, payload, AborRequest::getCode, item.getKey());
        }
        file.setAborRequests(abors);
        persistListFile(statePath, file, abors);
    }

    private void applyAborConfiguration(PlanItem item, Path statePath) {
        AborConfigurationFile file = readOrDefault(statePath, AborConfigurationFile.class, AborConfigurationFile::new);
        if (file.getScope() == null) {
            file.setScope(item.getScope());
        }
        List<AborConfigurationRequest> configs = new ArrayList<>(optionalList(file.getAborConfigurations()));
        if (item.getAction() == Action.DELETE) {
            removeByKey(configs, item.getKey(), AborConfigurationRequest::getCode);
        } else {
            AborConfigurationRequest payload = castPayload(item, AborConfigurationRequest.class);
            upsertByKey(configs, payload, AborConfigurationRequest::getCode, item.getKey());
        }
        file.setAborConfigurations(configs);
        persistListFile(statePath, file, configs);
    }

    private void applyDerivedPortfolio(PlanItem item, Path statePath) {
        DerivedPortfolioFile file = readOrDefault(statePath, DerivedPortfolioFile.class, DerivedPortfolioFile::new);
        if (file.getScope() == null) {
            file.setScope(item.getScope());
        }
        List<CreateDerivedTransactionPortfolioRequest> portfolios =
                new ArrayList<>(optionalList(file.getDerivedPortfolios()));
        if (item.getAction() == Action.DELETE) {
            removeByKey(portfolios, item.getKey(), CreateDerivedTransactionPortfolioRequest::getCode);
        } else {
            CreateDerivedTransactionPortfolioRequest payload =
                    castPayload(item, CreateDerivedTransactionPortfolioRequest.class);
            upsertByKey(portfolios, payload, CreateDerivedTransactionPortfolioRequest::getCode, item.getKey());
        }
        file.setDerivedPortfolios(portfolios);
        persistListFile(statePath, file, portfolios);
    }

    private void applyPortfolioGroup(PlanItem item, Path statePath) {
        PortfolioGroupFile file = readOrDefault(statePath, PortfolioGroupFile.class, PortfolioGroupFile::new);
        if (file.getScope() == null) {
            file.setScope(item.getScope());
        }
        List<CreatePortfolioGroupRequest> groups = new ArrayList<>(optionalList(file.getGroups()));
        if (item.getAction() == Action.DELETE) {
            removeByKey(groups, item.getKey(), CreatePortfolioGroupRequest::getCode);
        } else {
            CreatePortfolioGroupRequest payload = castPayload(item, CreatePortfolioGroupRequest.class);
            upsertByKey(groups, payload, CreatePortfolioGroupRequest::getCode, item.getKey());
        }
        file.setGroups(groups);
        persistListFile(statePath, file, groups);
    }

    private void applyAccount(PlanItem item, Path statePath) {
        AccountFile file = readOrDefault(statePath, AccountFile.class, AccountFile::new);
        if (file.getScope() == null) {
            file.setScope(item.getScope());
        }
        if (file.getChartOfAccountsCode() == null) {
            file.setChartOfAccountsCode(extractChartOfAccounts(item.getKey(), item.getScope()));
        }
        List<Account> accounts = new ArrayList<>(optionalList(file.getAccounts()));
        if (item.getAction() == Action.DELETE) {
            String codeToRemove = extractAccountCode(item, file);
            accounts.removeIf(acc -> Objects.equals(acc.getCode(), codeToRemove));
        } else {
            Account payload = castPayload(item, Account.class);
            accounts.removeIf(acc -> Objects.equals(acc.getCode(), payload.getCode()));
            accounts.add(payload);
        }
        file.setAccounts(accounts);
        persistListFile(statePath, file, accounts);
    }

    private <T> T castPayload(PlanItem item, Class<T> type) {
        Object payload = item.getPayload();
        if (payload == null) {
            throw new PlanApplyException("Missing payload for state update of " + item.getKey());
        }
        if (!type.isInstance(payload)) {
            throw new PlanApplyException("Unexpected payload type for " + item.getKey()
                    + "; expected " + type.getSimpleName() + " but found " + payload.getClass().getSimpleName());
        }
        return type.cast(payload);
    }

    private Path resolveStatePath(PlanItem item) {
        String key = item.getKey();
        if (item.getSourcePath() != null && !item.getSourcePath().isBlank()) {
            key = deriveKeyFromFilename(Path.of(item.getSourcePath()));
        }
        return resolveStatePath(item.getFileCategory(), item.getScope(), key);
    }

    private String folderFor(FileCategory category) {
        return switch (category) {
            case SIDE -> fileLocationProperties.getSidesDirName();
            case TRANSACTION -> fileLocationProperties.getTransactionsDirName();
            case DERIVED_PORTFOLIO -> fileLocationProperties.getDerivedPortfoliosDirName();
            case PORTFOLIO_GROUP -> fileLocationProperties.getPortfolioGroupsDirName();
            case CHART_OF_ACCOUNTS -> fileLocationProperties.getChartOfAccountsDirName();
            case ACCOUNT -> fileLocationProperties.getAccountsDirName();
            case POSTING_RULE -> fileLocationProperties.getPostingRulesDirName();
            case ABOR_CONFIGURATION -> fileLocationProperties.getAborConfigurationsDirName();
            case ABOR -> fileLocationProperties.getAborDirName();
            default -> throw new UnsupportedFileCategoryException("Unsupported category " + category);
        };
    }

    private <T> T readOrDefault(Path path, Class<T> type, Supplier<T> fallback) {
        if (Files.exists(path)) {
            try {
                return jsonModelMapper.read(path, type);
            } catch (IOException e) {
                throw new PlanApplyException("Failed to read state file " + path, e);
            }
        }
        try {
            return fallback.get();
        } catch (Exception e) {
            throw new PlanApplyException("Failed to create default state file holder", e);
        }
    }

    private void writeStateFile(Path path, Object content) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            objectMapper.writeValue(path.toFile(), content);
            log.debug("State file written to {}", path.toAbsolutePath());
        } catch (IOException e) {
            throw new PlanApplyException("Failed to write state file " + path, e);
        }
    }

    private void deleteStateFile(Path path) {
        try {
            if (Files.deleteIfExists(path)) {
                log.debug("Deleted state file {}", path.toAbsolutePath());
            } else {
                log.debug("State file {} not found for deletion", path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new PlanApplyException("Failed to delete state file " + path, e);
        }
    }

    private <T> void upsertByKey(List<T> list, T payload, Function<T, String> keyExtractor, String fallbackKey) {
        String code = keyExtractor.apply(payload);
        if (code == null || code.isBlank()) {
            code = fallbackKey;
        }
        String finalCode = code;
        list.removeIf(existing -> Objects.equals(keyExtractor.apply(existing), finalCode));
        list.add(payload);
    }

    private <T> void removeByKey(List<T> list, String key, Function<T, String> keyExtractor) {
        list.removeIf(existing -> Objects.equals(keyExtractor.apply(existing), key));
    }

    private void persistListFile(Path path, Object file, List<?> contents) {
        if (contents == null || contents.isEmpty()) {
            deleteStateFile(path);
        } else {
            writeStateFile(path, file);
        }
    }

    private <T> List<T> optionalList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private String stripScopeSuffix(String key, String scope) {
        if (key == null) {
            return "";
        }
        if (scope != null && !scope.isBlank()) {
            String suffix = "-" + scope;
            if (key.endsWith(suffix)) {
                return key.substring(0, key.length() - suffix.length());
            }
        }
        return key;
    }

    private String extractChartOfAccounts(String key, String scope) {
        String trimmed = stripScopeSuffix(key, scope);
        int dash = trimmed.indexOf('-');
        if (dash > 0) {
            return trimmed.substring(0, dash);
        }
        return trimmed;
    }

    private String extractAccountCode(PlanItem item, AccountFile file) {
        String chartCode = file.getChartOfAccountsCode();
        String key = stripScopeSuffix(item.getKey(), item.getScope());
        if (chartCode != null && key.startsWith(chartCode + "-")) {
            return key.substring(chartCode.length() + 1);
        }
        int dash = key.indexOf('-');
        if (dash >= 0 && dash + 1 < key.length()) {
            return key.substring(dash + 1);
        }
        Account payload = item.getPayload() instanceof Account ? (Account) item.getPayload() : null;
        if (payload != null) {
            return payload.getCode();
        }
        return key;
    }

}
