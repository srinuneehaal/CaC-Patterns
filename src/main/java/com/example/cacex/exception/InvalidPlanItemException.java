package com.example.cacex.exception;

/**
 * Thrown when a plan item is missing required data or contains invalid payload.
 */
public class InvalidPlanItemException extends PlanApplyException {

    public InvalidPlanItemException(String message) {
        super(message);
    }
}
