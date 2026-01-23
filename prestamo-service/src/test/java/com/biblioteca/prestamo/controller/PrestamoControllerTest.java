package com.biblioteca.prestamo.controller;

import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.service.PrestamoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PrestamoController usando Mockito puro
 * Sin dependencia de Spring Security Test ni WebMvcTest
 */
@ExtendWith(MockitoExtension.class)
class PrestamoControllerTest {

    @Mock
    private PrestamoService prestamoService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PrestamoController prestamoController;

    private Prestamo prestamo;

    @BeforeEach
    void setUp() {
        prestamo = new Prestamo(1L, "testuser", LocalDate.now());
        prestamo.setId(1L);
    }

    @Test
    void prestar_deberiaRetornar201YPrestamoCreado() {
        // Arrange
        when(authentication.getName()).thenReturn("testuser");
        when(prestamoService.prestarLibro("testuser", 1L)).thenReturn(prestamo);

        // Act
        ResponseEntity<Prestamo> response = prestamoController.prestar(1L, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(1L, response.getBody().getLibroId());
        assertFalse(response.getBody().isDevuelto());

        verify(prestamoService, times(1)).prestarLibro("testuser", 1L);
    }

    @Test
    void devolver_deberiaRetornar200YPrestamoActualizado() {
        // Arrange
        prestamo.setDevuelto(true);
        prestamo.setFechaDevolucion(LocalDate.now());
        when(prestamoService.devolverLibro(1L)).thenReturn(prestamo);

        // Act
        ResponseEntity<Prestamo> response = prestamoController.devolver(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isDevuelto());
        assertNotNull(response.getBody().getFechaDevolucion());

        verify(prestamoService, times(1)).devolverLibro(1L);
    }

    @Test
    void misPrestamos_deberiaRetornar200YListaDePrestamos() {
        // Arrange
        Prestamo prestamo2 = new Prestamo(2L, "testuser", LocalDate.now().minusDays(3));
        prestamo2.setId(2L);
        prestamo2.setDevuelto(true);

        when(authentication.getName()).thenReturn("testuser");
        when(prestamoService.obtenerPrestamosUsuario("testuser"))
                .thenReturn(Arrays.asList(prestamo, prestamo2));

        // Act
        ResponseEntity<List<Prestamo>> response = prestamoController.misPrestamos(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).getUsername());

        verify(prestamoService, times(1)).obtenerPrestamosUsuario("testuser");
    }

    @Test
    void misPrestamosPaginados_deberiaRetornar200YPaginaDePrestamos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo), pageable, 1);

        when(authentication.getName()).thenReturn("testuser");
        when(prestamoService.obtenerPrestamosUsuarioPaginados(eq("testuser"), any(Pageable.class)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<Prestamo>> response = prestamoController.misPrestamosPaginados(
                authentication, 0, 10, "fechaPrestamo", "desc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getContent().size());

        verify(prestamoService, times(1)).obtenerPrestamosUsuarioPaginados(eq("testuser"), any(Pageable.class));
    }

    @Test
    void misPrestamosActivos_deberiaRetornar200YPrestamosActivos() {
        // Arrange
        when(authentication.getName()).thenReturn("testuser");
        when(prestamoService.obtenerPrestamosActivos("testuser"))
                .thenReturn(Collections.singletonList(prestamo));

        // Act
        ResponseEntity<List<Prestamo>> response = prestamoController.misPrestamosActivos(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertFalse(response.getBody().get(0).isDevuelto());

        verify(prestamoService, times(1)).obtenerPrestamosActivos("testuser");
    }

    @Test
    void listarTodos_deberiaRetornar200YListaDeTodosPrestamos() {
        // Arrange
        Prestamo prestamo2 = new Prestamo(2L, "user2", LocalDate.now().minusDays(3));
        prestamo2.setId(2L);

        when(prestamoService.listarTodos()).thenReturn(Arrays.asList(prestamo, prestamo2));

        // Act
        ResponseEntity<List<Prestamo>> response = prestamoController.listarTodos();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(prestamoService, times(1)).listarTodos();
    }

    @Test
    void listarTodosPaginados_deberiaRetornar200YPaginaDeTodosPrestamos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo), pageable, 1);

        when(prestamoService.obtenerPrestamosPaginados(any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Prestamo>> response = prestamoController.listarTodosPaginados(
                0, 10, "fechaPrestamo", "desc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());

        verify(prestamoService, times(1)).obtenerPrestamosPaginados(any(Pageable.class));
    }

    @Test
    void prestamosPorLibro_deberiaRetornar200YPaginaDePrestamos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo), pageable, 1);

        when(prestamoService.obtenerPrestamosPorLibro(eq(1L), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Prestamo>> response = prestamoController.prestamosPorLibro(1L, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1L, response.getBody().getContent().get(0).getLibroId());

        verify(prestamoService, times(1)).obtenerPrestamosPorLibro(eq(1L), any(Pageable.class));
    }
}

