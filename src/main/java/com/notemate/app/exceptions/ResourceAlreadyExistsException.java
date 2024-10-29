package com.notemate.app.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super (message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super (message, cause);
    }
}
