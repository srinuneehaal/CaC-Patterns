package com.example.cacex.service.apply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Summary of a full apply run, including each processed item's outcome.
 */
public class ApplyPlanReport {

    private final List<ApplyPlanItemResult> itemResults = new ArrayList<>();

    public void add(ApplyPlanItemResult itemResult) {
        if (itemResult != null) {
            itemResults.add(itemResult);
        }
    }

    public List<ApplyPlanItemResult> getItemResults() {
        return Collections.unmodifiableList(itemResults);
    }

    public int getTotalCount() {
        return itemResults.size();
    }

    public int getSuccessCount() {
        return (int) itemResults.stream().filter(ApplyPlanItemResult::success).count();
    }

    public int getFailureCount() {
        return getTotalCount() - getSuccessCount();
    }

    public boolean hasFailures() {
        return getFailureCount() > 0;
    }

    public String summary() {
        StringBuilder builder = new StringBuilder();
        builder.append("Apply summary: status=")
                .append(hasFailures() ? "FAILURE" : "SUCCESS")
                .append(", total=")
                .append(getTotalCount())
                .append(", succeeded=")
                .append(getSuccessCount())
                .append(", failed=")
                .append(getFailureCount());

        if (itemResults.isEmpty()) {
            builder.append(System.lineSeparator()).append("No plan items were processed.");
            return builder.toString();
        }

        appendSection(builder, "Successful items", successfulItems());
        appendSection(builder, "Failed items", failedItems());
        return builder.toString();
    }

    private List<ApplyPlanItemResult> successfulItems() {
        return resultsByStatus(true);
    }

    private List<ApplyPlanItemResult> failedItems() {
        return resultsByStatus(false);
    }

    private List<ApplyPlanItemResult> resultsByStatus(boolean success) {
        return itemResults.stream()
                .filter(itemResult -> itemResult.success() == success)
                .collect(Collectors.toList());
    }

    private void appendSection(StringBuilder builder, String heading, List<ApplyPlanItemResult> results) {
        builder.append(System.lineSeparator())
                .append(heading)
                .append(" (")
                .append(results.size())
                .append("):");

        if (results.isEmpty()) {
            builder.append(System.lineSeparator()).append("None.");
            return;
        }

        for (ApplyPlanItemResult itemResult : results) {
            builder.append(System.lineSeparator()).append(itemResult.summaryLine());
        }
    }
}
