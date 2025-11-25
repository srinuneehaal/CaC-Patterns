package com.example.cacex.exception;

/**
 * Thrown when no parsing strategy supports a given path.
 */
public class UnsupportedFilePathException extends RuntimeException {

    public UnsupportedFilePathException(String message) {
        super(message);
    }
}
