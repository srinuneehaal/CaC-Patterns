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

class AccountFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesAccountFile() throws Exception {
        Path path = tempDir.resolve("gla/acc.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "chartOfAccountsCode": "COA",
                  "glAccounts": [
                    {"code": "ACC1"}
                  ]
                }
                """);
        AccountFileParsingStrategy strategy = new AccountFileParsingStrategy(new JsonModelMapper(), props());
        assertTrue(strategy.supports(path));
        LoadedFile loaded = strategy.parse(path);
        assertEquals(FileCategory.ACCOUNT, loaded.getCategory());
        assertEquals("acc", loaded.getKey());
    }

    @Test
    void supportsReturnsFalseForNonJsonOrWrongFolder() {
        AccountFileParsingStrategy strategy = new AccountFileParsingStrategy(new JsonModelMapper(), props());
        assertFalse(strategy.supports(Path.of("gla/file.txt")));
        assertFalse(strategy.supports(Path.of("other/folder/file.json")));
    }

    private FileLocationProperties props() {
        FileLocationProperties props = new FileLocationProperties();
        props.setAccountsDirName("gla");
        return props;
    }
}
