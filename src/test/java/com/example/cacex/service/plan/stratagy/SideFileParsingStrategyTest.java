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

class SideFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesSideFile() throws Exception {
        Path path = tempDir.resolve("sides/side1.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "side": "BUY",
                  "sideDefinitionRequest": {"units": 1.0}
                }
                """);
        SideFileParsingStrategy strategy = new SideFileParsingStrategy(new JsonModelMapper(), buildProps());
        assertTrue(strategy.supports(path));
        LoadedFile loaded = strategy.parse(path);
        assertEquals(FileCategory.SIDE, loaded.getCategory());
        assertEquals("BUY-SCP", loaded.getKey());
    }

    @Test
    void fallsBackToFilenameWhenSideMissing() throws Exception {
        Path path = tempDir.resolve("sides/side2.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "{ \"scope\": \"S\" }");
        SideFileParsingStrategy strategy = new SideFileParsingStrategy(new JsonModelMapper(), buildProps());

        assertEquals("side2", strategy.parse(path).getKey());
    }

    @Test
    void supportsRejectsNonJson() {
        SideFileParsingStrategy strategy = new SideFileParsingStrategy(new JsonModelMapper(), buildProps());
        assertFalse(strategy.supports(Path.of("sides/file.txt")));
    }

    private FileLocationProperties buildProps() {
        FileLocationProperties props = new FileLocationProperties();
        props.setSidesDirName("sides");
        return props;
    }
}
