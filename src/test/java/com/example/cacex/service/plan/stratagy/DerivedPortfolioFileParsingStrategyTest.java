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

class DerivedPortfolioFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesDerivedPortfolio() throws Exception {
        Path path = tempDir.resolve("derivedportfolios/derived.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "createDerivedPortfolioRequestList": [
                    { "code": "DP1" }
                  ]
                }
                """);
        DerivedPortfolioFileParsingStrategy strategy =
                new DerivedPortfolioFileParsingStrategy(new JsonModelMapper(), props());
        assertTrue(strategy.supports(path));
        LoadedFile loaded = strategy.parse(path);
        assertEquals(FileCategory.DERIVED_PORTFOLIO, loaded.getCategory());
        assertEquals("derived", loaded.getKey());
    }

    @Test
    void rejectsUnsupportedFiles() {
        DerivedPortfolioFileParsingStrategy strategy =
                new DerivedPortfolioFileParsingStrategy(new JsonModelMapper(), props());
        assertFalse(strategy.supports(tempDir.resolve("other/file.txt")));
    }

    private FileLocationProperties props() {
        FileLocationProperties props = new FileLocationProperties();
        props.setDerivedPortfoliosDirName("derivedportfolios");
        return props;
    }
}
