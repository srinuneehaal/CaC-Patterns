package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.plan.JsonModelMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AborConfigurationFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesAborConfiguration() throws IOException {
        Path path = tempDir.resolve("aborconfigs/config.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "aborConfigurationRequestList": [
                    { "code": "CFG1" }
                  ]
                }
                """);
        AborConfigurationFileParsingStrategy strategy =
                new AborConfigurationFileParsingStrategy(new JsonModelMapper(), props());
        assertTrue(strategy.supports(path));
        LoadedFile loaded = strategy.parse(path);
        assertEquals(FileCategory.ABOR_CONFIGURATION, loaded.getCategory());
        assertEquals("config", loaded.getKey());
    }

    @Test
    void rejectsUnsupportedExtensions() {
        AborConfigurationFileParsingStrategy strategy =
                new AborConfigurationFileParsingStrategy(new JsonModelMapper(), props());
        assertFalse(strategy.supports(tempDir.resolve("aborconfigs/config.txt")));
    }

    private FileLocationProperties props() {
        FileLocationProperties props = new FileLocationProperties();
        props.setAborConfigurationsDirName("aborconfigs");
        return props;
    }
}