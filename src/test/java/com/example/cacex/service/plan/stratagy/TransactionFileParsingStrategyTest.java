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

class TransactionFileParsingStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void supportsAndParsesTransactionFile() throws Exception {
        Path path = tempDir.resolve("transactions/txn.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP",
                  "transactionType": "TYPE",
                  "transactionClass": "CLS",
                  "source": "SRC",
                  "transactionSequence": 2,
                  "transactionTypeRequest": {"description": "d"}
                }
                """);
        TransactionFileParsingStrategy strategy = new TransactionFileParsingStrategy(new JsonModelMapper(), buildProps());
        assertTrue(strategy.supports(path));
        LoadedFile loaded = strategy.parse(path);
        assertEquals(FileCategory.TRANSACTION, loaded.getCategory());
        assertEquals("CLS-SRC-SCP", loaded.getKey());
    }

    @Test
    void fallsBackToFilenameWhenTypeMissing() throws Exception {
        Path path = tempDir.resolve("transactions/txn2.json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, """
                {
                  "scope": "SCP"
                }
                """);
        TransactionFileParsingStrategy strategy = new TransactionFileParsingStrategy(new JsonModelMapper(), buildProps());

        assertEquals("txn2", strategy.parse(path).getKey());
    }

    @Test
    void supportsRejectsNonJson() {
        TransactionFileParsingStrategy strategy = new TransactionFileParsingStrategy(new JsonModelMapper(), buildProps());
        assertFalse(strategy.supports(Path.of("transactions/file.txt")));
    }

    private FileLocationProperties buildProps() {
        FileLocationProperties props = new FileLocationProperties();
        props.setTransactionsDirName("transactions");
        return props;
    }
}
