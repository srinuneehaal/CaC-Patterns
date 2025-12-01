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

class PostingRulesFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesPostingRules() throws Exception {
        Path path = tempDir.resolve("postingrules/rules.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "chartOfAccountsCode": "COA1",
                  "postingModuleCode": "PM1",
                  "postingModuleRequest": {
                    "code": "PM1",
                    "displayName": "module"
                  }
                }
                """);
        PostingRulesFileParsingStrategy strategy = new PostingRulesFileParsingStrategy(new JsonModelMapper(), props());
        assertTrue(strategy.supports(path));
        LoadedFile loaded = strategy.parse(path);
        assertEquals(FileCategory.POSTING_RULE, loaded.getCategory());
        assertEquals("PM1-COA1-SCP", loaded.getKey());
    }

    @Test
    void derivesModuleFromRequestWhenCodesMissing() throws Exception {
        Path path = tempDir.resolve("postingrules/rules2.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "postingModuleRequest": { "code": "PM2" }
                }
                """);
        PostingRulesFileParsingStrategy strategy = new PostingRulesFileParsingStrategy(new JsonModelMapper(), props());

        LoadedFile loaded = strategy.parse(path);

        assertEquals("PM2-SCP", loaded.getKey());
    }

    @Test
    void fallsBackToFilenameWhenCodesAbsent() throws Exception {
        Path path = tempDir.resolve("postingrules/rules3.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "{ \"scope\": \"S\" }");
        PostingRulesFileParsingStrategy strategy = new PostingRulesFileParsingStrategy(new JsonModelMapper(), props());

        LoadedFile loaded = strategy.parse(path);

        assertEquals("rules3", loaded.getKey());
    }

    @Test
    void unsupportedPathReturnsFalse() {
        PostingRulesFileParsingStrategy strategy = new PostingRulesFileParsingStrategy(new JsonModelMapper(), props());
        assertFalse(strategy.supports(Path.of("other/path.txt")));
    }

    private FileLocationProperties props() {
        FileLocationProperties props = new FileLocationProperties();
        props.setPostingRulesDirName("postingrules");
        return props;
    }
}
