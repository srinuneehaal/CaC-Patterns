package com.example.cacex.exception;

/**
 * Thrown when no applier exists for a given file category.
 */
public class MissingApplierException extends PlanApplyException {

    public MissingApplierException(String message) {
        super(message);
    }
}
