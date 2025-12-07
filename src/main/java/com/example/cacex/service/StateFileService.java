package com.example.cacex.service;

import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.model.*;
import com.finbourne.lusid.model.*;
import com.example.cacex.repository.StateRepository;
import com.example.cacex.service.plan.JsonModelMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.cacex.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class StateFileService {

    private static final Logger log = LoggerFactory.getLogger(StateFileService.class);

    private final StateRepository stateRepository;
    private final JsonModelMapper jsonModelMapper;
    private final ObjectMapper objectMapper;

    public StateFileService(StateRepository stateRepository,
                            JsonModelMapper jsonModelMapper,
                            ObjectMapper objectMapper) {
        this.stateRepository = stateRepository;
        this.jsonModelMapper = jsonModelMapper;
        this.objectMapper = objectMapper;
    }

    public void applyStateChange(PlanItem item) {
        if (item == null) {
            return;
        }
        if (item.getFileCategory() == null || item.getAction() == null) {
            throw new PlanApplyException("Plan item missing category or action; cannot update state file");
        }
        FileCategory category = item.getFileCategory();
        String scope = item.getScope();
        String key = resolveStorageKey(item);
        switch (category) {
            case SIDE -> applySide(item, scope, key);
            case TRANSACTION -> applyTransaction(item, scope, key);
            case CHART_OF_ACCOUNTS -> applyChartOfAccounts(item, scope, key);
            case POSTING_RULE -> applyPostingRule(item, scope, key);
            case GENERAL_LEDGER_PROFILE -> applyGeneralLedgerProfile(item, scope, key);
            case ABOR -> applyAbor(item, scope, key);
            case ABOR_CONFIGURATION -> applyAborConfiguration(item, scope, key);
            case DERIVED_PORTFOLIO -> applyDerivedPortfolio(item, scope, key);
            case PORTFOLIO_GROUP -> applyPortfolioGroup(item, scope, key);
            case ACCOUNT -> applyAccount(item, scope, key);
            default -> log.warn("State file update not implemented for category {}", category);
        }
    }

    public <T> List<StateDocument> listStateDocuments(FileCategory category, String scope) {
        return stateRepository.list(category.name(), scope);
    }

    public <T> T loadPayload(FileCategory category, String scope, String key, Class<T> payloadType) {
        System.out.println("loadPayload-->"+category+"-->"+scope+"-->"+key+"-->"+payloadType);
        return loadStateDocument(category, scope, key)
                .map(doc -> parseStatePayload(doc.getData(), payloadType))
                .orElse(null);
    }

    public boolean payloadsEqual(Object left, Object right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        try {
            return objectMapper.valueToTree(left).equals(objectMapper.valueToTree(right));
        } catch (IllegalArgumentException e) {
            log.debug("Failed to compare payloads: {}", e.getMessage());
            return false;
        }
    }

    public <T> T payloadFromDocument(StateDocument document, Class<T> payloadType) {
        if (document == null) {
            return null;
        }
        return parseStatePayload(document.getData(), payloadType);
    }

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

    private void applySide(PlanItem item, String scope, String key) {
        if (item.getAction() == Action.DELETE) {
            deleteStateDocument(item.getFileCategory(), scope, key);
            return;
        }
        SideFile payload = castPayload(item, SideFile.class);
        if (payload.getScope() == null) {
            payload.setScope(scope);
        }
        persistStateDocument(item.getFileCategory(), scope, key, payload);
    }

    private void applyTransaction(PlanItem item, String scope, String key) {
        if (item.getAction() == Action.DELETE) {
            deleteStateDocument(item.getFileCategory(), scope, key);
            return;
        }
        TransactionFile payload = castPayload(item, TransactionFile.class);
        if (payload.getScope() == null) {
            payload.setScope(scope);
        }
        persistStateDocument(item.getFileCategory(), scope, key, payload);
    }

    private void applyChartOfAccounts(PlanItem item, String scope, String key) {
        if (item.getAction() == Action.DELETE) {
            deleteStateDocument(item.getFileCategory(), scope, key);
            return;
        }
        ChartOfAccountsRequest payload = castPayload(item, ChartOfAccountsRequest.class);
        ChartOfAccountsFile file = new ChartOfAccountsFile();
        file.setScope(scope);
        file.setChartOfAccountsCode(stripScopeSuffix(key, scope));
        file.setChartOfAccountsRequest(payload);
        persistStateDocument(item.getFileCategory(), scope, key, file);
    }

    private void applyPostingRule(PlanItem item, String scope, String key) {
        if (item.getAction() == Action.DELETE) {
            deleteStateDocument(item.getFileCategory(), scope, key);
            return;
        }
        PostingModuleRequest payload = castPayload(item, PostingModuleRequest.class);
        String trimmedKey = stripScopeSuffix(key, scope);
        String[] parts = trimmedKey.split("-");
        String moduleCode = parts.length > 0 ? parts[0] : trimmedKey;
        String coaCode = parts.length > 1 ? parts[1] : moduleCode;

        PostingRulesFile file = new PostingRulesFile();
        file.setScope(scope);
        file.setPostingModuleCode(moduleCode);
        file.setChartOfAccountsCode(coaCode);
        file.setPostingModuleRequest(payload);
        persistStateDocument(item.getFileCategory(), scope, key, file);
    }

    private void applyGeneralLedgerProfile(PlanItem item, String scope, String key) {
        if (item.getAction() == Action.DELETE) {
            deleteStateDocument(item.getFileCategory(), scope, key);
            return;
        }
        GeneralLedgerProfileRequest payload = castPayload(item, GeneralLedgerProfileRequest.class);
        GeneralLedgerProfileFile file = new GeneralLedgerProfileFile();
        file.setScope(scope);
        String trimmedKey = stripScopeSuffix(key, scope);
        file.setChartOfAccountsCode(extractChartOfAccountsFromGeneralLedgerProfileKey(trimmedKey));
        String profileCode = payload.getGeneralLedgerProfileCode();
        if (!StringUtils.hasText(profileCode)) {
            profileCode = extractGeneralLedgerProfileCodeFromKey(trimmedKey);
        }
        file.setGeneralLedgerProfileCode(profileCode);
        file.setGeneralLedgerProfileRequest(payload);
        persistStateDocument(item.getFileCategory(), scope, key, file);
    }

    private void applyAbor(PlanItem item, String scope, String key) {
        AborFile file = readOrDefault(item.getFileCategory(), scope, key, AborFile.class, AborFile::new);
        if (file.getScope() == null) {
            file.setScope(scope);
        }
        List<AborRequest> abors = new ArrayList<>(optionalList(file.getAborRequests()));
        if (item.getAction() == Action.DELETE) {
            removeByKey(abors, item.getKey(), AborRequest::getCode);
        } else {
            AborRequest payload = castPayload(item, AborRequest.class);
            upsertByKey(abors, payload, AborRequest::getCode, item.getKey());
        }
        file.setAborRequests(abors);
        persistListDocument(item.getFileCategory(), scope, key, file, abors);
    }

    private void applyAborConfiguration(PlanItem item, String scope, String key) {
        AborConfigurationFile file = readOrDefault(item.getFileCategory(), scope, key, AborConfigurationFile.class,
                AborConfigurationFile::new);
        if (file.getScope() == null) {
            file.setScope(scope);
        }
        List<AborConfigurationRequest> configs = new ArrayList<>(optionalList(file.getAborConfigurations()));
        if (item.getAction() == Action.DELETE) {
            removeByKey(configs, item.getKey(), AborConfigurationRequest::getCode);
        } else {
            AborConfigurationRequest payload = castPayload(item, AborConfigurationRequest.class);
            upsertByKey(configs, payload, AborConfigurationRequest::getCode, item.getKey());
        }
        file.setAborConfigurations(configs);
        persistListDocument(item.getFileCategory(), scope, key, file, configs);
    }

    private void applyDerivedPortfolio(PlanItem item, String scope, String key) {
        DerivedPortfolioFile file = readOrDefault(item.getFileCategory(), scope, key, DerivedPortfolioFile.class,
                DerivedPortfolioFile::new);
        if (file.getScope() == null) {
            file.setScope(scope);
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
        persistListDocument(item.getFileCategory(), scope, key, file, portfolios);
    }

    private void applyPortfolioGroup(PlanItem item, String scope, String key) {
        PortfolioGroupFile file = readOrDefault(item.getFileCategory(), scope, key, PortfolioGroupFile.class,
                PortfolioGroupFile::new);
        if (file.getScope() == null) {
            file.setScope(scope);
        }
        List<CreatePortfolioGroupRequest> groups = new ArrayList<>(optionalList(file.getGroups()));
        if (item.getAction() == Action.DELETE) {
            removeByKey(groups, item.getKey(), CreatePortfolioGroupRequest::getCode);
        } else {
            CreatePortfolioGroupRequest payload = castPayload(item, CreatePortfolioGroupRequest.class);
            upsertByKey(groups, payload, CreatePortfolioGroupRequest::getCode, item.getKey());
        }
        file.setGroups(groups);
        persistListDocument(item.getFileCategory(), scope, key, file, groups);
    }

    private void applyAccount(PlanItem item, String scope, String key) {
        System.out.println();
        AccountFile file = readOrDefault(item.getFileCategory(), scope, key, AccountFile.class, AccountFile::new);
        if (file.getScope() == null) {
            file.setScope(scope);
        }
        if (file.getChartOfAccountsCode() == null) {
            file.setChartOfAccountsCode(extractChartOfAccounts(key, scope));
        }
        System.out.println("accounts-->"+file.getAccounts());
        List<Account> accounts = new ArrayList<>(optionalList(file.getAccounts()));
        if (item.getAction() == Action.DELETE) {
            System.out.println("accounts-->"+accounts);
            System.out.println("Removing account " + item.getKey() + " from file " + file.getChartOfAccountsCode());
            removeAccountByCandidates(item, file, accounts);
        } else {
            Account payload = castPayload(item, Account.class);
            accounts.removeIf(acc -> Objects.equals(acc.getCode(), payload.getCode()));
            accounts.add(payload);
        }
        file.setAccounts(accounts);
        persistListDocument(item.getFileCategory(), scope, key, file, accounts);
    }

    @SuppressWarnings("unchecked")
    private <T> T castPayload(PlanItem item, Class<T> type) {
        Object payload = item.getPayload();
        if (payload == null) {
            throw new PlanApplyException("Missing payload for state update of " + item.getKey());
        }
        if (!type.isInstance(payload)) {
            throw new PlanApplyException("Unexpected payload type for " + item.getKey()
                    + "; expected " + type.getSimpleName() + " but found " + payload.getClass().getSimpleName());
        }
        return (T) payload;
    }

    private <T> T readOrDefault(FileCategory category,
                               String scope,
                               String key,
                               Class<T> type,
                               Supplier<T> fallback) {
        System.out.println("readOrDefault-->"+category+"-->"+scope+"-->"+key+"-->"+type+"-->"+fallback);
        T payload = loadPayload(category, scope, key, type);
        System.out.println("readOrDefault-->"+payload);
        return payload == null ? fallback.get() : payload;
    }

    private Optional<StateDocument> loadStateDocument(FileCategory category, String scope, String key) {
        String id = stateDocumentId(category, scope, key);
        System.out.println("loadStateDocument-->"+category+"-->"+scope+"-->"+key+"-->"+id);
        return stateRepository.find(id, category.name());
    }

    private <T> T parseStatePayload(JsonNode data, Class<T> type) {
        if (data == null) {
            return null;
        }
        try {
            return jsonModelMapper.read(data.toString(), type);
        } catch (IOException e) {
            throw new PlanApplyException("Failed to parse state payload for type " + type.getSimpleName(), e);
        }
    }

    private void persistStateDocument(FileCategory category, String scope, String key, Object content) {
        try {
            StateDocument document = new StateDocument();
            document.setId(stateDocumentId(category, scope, key));
            document.setTypeOfItem(category.name());
            document.setScope(normalizeScope(scope));
            document.setData(objectMapper.valueToTree(content));
            stateRepository.upsert(document);
            log.debug("State document {} persisted for category {}", document.getId(), category);
        } catch (Exception e) {
            throw new PlanApplyException("Failed to write state for " + category + "/" + key, e);
        }
    }

    private void deleteStateDocument(FileCategory category, String scope, String key) {
        stateRepository.delete(stateDocumentId(category, scope, key), category.name());
    }

    private void persistListDocument(FileCategory category,
                                     String scope,
                                     String key,
                                     Object file,
                                     List<?> contents) {
        if (contents == null || contents.isEmpty()) {
            deleteStateDocument(category, scope, key);
        } else {
            persistStateDocument(category, scope, key, file);
        }
    }

    private String resolveStorageKey(PlanItem item) {
        String key = item.getKey();
        if (StringUtils.hasText(item.getSourcePath())) {
            String derived = deriveKeyFromSourcePath(item.getSourcePath());
            if (StringUtils.hasText(derived)) {
                key = derived;
            }
        }
        return key;
    }

    private String deriveKeyFromSourcePath(String sourcePath) {
        if (!StringUtils.hasText(sourcePath)) {
            return null;
        }
        try {
            return deriveKeyFromFilename(Path.of(sourcePath));
        } catch (IllegalArgumentException e) {
            log.debug("Falling back to manual key derivation for {}: {}", sourcePath, e.getMessage());
            String normalized = sourcePath.replace('\\', '/');
            int segmentIndex = normalized.lastIndexOf('/');
            if (segmentIndex >= 0 && segmentIndex < normalized.length() - 1) {
                return normalized.substring(segmentIndex + 1);
            }
            return normalized;
        }
    }

    private String stateDocumentId(FileCategory category, String scope, String key) {
        if (category == FileCategory.ACCOUNT) {
            return accountStateDocumentId(scope, key);
        }
        return defaultStateDocumentId(scope, key);
    }

    private String defaultStateDocumentId(String scope, String key) {
        String normalizedKey = key == null ? "" : key.trim();
        String normalizedScope = normalizeScope(scope);
        if (!normalizedScope.isEmpty()) {
            String suffix = "-" + normalizedScope;
            if (!normalizedKey.isEmpty() && normalizedKey.toLowerCase(Locale.ROOT).endsWith(suffix.toLowerCase(Locale.ROOT))) {
                return normalizedKey;
            }
            return normalizedKey + suffix;
        }
        return normalizedKey;
    }

    private String accountStateDocumentId(String scope, String key) {
        String chartOfAccountsCode = extractChartOfAccounts(key, scope);
        if (!StringUtils.hasText(chartOfAccountsCode)) {
            return defaultStateDocumentId(scope, key);
        }
        String normalizedScope = normalizeScope(scope);
        if (normalizedScope.isEmpty()) {
            return chartOfAccountsCode;
        }
        return chartOfAccountsCode + "-" + normalizedScope;
    }

    private String normalizeScope(String scope) {
        return scope == null ? "" : scope.trim();
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

    private void removeAccountByCandidates(PlanItem item, AccountFile file, List<Account> accounts) {
        Set<String> codes = new LinkedHashSet<>();
        Account payload = item.getPayload() instanceof Account ? (Account) item.getPayload() : null;
        if (payload != null && StringUtils.hasText(payload.getCode())) {
            codes.add(payload.getCode());
        }
        String strippedKey = stripScopeSuffix(item.getKey(), item.getScope());
        if (StringUtils.hasText(strippedKey)) {
            codes.add(strippedKey);
        }
        String chartCode = file.getChartOfAccountsCode();
        if (StringUtils.hasText(chartCode) && StringUtils.hasText(strippedKey)
                && strippedKey.startsWith(chartCode + "-")) {
            codes.add(strippedKey.substring(chartCode.length() + 1));
        }
        Set<String> normalized = codes.stream()
                .filter(StringUtils::hasText)
                .map(this::normalizeAccountCode)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        accounts.removeIf(acc -> normalized.contains(normalizeAccountCode(acc.getCode())));
    }

    private String normalizeAccountCode(String code) {
        if (code == null) {
            return null;
        }
        return code.trim().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    private String extractChartOfAccountsFromGeneralLedgerProfileKey(String key) {
        if (key == null) {
            return "";
        }
        int dash = key.indexOf('-');
        if (dash >= 0 && dash + 1 < key.length()) {
            return key.substring(dash + 1);
        }
        return key;
    }

    private String extractGeneralLedgerProfileCodeFromKey(String key) {
        if (key == null) {
            return "";
        }
        int dash = key.indexOf('-');
        if (dash > 0) {
            return key.substring(0, dash);
        }
        return key;
    }
}
