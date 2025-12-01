package com.example.cacex.service.plan;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class ChangedFilesProvider {

    private static final String ENV_KEY = "CHANGED_FILES";

    private final Function<String, String> envLookup;

    /**
     * Creates a provider that reads from real environment variables.
     */
    public ChangedFilesProvider() {
        this(System::getenv);
    }

    /**
     * Creates a provider with a custom environment lookup (primarily for testing).
     *
     * @param envLookup function that returns environment values
     */
    ChangedFilesProvider(Function<String, String> envLookup) {
        this.envLookup = envLookup;
    }

    /**
     * Returns normalized paths supplied via the CHANGED_FILES environment variable.
     *
     * @return list of changed file paths, or empty when none
     */
    public List<Path> getChangedPaths() {
        String envValue = envLookup.apply(ENV_KEY);
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
