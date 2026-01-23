package com.biblioteca.libro.controller;

import com.biblioteca.libro.entity.Libro;
import com.biblioteca.libro.service.LibroService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para LibroController usando Mockito puro
 * Sin dependencia de Spring Security Test ni WebMvcTest
 */
@ExtendWith(MockitoExtension.class)
class LibroControllerTest {

    @Mock
    private LibroService libroService;

    @InjectMocks
    private LibroController libroController;

    private Libro libro;

    @BeforeEach
    void setUp() {
        libro = new Libro("Java Efectivo", "Joshua Bloch", 5);
        libro.setId(1L);
    }

    @Test
    void crear_deberiaRetornar201YLibroCreado() {
        // Arrange
        Libro libroNuevo = new Libro("Java Efectivo", "Joshua Bloch", 5);
        when(libroService.guardar(any(Libro.class))).thenReturn(libro);

        // Act
        ResponseEntity<Libro> response = libroController.crear(libroNuevo);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Java Efectivo", response.getBody().getTitulo());
        assertEquals("Joshua Bloch", response.getBody().getAutor());
        assertEquals(5, response.getBody().getStock());

        verify(libroService, times(1)).guardar(any(Libro.class));
    }

    @Test
    void listar_deberiaRetornar200YListaDeLibros() {
        // Arrange
        Libro libro2 = new Libro("Spring Boot en Accion", "Craig Walls", 3);
        libro2.setId(2L);

        when(libroService.listar()).thenReturn(Arrays.asList(libro, libro2));

        // Act
        ResponseEntity<List<Libro>> response = libroController.listar();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Java Efectivo", response.getBody().get(0).getTitulo());
        assertEquals("Spring Boot en Accion", response.getBody().get(1).getTitulo());

        verify(libroService, times(1)).listar();
    }

    @Test
    void listarPaginado_deberiaRetornar200YPaginaDeLibros() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroService.listarPaginado(any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Libro>> response = libroController.listarPaginado(0, 10, "titulo", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Java Efectivo", response.getBody().getContent().get(0).getTitulo());

        verify(libroService, times(1)).listarPaginado(any(Pageable.class));
    }

    @Test
    void buscarPorId_existente_deberiaRetornar200YLibro() {
        // Arrange
        when(libroService.buscarPorId(1L)).thenReturn(Optional.of(libro));

        // Act
        ResponseEntity<Libro> response = libroController.buscarPorId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Java Efectivo", response.getBody().getTitulo());

        verify(libroService, times(1)).buscarPorId(1L);
    }

    @Test
    void buscarPorId_noExistente_deberiaRetornar404() {
        // Arrange
        when(libroService.buscarPorId(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Libro> response = libroController.buscarPorId(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(libroService, times(1)).buscarPorId(999L);
    }

    @Test
    void actualizar_existente_deberiaRetornar200YLibroActualizado() {
        // Arrange
        Libro libroActualizado = new Libro("Java Efectivo 2da Edicion", "Joshua Bloch", 8);
        libroActualizado.setId(1L);

        when(libroService.actualizar(eq(1L), any(Libro.class))).thenReturn(libroActualizado);

        // Act
        ResponseEntity<Libro> response = libroController.actualizar(1L, libroActualizado);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Java Efectivo 2da Edicion", response.getBody().getTitulo());
        assertEquals(8, response.getBody().getStock());

        verify(libroService, times(1)).actualizar(eq(1L), any(Libro.class));
    }

    @Test
    void actualizar_noExistente_deberiaRetornar404() {
        // Arrange
        Libro libroNoExistente = new Libro("Libro No Existente", "Autor", 1);
        when(libroService.actualizar(eq(999L), any(Libro.class))).thenReturn(null);

        // Act
        ResponseEntity<Libro> response = libroController.actualizar(999L, libroNoExistente);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(libroService, times(1)).actualizar(eq(999L), any(Libro.class));
    }

    @Test
    void eliminar_existente_deberiaRetornar204() {
        // Arrange
        when(libroService.eliminar(1L)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = libroController.eliminar(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(libroService, times(1)).eliminar(1L);
    }

    @Test
    void eliminar_noExistente_deberiaRetornar404() {
        // Arrange
        when(libroService.eliminar(999L)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = libroController.eliminar(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(libroService, times(1)).eliminar(999L);
    }

    @Test
    void buscarPorTitulo_deberiaRetornar200YPaginaDeLibros() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroService.buscarPorTitulo(eq("Java"), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Libro>> response = libroController.buscarPorTitulo("Java", 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().get(0).getTitulo().contains("Java"));

        verify(libroService, times(1)).buscarPorTitulo(eq("Java"), any(Pageable.class));
    }

    @Test
    void buscarPorAutor_deberiaRetornar200YPaginaDeLibros() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroService.buscarPorAutor(eq("Joshua"), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Libro>> response = libroController.buscarPorAutor("Joshua", 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().get(0).getAutor().contains("Joshua"));

        verify(libroService, times(1)).buscarPorAutor(eq("Joshua"), any(Pageable.class));
    }

    @Test
    void obtenerDisponibles_deberiaRetornar200YLibrosConStock() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroService.obtenerLibrosDisponibles(any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Libro>> response = libroController.obtenerDisponibles(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().get(0).getStock() > 0);

        verify(libroService, times(1)).obtenerLibrosDisponibles(any(Pageable.class));
    }
}

