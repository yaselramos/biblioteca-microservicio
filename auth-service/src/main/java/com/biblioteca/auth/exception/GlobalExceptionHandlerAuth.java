package com.biblioteca.auth.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler para Auth Service que delega a la implementación común.
 */
@RestControllerAdvice
public class GlobalExceptionHandlerAuth extends com.biblioteca.common.exception.GlobalExceptionHandler {
}

