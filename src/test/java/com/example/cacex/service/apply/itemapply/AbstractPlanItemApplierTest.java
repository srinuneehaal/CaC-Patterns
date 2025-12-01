package com.example.cacex.service.apply.itemapply;

import com.example.cacex.exception.InvalidPlanItemException;
import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.PlanItem;
import com.example.cacex.service.apply.apiservice.PlanItemActionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AbstractPlanItemApplierTest {

    @Mock
    private PlanItemActionService<String> apiService;

    @InjectMocks
    private TestApplier applier;

    @Test
    void delegatesToCreateUpdateDeleteBasedOnAction() {
        PlanItem newItem = new PlanItem(Action.NEW, FileCategory.SIDE, "S", "K", "p", "payload");
        PlanItem updateItem = new PlanItem(Action.UPDATE, FileCategory.SIDE, "S", "K", "p", "payload");
        PlanItem deleteItem = new PlanItem(Action.DELETE, FileCategory.SIDE, "S", "K", "p", "payload");

        applier.apply(newItem);
        applier.apply(updateItem);
        applier.apply(deleteItem);

        InOrder order = inOrder(apiService);
        order.verify(apiService).create("S", "K", "payload");
        order.verify(apiService).update("S", "K", "payload");
        order.verify(apiService).delete("S", "K", "payload");
    }

    @Test
    void throwsInvalidPlanItemExceptionForWrongPayloadType() {
        PlanItem item = new PlanItem(Action.NEW, FileCategory.SIDE, "S", "K", "p", 123);

        assertThrows(InvalidPlanItemException.class, () -> applier.apply(item));
        verifyNoInteractions(apiService);
    }

    @Test
    void throwsInvalidPlanItemExceptionWhenActionNull() {
        PlanItem item = new PlanItem(null, FileCategory.SIDE, "S", "K", "p", "payload");

        assertThrows(InvalidPlanItemException.class, () -> applier.apply(item));
        verifyNoInteractions(apiService);
    }

    @Test
    void wrapsRuntimeExceptionsFromApiService() {
        PlanItem item = new PlanItem(Action.NEW, FileCategory.SIDE, "S", "K", "p", "payload");
        doThrow(new IllegalStateException("boom")).when(apiService).create(eq("S"), eq("K"), eq("payload"));

        assertThrows(PlanApplyException.class, () -> applier.apply(item));
    }

    @Test
    void allowsNullPayloadForDelete() {
        PlanItem delete = new PlanItem(Action.DELETE, FileCategory.SIDE, "S", "K", "p", null);

        applier.apply(delete);

        Mockito.verify(apiService).delete("S", "K", null);
    }

    private static class TestApplier extends AbstractPlanItemApplier<String> {
        protected TestApplier(PlanItemActionService<String> apiService) {
            super(String.class, apiService);
        }

        @Override
        public FileCategory supportedCategory() {
            return FileCategory.SIDE;
        }
    }
}
