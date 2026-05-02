package com.example.cacex.service.apply;

import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.PlanItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplyPlanReportTest {

    @Test
    void summaryGroupsSuccessfulAndFailedItems() {
        ApplyPlanReport report = new ApplyPlanReport();
        PlanItem successItem = new PlanItem(
                Action.NEW,
                FileCategory.SIDE,
                "scope-a",
                "side-1",
                "changedfiles/scope-a/sides/side-1.json",
                null
        );
        PlanItem failureItem = new PlanItem(
                Action.UPDATE,
                FileCategory.TRANSACTION,
                "scope-a",
                "txn-1",
                "changedfiles/scope-a/transactions/txn-1.json",
                null
        );

        report.add(ApplyPlanItemResult.success(1, successItem));
        report.add(ApplyPlanItemResult.failure(2, failureItem, "Upstream API rejected the update."));

        String summary = report.summary();

        assertTrue(summary.contains("Apply summary: status=FAILURE, total=2, succeeded=1, failed=1"));
        assertTrue(summary.contains("Successful items (1):"));
        assertTrue(summary.contains("Failed items (1):"));
        assertTrue(summary.contains("source=changedfiles/scope-a/sides/side-1.json"));
        assertTrue(summary.contains("Applied item and updated state successfully."));
        assertTrue(summary.contains("source=changedfiles/scope-a/transactions/txn-1.json"));
        assertTrue(summary.contains("Upstream API rejected the update."));
    }

    @Test
    void summaryShowsEmptySectionsWhenNoItemsProcessed() {
        ApplyPlanReport report = new ApplyPlanReport();

        String summary = report.summary();

        assertTrue(summary.contains("Apply summary: status=SUCCESS, total=0, succeeded=0, failed=0"));
        assertTrue(summary.contains("No plan items were processed."));
        assertFalse(summary.contains("Successful items ("));
        assertFalse(summary.contains("Failed items ("));
    }
}
