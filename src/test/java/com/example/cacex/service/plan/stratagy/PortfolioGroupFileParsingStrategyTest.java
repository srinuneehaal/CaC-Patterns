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

class PortfolioGroupFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesPortfolioGroup() throws Exception {
        Path path = tempDir.resolve("portfoliogroups/groups.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "createPortfolioGroupRequestList": [
                    { "code": "G1" }
                  ]
                }
                """);
        PortfolioGroupFileParsingStrategy strategy =
                new PortfolioGroupFileParsingStrategy(new JsonModelMapper(), props());
        assertTrue(strategy.supports(path));
        LoadedFile loaded = strategy.parse(path);
        assertEquals(FileCategory.PORTFOLIO_GROUP, loaded.getCategory());
        assertEquals("groups", loaded.getKey());
    }

    @Test
    void rejectsUnsupportedExtensions() {
        PortfolioGroupFileParsingStrategy strategy =
                new PortfolioGroupFileParsingStrategy(new JsonModelMapper(), props());
        assertFalse(strategy.supports(tempDir.resolve("portfoliogroups/file.txt")));
    }

    private FileLocationProperties props() {
        FileLocationProperties props = new FileLocationProperties();
        props.setPortfolioGroupsDirName("portfoliogroups");
        return props;
    }
}
