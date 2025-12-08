package com.example.cacex.service.plan.stratagy;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import com.example.cacex.service.plan.JsonModelMapper;
import com.finbourne.lusid.model.GeneralLedgerProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GeneralLedgerProfileFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesGeneralLedgerProfile() throws IOException {
        Path path = tempDir.resolve("glprofile/profile.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "ATG",
                  "chartOfAccountsCode": "USG",
                  "generalLedgerProfileCode": "USG",
                  "generalLedgerProfileRequest": {
                    "generalLedgerProfileCode": "USG",
                    "displayName": "USGAAP"
                  }
                }
                """);
        GeneralLedgerProfileFileParsingStrategy strategy = new GeneralLedgerProfileFileParsingStrategy(
                new JsonModelMapper(), props());

        assertTrue(strategy.supports(path));

        LoadedFile loaded = strategy.parse(path);

        assertEquals(FileCategory.GENERAL_LEDGER_PROFILE, loaded.getCategory());
        assertEquals("USG-USG-ATG", loaded.getKey());
        assertTrue(loaded.getPayload() instanceof GeneralLedgerProfileRequest);
        GeneralLedgerProfileRequest request = (GeneralLedgerProfileRequest) loaded.getPayload();
        assertEquals("USG", request.getGeneralLedgerProfileCode());
    }

    @Test
    void fallsBackToFilenameWhenCodesAbsent() throws IOException {
        Path path = tempDir.resolve("glprofile/profile2.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "ATG",
                  "generalLedgerProfileRequest": {
                    "displayName": "Fallback"
                  }
                }
                """);
        GeneralLedgerProfileFileParsingStrategy strategy = new GeneralLedgerProfileFileParsingStrategy(
                new JsonModelMapper(), props());

        LoadedFile loaded = strategy.parse(path);

        assertEquals("profile2", loaded.getKey());
        assertNotNull(loaded.getPayload());
    }

    private FileLocationProperties props() {
        FileLocationProperties props = new FileLocationProperties();
        props.setGeneralLedgerProfilesDirName("glprofile");
        return props;
    }
}
