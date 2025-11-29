package com.example.cacex.service.plan;

import com.example.cacex.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finbourne.lusid.model.AborConfigurationRequest;
import com.finbourne.lusid.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class JsonModelMapper {

    private static final Logger log = LoggerFactory.getLogger(JsonModelMapper.class);

    private final ObjectMapper objectMapper;

    public JsonModelMapper() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> T read(Path path, Class<T> type) throws IOException {
        String content = Files.readString(path, StandardCharsets.UTF_8);
        if (type == SideFile.class) {
            return type.cast(readSideFile(content));
        }
        if (type == TransactionFile.class) {
            return type.cast(readTransactionFile(content));
        }
        if (type == DerivedPortfolioFile.class) {
            return type.cast(readDerivedPortfolioFile(content));
        }
        if (type == PortfolioGroupFile.class) {
            return type.cast(readPortfolioGroupFile(content));
        }
        if (type == ChartOfAccountsFile.class) {
            return type.cast(readChartOfAccountsFile(content));
        }
        if (type == AccountFile.class) {
            return type.cast(readAccountFile(content));
        }
        if (type == AborConfigurationFile.class) {
            return type.cast(readAborConfigurationFile(content));
        }
        return objectMapper.readValue(content, type);
    }

    private SideFile readSideFile(String content) throws IOException {
        JsonNode root = objectMapper.readTree(content);
        SideFile sideFile = objectMapper.treeToValue(root, SideFile.class);
        JsonNode definition =  root.get("sideDefinitionRequest");
        if (definition != null && !definition.isNull()) {
            sideFile.setSideDefinition(objectMapper.treeToValue(definition,
                    com.finbourne.lusid.model.SideDefinitionRequest.class));
        } else {
            log.warn("sideDefinition missing when parsing side file payload. Fields: {}",
                    describeFields(root));
        }
        return sideFile;
    }

    private TransactionFile readTransactionFile(String content) throws IOException {
        JsonNode root = objectMapper.readTree(content);
        TransactionFile txnFile = objectMapper.treeToValue(root, TransactionFile.class);
        JsonNode definition =  root.get("transactionTypeRequest");
        if (definition != null && !definition.isNull()) {
            txnFile.setSideDefinition(objectMapper.treeToValue(definition,
                    com.finbourne.lusid.model.TransactionTypeRequest.class));
        } else {
            log.warn("sideDefinition missing when parsing transaction file payload. Fields: {}",
                    describeFields(root));
        }
        return txnFile;
    }

    private DerivedPortfolioFile readDerivedPortfolioFile(String content) throws IOException {
        JsonNode root = objectMapper.readTree(content);
        DerivedPortfolioFile file = objectMapper.treeToValue(root, DerivedPortfolioFile.class);
        JsonNode listNode = root.get("createDerivedPortfolioRequestList");
        if (listNode != null && !listNode.isNull()) {
            file.setDerivedPortfolios(objectMapper.readerForListOf(
                    com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest.class)
                    .readValue(listNode));
        } else {
            log.warn("createDerivedPortfolioRequestList missing when parsing derived portfolio payload. Fields: {}",
                    describeFields(root));
        }
        return file;
    }

    private PortfolioGroupFile readPortfolioGroupFile(String content) throws IOException {
        JsonNode root = objectMapper.readTree(content);
        PortfolioGroupFile file = objectMapper.treeToValue(root, PortfolioGroupFile.class);
        JsonNode listNode = root.get("createPortfolioGroupRequestList");
        if (listNode != null && !listNode.isNull()) {
            file.setGroups(objectMapper.readerForListOf(
                    com.finbourne.lusid.model.CreatePortfolioGroupRequest.class).readValue(listNode));
        } else {
            log.warn("createPortfolioGroupRequestList missing when parsing portfolio group payload. Fields: {}",
                    describeFields(root));
        }
        return file;
    }

    private ChartOfAccountsFile readChartOfAccountsFile(String content) throws IOException {
        JsonNode root = objectMapper.readTree(content);
        ChartOfAccountsFile file = objectMapper.treeToValue(root, ChartOfAccountsFile.class);
        JsonNode requestNode = root.get("chartOfAccountsRequest");
        if (requestNode != null && !requestNode.isNull()) {
            file.setChartOfAccountsRequest(objectMapper.treeToValue(requestNode,
                    com.finbourne.lusid.model.ChartOfAccountsRequest.class));
        } else {
            log.warn("chartOfAccountsRequest missing when parsing chart of accounts payload. Fields: {}",
                    describeFields(root));
        }
        return file;
    }

    private AccountFile readAccountFile(String content) throws IOException {
        JsonNode root = objectMapper.readTree(content);
        AccountFile file = objectMapper.treeToValue(root, AccountFile.class);
        JsonNode accountsNode = root.get("glAccounts");
        if (accountsNode != null && !accountsNode.isNull()) {
            file.setAccounts(objectMapper.readerForListOf(Account.class).readValue(accountsNode));
        } else {
            log.warn("glAccounts missing when parsing accounts payload. Fields: {}", describeFields(root));
        }
        return file;
    }

    private AborConfigurationFile readAborConfigurationFile(String content) throws IOException {
        JsonNode root = objectMapper.readTree(content);
        AborConfigurationFile file = objectMapper.treeToValue(root, AborConfigurationFile.class);
        JsonNode listNode = root.get("aborConfigurationRequestList");
        if (listNode != null && !listNode.isNull()) {
            file.setAborConfigurations(objectMapper.readerForListOf(AborConfigurationRequest.class).readValue(listNode));
        } else {
            log.warn("aborConfigurationRequestList missing when parsing ABOR configuration payload. Fields: {}",
                    describeFields(root));
        }
        return file;
    }

    private List<String> describeFields(JsonNode root) {
        List<String> fields = new java.util.ArrayList<>();
        root.fieldNames().forEachRemaining(fields::add);
        return fields;
    }
}
