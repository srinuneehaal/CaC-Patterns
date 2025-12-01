package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.plan.JsonModelMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AborFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesAborFile() throws Exception {
        Path path = tempDir.resolve("abor/abor.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "aborRequestList": [
                    { "code": "AB1" }
                  ]
                }
                """);
        AborFileParsingStrategy strategy = new AborFileParsingStrategy(new JsonModelMapper(), props());
        assertTrue(strategy.supports(path));
        LoadedFile loaded = strategy.parse(path);
        assertEquals(FileCategory.ABOR, loaded.getCategory());
        assertEquals("abor", loaded.getKey());
    }

    @Test
    void rejectsAborConfigurationPaths() {
        AborFileParsingStrategy strategy = new AborFileParsingStrategy(new JsonModelMapper(), props());
        Path cfgPath = tempDir.resolve("aborconfigs/config.json");
        assertFalse(strategy.supports(cfgPath));
    }

    private FileLocationProperties props() {
        FileLocationProperties props = new FileLocationProperties();
        props.setAborDirName("abor");
        props.setAborConfigurationsDirName("aborconfigs");
        return props;
    }
}
