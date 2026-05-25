package com.biblioteca.prestamo.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Error de negocio";
        BusinessException exception = new BusinessException(message);
        assertEquals(message, exception.getMessage());
    }
}
