package com.example.cacex.exception;

/**
 * Base exception for failures encountered while applying a plan.
 */
public class PlanApplyException extends RuntimeException {

    public PlanApplyException(String message) {
        super(message);
    }

    public PlanApplyException(String message, Throwable cause) {
        super(message, cause);
    }
}
