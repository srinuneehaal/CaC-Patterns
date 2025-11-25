package com.example.cacex.service;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.MasterPlan;
import com.example.cacex.model.PlanItem;
import com.example.cacex.model.SideFile;
import com.example.cacex.model.TransactionFile;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finbourne.lusid.model.ChartOfAccountsRequest;
import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;
import com.finbourne.lusid.model.CreatePortfolioGroupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class PlanReader {

    private static final Logger log = LoggerFactory.getLogger(PlanReader.class);
    private static final Path INPUT_PATH = Path.of("plan", "masterplan.json");

    private final ObjectMapper objectMapper;

    public PlanReader() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public MasterPlan read() {
        if (!Files.exists(INPUT_PATH)) {
            throw new IllegalStateException("Plan file not found at " + INPUT_PATH.toAbsolutePath());
        }
        try {
            JsonNode root = objectMapper.readTree(INPUT_PATH.toFile());
            MasterPlan plan = new MasterPlan();
            JsonNode itemsNode = root.get("items");
            if (itemsNode == null || !itemsNode.isArray()) {
                log.warn("Plan file {} contains no items array", INPUT_PATH.toAbsolutePath());
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
            case DERIVED_PORTFOLIO ->
                    objectMapper.convertValue(payloadNode, CreateDerivedTransactionPortfolioRequest.class);
            case PORTFOLIO_GROUP ->
                    objectMapper.convertValue(payloadNode, CreatePortfolioGroupRequest.class);
            case CHART_OF_ACCOUNTS ->
                    objectMapper.convertValue(payloadNode, ChartOfAccountsRequest.class);
        };
    }
}
