package com.example.cacex.service;

import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.MasterPlan;
import com.example.cacex.model.PlanItem;
import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.service.apply.PlanReader;
import com.example.cacex.service.apply.itemapply.PlanItemApplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanApplyServiceTest {

    @Mock
    private PlanReader planReader;
    @Mock
    private StateFileService stateFileService;
    @Mock
    private PlanItemApplier sideApplier;
    @Mock
    private PlanItemApplier txnApplier;

    private PlanApplyService planApplyService;

    @BeforeEach
    void setup() {
        when(sideApplier.supportedCategory()).thenReturn(FileCategory.SIDE);
        when(txnApplier.supportedCategory()).thenReturn(FileCategory.TRANSACTION);
        planApplyService = new PlanApplyService(planReader, stateFileService, List.of(sideApplier, txnApplier));
    }

    @Test
    void applyPlanInvokesAppliersAndStateUpdates() {
        PlanItem sideItem = new PlanItem(Action.NEW, FileCategory.SIDE, "SCP", "SideKey", "path", null);
        PlanItem txnItem = new PlanItem(Action.UPDATE, FileCategory.TRANSACTION, "SCP", "TxnKey", "path", null);
        MasterPlan masterPlan = new MasterPlan();
        masterPlan.addItem(sideItem);
        masterPlan.addItem(txnItem);
        when(planReader.read()).thenReturn(masterPlan);

        planApplyService.applyPlan();

        verify(sideApplier).apply(sideItem);
        verify(txnApplier).apply(txnItem);
        verify(stateFileService).applyStateChange(sideItem);
        verify(stateFileService).applyStateChange(txnItem);
    }

    @Test
    void missingApplierDoesNotThrow() {
        PlanItem unknown = new PlanItem(Action.DELETE, FileCategory.POSTING_RULE, "S", "Key", "path", null);
        MasterPlan masterPlan = new MasterPlan();
        masterPlan.addItem(unknown);
        when(planReader.read()).thenReturn(masterPlan);
        planApplyService = new PlanApplyService(planReader, stateFileService, List.of(sideApplier));

        assertDoesNotThrow(() -> planApplyService.applyPlan());
        verifyNoInteractions(stateFileService);
    }

    @Test
    void emptyPlanSkipsProcessing() {
        when(planReader.read()).thenReturn(new MasterPlan());

        planApplyService.applyPlan();

        verify(sideApplier, never()).apply(any());
        verify(txnApplier, never()).apply(any());
        verifyNoInteractions(stateFileService);
    }

    @Test
    void readFailureWrappedInPlanApplyException() {
        when(planReader.read()).thenThrow(new IllegalStateException("boom"));

        assertThrows(PlanApplyException.class, () -> planApplyService.applyPlan());
        verify(sideApplier, never()).apply(any());
        verify(txnApplier, never()).apply(any());
        verifyNoInteractions(stateFileService);
    }

    @Test
    void applierExceptionDoesNotStopOtherItems() {
        PlanItem failing = new PlanItem(Action.NEW, FileCategory.SIDE, "S", "Key1", "p", null);
        PlanItem succeeding = new PlanItem(Action.NEW, FileCategory.TRANSACTION, "S", "Key2", "p", null);
        MasterPlan plan = new MasterPlan();
        plan.addItem(failing);
        plan.addItem(succeeding);
        when(planReader.read()).thenReturn(plan);
        doThrow(new PlanApplyException("bad")).when(sideApplier).apply(failing);

        planApplyService.applyPlan();

        verify(sideApplier).apply(failing);
        verify(txnApplier).apply(succeeding);
        verify(stateFileService).applyStateChange(succeeding);
    }

    @Test
    void duplicateAppliersFavorFirstRegistered() {
        PlanItemApplier duplicateSide = mock(PlanItemApplier.class);
        when(duplicateSide.supportedCategory()).thenReturn(FileCategory.SIDE);

        planApplyService = new PlanApplyService(planReader, stateFileService, List.of(sideApplier, duplicateSide));

        MasterPlan masterPlan = new MasterPlan();
        PlanItem item = new PlanItem(Action.NEW, FileCategory.SIDE, "S", "K", "p", null);
        masterPlan.addItem(item);
        when(planReader.read()).thenReturn(masterPlan);

        planApplyService.applyPlan();

        verify(sideApplier).apply(item);
        verify(duplicateSide, never()).apply(any());
    }

    @Test
    void unexpectedExceptionsAreCaughtPerItem() {
        PlanItem errorItem = new PlanItem(Action.NEW, FileCategory.SIDE, "S", "Key", "p", null);
        PlanItem okItem = new PlanItem(Action.NEW, FileCategory.TRANSACTION, "S", "Key2", "p", null);
        MasterPlan masterPlan = new MasterPlan();
        masterPlan.addItem(errorItem);
        masterPlan.addItem(okItem);
        when(planReader.read()).thenReturn(masterPlan);
        doThrow(new RuntimeException("boom")).when(sideApplier).apply(errorItem);

        planApplyService.applyPlan();

        verify(sideApplier).apply(errorItem);
        verify(txnApplier).apply(okItem);
    }
}
