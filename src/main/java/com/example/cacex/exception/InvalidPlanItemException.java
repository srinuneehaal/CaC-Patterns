package com.example.cacex.exception;

/**
 * Thrown when a plan item is missing required data or contains invalid payload.
 */
public class InvalidPlanItemException extends PlanApplyException {

    /**
     * Creates an exception for an invalid plan item.
     *
     * @param message description of the validation failure
     */
    public InvalidPlanItemException(String message) {
        super(message);
    }
}
