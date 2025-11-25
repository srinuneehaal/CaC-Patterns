package com.example.cacex.exception;

/**
 * Thrown when no parsing strategy exists for a requested file category.
 */
public class UnsupportedFileCategoryException extends RuntimeException {

    public UnsupportedFileCategoryException(String message) {
        super(message);
    }
}
