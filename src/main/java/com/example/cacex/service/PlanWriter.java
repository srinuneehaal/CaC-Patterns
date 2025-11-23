package com.example.cacex.service;

import com.example.cacex.model.MasterPlan;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class PlanWriter {

    private static final Path OUTPUT_PATH = Path.of("plan", "masterplan.json");

    private final ObjectMapper objectMapper;

    public PlanWriter() {
        this.objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Path write(MasterPlan masterPlan) {
        try {
            Files.createDirectories(OUTPUT_PATH.getParent());
            objectMapper.writeValue(OUTPUT_PATH.toFile(), masterPlan);
            return OUTPUT_PATH;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write plan file", e);
        }
    }
}
