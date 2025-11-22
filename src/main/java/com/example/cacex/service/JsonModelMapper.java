package com.example.cacex.service;

import com.example.cacex.model.SideFile;
import com.example.cacex.model.TransactionFile;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        return objectMapper.readValue(content, type);
    }

    private SideFile readSideFile(String content) throws IOException {
        JsonNode root = objectMapper.readTree(content);
        SideFile sideFile = objectMapper.treeToValue(root, SideFile.class);
        JsonNode definition = root.has("sideDefinition")
                ? root.get("sideDefinition")
                : root.get("sideDefinitionRequest");
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
        JsonNode definition = root.has("sideDefinition")
                ? root.get("sideDefinition")
                : root.get("transactionTypeRequest");
        if (definition != null && !definition.isNull()) {
            txnFile.setSideDefinition(objectMapper.treeToValue(definition,
                    com.finbourne.lusid.model.TransactionTypeRequest.class));
        } else {
            log.warn("sideDefinition missing when parsing transaction file payload. Fields: {}",
                    describeFields(root));
        }
        return txnFile;
    }

    private List<String> describeFields(JsonNode root) {
        List<String> fields = new java.util.ArrayList<>();
        root.fieldNames().forEachRemaining(fields::add);
        return fields;
    }
}
