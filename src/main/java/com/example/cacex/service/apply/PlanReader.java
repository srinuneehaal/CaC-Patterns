package com.example.cacex.service.apply;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finbourne.lusid.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class PlanReader {

    private static final Logger log = LoggerFactory.getLogger(PlanReader.class);

    private final FileLocationProperties fileLocationProperties;
    private final ObjectMapper objectMapper;

    public PlanReader(FileLocationProperties fileLocationProperties, ObjectMapper objectMapper) {
        this.fileLocationProperties = fileLocationProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Reads and converts the configured master plan file into a {@link MasterPlan}.
     *
     * @return populated master plan
     * @throws IllegalStateException if the plan file is missing or cannot be parsed
     */
    public MasterPlan read() {
        Path inputPath = fileLocationProperties.masterPlanPath();
        if (!Files.exists(inputPath)) {
            throw new IllegalStateException("Plan file not found at " + inputPath.toAbsolutePath());
        }
        try {
            JsonNode root = objectMapper.readTree(inputPath.toFile());
            MasterPlan plan = new MasterPlan();
            JsonNode itemsNode = root.get("items");
            if (itemsNode == null || !itemsNode.isArray()) {
                log.warn("Plan file {} contains no items array", inputPath.toAbsolutePath());
                return plan;
            }
            for (JsonNode itemNode : itemsNode) {
                PlanItem planItem = objectMapper.treeToValue(itemNode, PlanItem.class);
                planItem.setPayload(convertPayload(planItem.getFileCategory(), itemNode.get("payload")));
                plan.addItem(planItem);
            }
            return plan;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read plan file", e);
        }
    }

    private Object convertPayload(FileCategory category, JsonNode payloadNode) {
        if (payloadNode == null || payloadNode.isNull() || category == null) {
            return null;
        }
        return switch (category) {
            case SIDE -> objectMapper.convertValue(payloadNode, SideFile.class);
            case TRANSACTION -> objectMapper.convertValue(payloadNode, TransactionFile.class);
            case ABOR_CONFIGURATION ->
                    objectMapper.convertValue(payloadNode, AborConfigurationRequest.class);
            case ABOR ->
                    objectMapper.convertValue(payloadNode, AborRequest.class);
            case DERIVED_PORTFOLIO ->
                    objectMapper.convertValue(payloadNode, CreateDerivedTransactionPortfolioRequest.class);
            case PORTFOLIO_GROUP ->
                    objectMapper.convertValue(payloadNode, CreatePortfolioGroupRequest.class);
            case CHART_OF_ACCOUNTS ->
                    objectMapper.convertValue(payloadNode, ChartOfAccountsRequest.class);
            case ACCOUNT -> objectMapper.convertValue(payloadNode, Account.class);
            case POSTING_RULE -> objectMapper.convertValue(payloadNode, PostingModuleRequest.class);
        };
    }
}
