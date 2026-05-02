package com.example.cacex.service.apply;

import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.PlanItem;

/**
 * Captures the outcome of applying a single {@link PlanItem}.
 */
public record ApplyPlanItemResult(
        int index,
        Action action,
        FileCategory fileCategory,
        String scope,
        String key,
        String sourcePath,
        boolean success,
        String message
) {

    private static final String DEFAULT_SUCCESS_MESSAGE = "Applied item and updated state successfully.";

    public static ApplyPlanItemResult success(int index, PlanItem item) {
        return new ApplyPlanItemResult(
                index,
                item == null ? null : item.getAction(),
                item == null ? null : item.getFileCategory(),
                item == null ? null : item.getScope(),
                item == null ? null : item.getKey(),
                item == null ? null : item.getSourcePath(),
                true,
                DEFAULT_SUCCESS_MESSAGE
        );
    }

    public static ApplyPlanItemResult failure(int index, PlanItem item, String message) {
        return new ApplyPlanItemResult(
                index,
                item == null ? null : item.getAction(),
                item == null ? null : item.getFileCategory(),
                item == null ? null : item.getScope(),
                item == null ? null : item.getKey(),
                item == null ? null : item.getSourcePath(),
                false,
                normalizeMessage(message)
        );
    }

    public String status() {
        return success ? "SUCCESS" : "FAILURE";
    }

    public String summaryLine() {
        return index + ". "
                + status()
                + " "
                + valueOrUnknown(fileCategory)
                + " "
                + valueOrUnknown(action)
                + " "
                + target()
                + " - "
                + normalizeMessage(message);
    }

    private String target() {
        return "[scope="
                + valueOrPlaceholder(scope)
                + ", key="
                + valueOrPlaceholder(key)
                + ", source="
                + valueOrPlaceholder(sourcePath)
                + "]";
    }

    private static String valueOrUnknown(Object value) {
        return value == null ? "UNKNOWN" : value.toString();
    }

    private static String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "<blank>" : value;
    }

    private static String normalizeMessage(String message) {
        return message == null || message.isBlank() ? "No message available." : message;
    }
}
