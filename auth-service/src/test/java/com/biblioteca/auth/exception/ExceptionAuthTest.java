package com.biblioteca.auth.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionAuthTest {

    @Test
    void testResourceNotFoundAuth() {
        ResourceNotFoundExceptionAuth ex = new ResourceNotFoundExceptionAuth("Error");
        assertEquals("Error", ex.getMessage());
        
        ResourceNotFoundExceptionAuth ex2 = new ResourceNotFoundExceptionAuth("Error", new RuntimeException("Causa"));
        assertEquals("Error", ex2.getMessage());
        assertNotNull(ex2.getCause());
    }

    @Test
    void testGlobalExceptionHandlerAuth() {
        GlobalExceptionHandlerAuth handler = new GlobalExceptionHandlerAuth();
        assertNotNull(handler);
    }
}
