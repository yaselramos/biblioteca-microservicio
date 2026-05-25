package com.biblioteca.common.exception;

import com.biblioteca.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleResourceNotFound_DeberiaRetornar404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso no encontrado");
        
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("Recurso no encontrado", response.getBody().message());
        assertEquals("/api/test", response.getBody().path());
    }

    @Test
    void handleIllegalState_DeberiaRetornar400() {
        IllegalStateException ex = new IllegalStateException("Estado ilegal");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalState(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Estado ilegal", response.getBody().message());
    }

    @Test
    void handleGenericException_DeberiaRetornar500() {
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<ErrorResponse> response = handler.handleGenericError(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().error());
        assertTrue(response.getBody().message().contains("Error inesperado"));
    }
}
