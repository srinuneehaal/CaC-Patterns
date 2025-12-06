package com.example.cacex.service;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.*;
import com.example.cacex.service.plan.rules.PlanOrderingRuleEngine;
import com.example.cacex.service.plan.stratagy.FileParsingStrategy;
import com.example.cacex.service.plan.stratagy.FileParsingStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlanServiceCoreTest {

    @TempDir
    Path tempDir;

    @Mock
    private FileParsingStrategyFactory strategyFactory;

    @Mock
    private PlanOrderingRuleEngine planOrderingRuleEngine;

    @Mock
    private StateFileService stateFileService;

    private FileLocationProperties properties;
    private PlanService planService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        properties = new FileLocationProperties();
        properties.setChangedFilesDir("changedfiles");

        when(planOrderingRuleEngine.applyOrdering(any())).thenAnswer(inv -> inv.getArgument(0, MasterPlan.class));

        planService = new PlanService(strategyFactory, planOrderingRuleEngine, properties, stateFileService);
    }

    @Test
    void addsNewItemWhenStateMissing() throws IOException {
        Path changed = tempDir.resolve("changedfiles/S1/transactions/tx1.json");
        Files.createDirectories(changed.getParent());
        Files.createFile(changed);

        when(stateFileService.deriveKeyFromFilename(changed)).thenReturn("tx1");
        when(stateFileService.loadPayload(FileCategory.TRANSACTION, "S1", "tx1", TransactionFile.class))
                .thenReturn(null);

        StubParsingStrategy strategy = new StubParsingStrategy(FileCategory.TRANSACTION);
        strategy.addPayload(changed, new LoadedFile(FileCategory.TRANSACTION, "tx1", changed, "new-payload"));
        when(strategyFactory.resolve(any(Path.class))).thenReturn(strategy);

        MasterPlan plan = planService.buildPlan(List.of(changed));

        assertEquals(1, plan.getItems().size());
        PlanItem item = plan.getItems().get(0);
        assertEquals(Action.NEW, item.getAction());
        assertEquals(FileCategory.TRANSACTION, item.getFileCategory());
        assertEquals("S1", item.getScope());
        assertEquals("tx1", item.getKey());
        assertEquals("new-payload", item.getPayload());
    }

    @Test
    void addsUpdateWhenChangedPayloadDiffersFromState() throws IOException {
        Path changed = tempDir.resolve("changedfiles/S1/transactions/tx2.json");
        Files.createDirectories(changed.getParent());
        Files.createFile(changed);

        when(stateFileService.deriveKeyFromFilename(changed)).thenReturn("tx2");
        when(stateFileService.loadPayload(FileCategory.TRANSACTION, "S1", "tx2", TransactionFile.class))
                .thenReturn(new TransactionFile());

        StubParsingStrategy strategy = new StubParsingStrategy(FileCategory.TRANSACTION);
        strategy.addPayload(changed, new LoadedFile(FileCategory.TRANSACTION, "tx2", changed, "changed-payload"));
        when(strategyFactory.resolve(any(Path.class))).thenReturn(strategy);

        MasterPlan plan = planService.buildPlan(List.of(changed));

        assertEquals(1, plan.getItems().size());
        PlanItem item = plan.getItems().get(0);
        assertEquals(Action.UPDATE, item.getAction());
        assertEquals("changed-payload", item.getPayload());
    }

    @Test
    void addsDeleteWhenChangedFileMissingButStateExists() throws IOException {
        Path changed = tempDir.resolve("changedfiles/S1/transactions/tx3.json");
        TransactionFile statePayload = new TransactionFile();
        when(stateFileService.deriveKeyFromFilename(changed)).thenReturn("tx3");
        when(stateFileService.loadPayload(FileCategory.TRANSACTION, "S1", "tx3", TransactionFile.class))
                .thenReturn(statePayload);

        StubParsingStrategy strategy = new StubParsingStrategy(FileCategory.TRANSACTION);
        when(strategyFactory.resolve(any(Path.class))).thenReturn(strategy);

        MasterPlan plan = planService.buildPlan(List.of(changed));

        assertEquals(1, plan.getItems().size());
        PlanItem item = plan.getItems().get(0);
        assertEquals(Action.DELETE, item.getAction());
        assertEquals(statePayload, item.getPayload());
    }

    @Test
    void ignoresNonChangedRootFilesButStillOrdersPlan() {
        Path other = tempDir.resolve("unrelated/file.json");

        MasterPlan plan = planService.buildPlan(List.of(other));

        assertTrue(plan.getItems().isEmpty());
        ArgumentCaptor<MasterPlan> captor = ArgumentCaptor.forClass(MasterPlan.class);
        verify(planOrderingRuleEngine).applyOrdering(captor.capture());
        assertTrue(captor.getValue().getItems().isEmpty());
    }

    private static final class StubParsingStrategy implements FileParsingStrategy {

        private final FileCategory category;
        private final Map<Path, LoadedFile> payloads = new HashMap<>();

        private StubParsingStrategy(FileCategory category) {
            this.category = category;
        }

        @Override
        public FileCategory getCategory() {
            return category;
        }

        @Override
        public boolean supports(Path path) {
            return true;
        }

        @Override
        public LoadedFile parse(Path path) {
            return payloads.get(path);
        }

        void addPayload(Path path, LoadedFile loadedFile) {
            payloads.put(path, loadedFile);
        }
    }
}
