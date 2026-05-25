package com.biblioteca.auth.exception;

/**
 * ResourceNotFoundException para Auth Service que delega a la implementación común.
 */
public class ResourceNotFoundExceptionAuth extends com.biblioteca.common.exception.ResourceNotFoundException {
    public ResourceNotFoundExceptionAuth(String message) {
        super(message);
    }

    public ResourceNotFoundExceptionAuth(String message, Throwable cause) {
        super(message, cause);
    }
}

