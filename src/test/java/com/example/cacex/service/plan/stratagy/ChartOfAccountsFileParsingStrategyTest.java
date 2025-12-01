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

class ChartOfAccountsFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesChartOfAccounts() throws Exception {
        Path path = tempDir.resolve("coa/coa.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "chartOfAccountsCode": "COA1",
                  "chartOfAccountsRequest": { "code": "COA1" }
                }
                """);
        ChartOfAccountsFileParsingStrategy strategy = new ChartOfAccountsFileParsingStrategy(new JsonModelMapper(), props());
        assertTrue(strategy.supports(path));
        LoadedFile loaded = strategy.parse(path);
        assertEquals(FileCategory.CHART_OF_ACCOUNTS, loaded.getCategory());
        assertEquals("COA1-SCP", loaded.getKey());
    }

    @Test
    void supportsReturnsFalseForNonJsonOrFolder() {
        ChartOfAccountsFileParsingStrategy strategy = new ChartOfAccountsFileParsingStrategy(new JsonModelMapper(), props());
        assertFalse(strategy.supports(Path.of("other/file.txt")));
    }

    @Test
    void usesRequestCodeWhenChartCodeMissing() throws Exception {
        Path path = tempDir.resolve("coa/coa2.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "chartOfAccountsRequest": { "code": "COA2" }
                }
                """);
        ChartOfAccountsFileParsingStrategy strategy = new ChartOfAccountsFileParsingStrategy(new JsonModelMapper(), props());

        assertEquals("COA2-SCP", strategy.parse(path).getKey());
    }

    @Test
    void fallsBackToFilenameWhenCodesAbsent() throws Exception {
        Path path = tempDir.resolve("other/chartofaccounts/unknown.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "{ \"scope\": \"S\" }");
        ChartOfAccountsFileParsingStrategy strategy = new ChartOfAccountsFileParsingStrategy(new JsonModelMapper(), props());

        assertTrue(strategy.supports(path));
        assertEquals("unknown", strategy.parse(path).getKey());
    }

    @Test
    void returnsCodeWithoutScopeWhenScopeMissing() throws Exception {
        Path path = tempDir.resolve("coa/coa3.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "chartOfAccountsCode": "COA3"
                }
                """);
        ChartOfAccountsFileParsingStrategy strategy = new ChartOfAccountsFileParsingStrategy(new JsonModelMapper(), props());

        assertEquals("COA3", strategy.parse(path).getKey());
    }

    private FileLocationProperties props() {
        FileLocationProperties props = new FileLocationProperties();
        props.setChartOfAccountsDirName("coa");
        return props;
    }
}
