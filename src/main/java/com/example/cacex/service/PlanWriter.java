package com.example.cacex.service;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.MasterPlan;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class PlanWriter {

    private final FileLocationProperties fileLocationProperties;
    private final ObjectMapper objectMapper;

    public PlanWriter(FileLocationProperties fileLocationProperties) {
        this.fileLocationProperties = fileLocationProperties;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Path write(MasterPlan masterPlan) {
        Path outputPath = fileLocationProperties.masterPlanPath();
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            objectMapper.writeValue(outputPath.toFile(), masterPlan);
            return outputPath;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write plan file", e);
        }
    }
}
