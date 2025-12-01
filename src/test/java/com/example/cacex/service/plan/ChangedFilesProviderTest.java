package com.example.cacex.service.plan;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChangedFilesProviderTest {

    @Test
    void returnsEmptyListWhenEnvMissing() {
        ChangedFilesProvider provider = new ChangedFilesProvider(env -> null);

        assertTrue(provider.getChangedPaths().isEmpty());
    }

    @Test
    void returnsEmptyListWhenEnvBlank() {
        ChangedFilesProvider provider = new ChangedFilesProvider(env -> "   ");

        assertTrue(provider.getChangedPaths().isEmpty());
    }

    @Test
    void parsesAndSanitizesChangedFilePathsFromEnv() {
        ChangedFilesProvider provider = new ChangedFilesProvider(env -> "\"changedfiles/foo.json\" changedfiles\\bar.json");

        List<Path> paths = provider.getChangedPaths();

        assertEquals(List.of(
                Path.of("changedfiles", "foo.json"),
                Path.of("changedfiles\\bar.json")
        ), paths);
    }
}
