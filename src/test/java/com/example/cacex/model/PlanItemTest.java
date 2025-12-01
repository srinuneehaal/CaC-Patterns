package com.example.cacex.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PlanItemTest {

    @Test
    void constructorAndGettersExposeValues() {
        Object payload = new Object();
        PlanItem item = new PlanItem(Action.NEW, FileCategory.TRANSACTION, "ScopeA", "Key1", "source.json", payload);

        assertEquals(Action.NEW, item.getAction());
        assertEquals(FileCategory.TRANSACTION, item.getFileCategory());
        assertEquals("ScopeA", item.getScope());
        assertEquals("Key1", item.getKey());
        assertEquals("source.json", item.getSourcePath());
        assertSame(payload, item.getPayload());
    }

    @Test
    void settersAllowUpdatingPlanItem() {
        PlanItem item = new PlanItem();
        item.setAction(Action.UPDATE);
        item.setFileCategory(FileCategory.POSTING_RULE);
        item.setScope("S1");
        item.setKey("K2");
        item.setSourcePath("path.json");
        item.setPayload("payload");

        assertEquals(Action.UPDATE, item.getAction());
        assertEquals(FileCategory.POSTING_RULE, item.getFileCategory());
        assertEquals("S1", item.getScope());
        assertEquals("K2", item.getKey());
        assertEquals("path.json", item.getSourcePath());
        assertEquals("payload", item.getPayload());
    }

    @Test
    void masterPlanTracksItemsAndCanReplaceList() {
        MasterPlan masterPlan = new MasterPlan();
        PlanItem first = new PlanItem(Action.NEW, FileCategory.SIDE, "S", "K1", "a.json", null);
        masterPlan.addItem(first);

        assertEquals(List.of(first), masterPlan.getItems());

        PlanItem second = new PlanItem(Action.DELETE, FileCategory.ABOR, "S", "K2", "b.json", null);
        ArrayList<PlanItem> replacement = new ArrayList<>();
        replacement.add(second);

        masterPlan.setItems(replacement);

        assertSame(replacement, masterPlan.getItems());
        assertEquals(List.of(second), masterPlan.getItems());
    }
}
