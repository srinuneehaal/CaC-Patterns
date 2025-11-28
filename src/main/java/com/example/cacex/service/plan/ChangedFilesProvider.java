package com.example.cacex.service.plan;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component
public class ChangedFilesProvider {

    private static final String ENV_KEY = "CHANGED_FILES";

    public List<Path> getChangedPaths() {
        String envValue = System.getenv(ENV_KEY);
        if (envValue == null || envValue.trim().isEmpty()) {
            return List.of();
        }

        return Arrays.stream(envValue.trim().split("\\s+"))
                .map(this::normalizePath)
                .toList();
    }

    private Path normalizePath(String rawPath) {
        // Support both forward and backward slashes from env values.
        String sanitized = rawPath.replace("\"", "").trim();
        return Paths.get(sanitized);
    }
}
