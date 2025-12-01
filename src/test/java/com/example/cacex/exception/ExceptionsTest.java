package com.example.cacex.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ExceptionsTest {

    @Test
    void planApplyExceptionCarriesMessageAndCause() {
        Throwable cause = new IllegalStateException("root");
        PlanApplyException ex = new PlanApplyException("apply failed", cause);
        assertEquals("apply failed", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void invalidPlanItemExceptionIsSubclassOfPlanApplyException() {
        InvalidPlanItemException ex = new InvalidPlanItemException("bad item");
        assertEquals("bad item", ex.getMessage());
        assertEquals(PlanApplyException.class, ex.getClass().getSuperclass());
    }

    @Test
    void missingApplierExceptionIsSubclassOfPlanApplyException() {
        MissingApplierException ex = new MissingApplierException("no applier");
        assertEquals("no applier", ex.getMessage());
        assertEquals(PlanApplyException.class, ex.getClass().getSuperclass());
    }

    @Test
    void planProcessingExceptionCarriesMessageAndCause() {
        Throwable cause = new RuntimeException("cause");
        PlanProcessingException ex = new PlanProcessingException("process failed", cause);
        assertEquals("process failed", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void planProcessingExceptionSupportsMessageOnly() {
        PlanProcessingException ex = new PlanProcessingException("message-only");
        assertEquals("message-only", ex.getMessage());
    }

    @Test
    void unsupportedFileCategoryExceptionCarriesMessage() {
        UnsupportedFileCategoryException ex = new UnsupportedFileCategoryException("bad category");
        assertEquals("bad category", ex.getMessage());
    }

    @Test
    void unsupportedFilePathExceptionCarriesMessage() {
        UnsupportedFilePathException ex = new UnsupportedFilePathException("bad path");
        assertEquals("bad path", ex.getMessage());
    }
}
