package com.example.cacex.exception;

/**
 * Generic wrapper for errors encountered while building or processing plans.
 */
public class PlanProcessingException extends RuntimeException {

    public PlanProcessingException(String message) {
        super(message);
    }

    public PlanProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
