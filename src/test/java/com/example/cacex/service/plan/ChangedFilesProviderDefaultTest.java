package com.example.cacex.service.plan;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChangedFilesProviderDefaultTest {

    @Test
    void defaultConstructorUsesSystemEnv() {
        ChangedFilesProvider provider = new ChangedFilesProvider();

        List<?> paths = provider.getChangedPaths();
        assertNotNull(paths);
    }
}
