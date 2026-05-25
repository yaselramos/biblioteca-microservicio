package com.biblioteca.libro.controller;

import com.biblioteca.libro.dto.LibroDto;
import com.biblioteca.libro.entity.Libro;
import com.biblioteca.libro.repository.LibroRepository;
import com.biblioteca.libro.service.LibroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para LibroController usando Mockito puro
 * Sin dependencia de Spring Security Test ni WebMvcTest
 */
@ExtendWith(MockitoExtension.class)
class LibroControllerTest {

    @Mock
    private LibroRepository libroRepository;

    private LibroService libroService;
    private LibroController libroController;

    private Libro libro;

    @BeforeEach
    void setUp() {
        libro = new Libro();
        libro.setTitulo("Java Efectivo");
        libro.setAutor("Joshua Bloch");
libro.setStock(5);

        libro.setId(1L);

        // Creamos un servicio real que usará el repo mockeado
        libroService = new LibroService(libroRepository);
        // Creamos el controller con el servicio real
        libroController = new LibroController(libroService);
    }

    @Test
    @Order(1)
    void crear_deberiaRetornar201YLibroCreado() {
        // Arrange
        LibroDto libroNuevo = new LibroDto(null, "Java Efectivo", "Joshua Bloch", 5);
      when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        // Act
        ResponseEntity<Libro> response = libroController.crear(libroNuevo);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Java Efectivo", response.getBody().getTitulo());
        assertEquals("Joshua Bloch", response.getBody().getAutor());
        assertEquals(5, response.getBody().getStock());

        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    @Order(3)
    void listarPaginado_deberiaRetornar200YPaginaDeLibros() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Libro>> response = libroController.listarPaginado(0, 10, "titulo", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Java Efectivo", response.getBody().getContent().get(0).getTitulo());

        verify(libroRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Order(4)
    void buscarPorId_existente_deberiaRetornar200YLibro() {
        // Arrange
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

        // Act
        ResponseEntity<Libro> response = libroController.buscarPorId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Java Efectivo", response.getBody().getTitulo());

        verify(libroRepository, times(1)).findById(1L);
    }

    @Test
    @Order(5)
    void buscarPorId_noExistente_deberiaRetornar404() {
        // Arrange
        when(libroRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Libro> response = libroController.buscarPorId(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(libroRepository, times(1)).findById(999L);
    }

    @Test
    @Order(6)
    void actualizar_existente_deberiaRetornar200YLibroActualizado() {
        // Arrange
        LibroDto libroActualizado = new LibroDto(1L, "Java Efectivo 2da Edicion", "Joshua Bloch", 8);
        Libro libroPersistido = new Libro();
        libroPersistido.setTitulo("Java Efectivo 2da Edicion");
        libroPersistido.setAutor("Joshua Bloch");
        libroPersistido.setStock(8);
        libroPersistido.setId(1L);

        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(libroRepository.save(any(Libro.class))).thenReturn(libroPersistido);

        // Act
        ResponseEntity<Libro> response = libroController.actualizar(1L, libroActualizado);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Java Efectivo 2da Edicion", response.getBody().getTitulo());
        assertEquals(8, response.getBody().getStock());

        verify(libroRepository, times(1)).findById(1L);
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    @Order(7)
    void actualizar_noExistente_deberiaRetornar404() {
        // Arrange
        LibroDto libroNoExistente = new LibroDto(null, "Libro No Existente", "Autor", 1);
        when(libroRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Libro> response = libroController.actualizar(999L, libroNoExistente);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(libroRepository, times(1)).findById(999L);
    }

    @Test
    @Order(8)
    void eliminar_existente_deberiaRetornar204() {
        // Arrange
        when(libroRepository.existsById(1L)).thenReturn(true);
        doNothing().when(libroRepository).deleteById(1L);

        // Act
        ResponseEntity<Void> response = libroController.eliminar(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(libroRepository, times(1)).existsById(1L);
        verify(libroRepository, times(1)).deleteById(1L);
    }

    @Test
    @Order(9)
    void eliminar_noExistente_deberiaRetornar404() {
        // Arrange
        when(libroRepository.existsById(999L)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = libroController.eliminar(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(libroRepository, times(1)).existsById(999L);
    }

    @Test
    @Order(10)
    void buscarPorTitulo_deberiaRetornar200YPaginaDeLibros() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroRepository.findByTituloContainingIgnoreCase(eq("Java"), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Libro>> response = libroController.buscarPorTitulo("Java", 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().get(0).getTitulo().contains("Java"));

        verify(libroRepository, times(1)).findByTituloContainingIgnoreCase(eq("Java"), any(Pageable.class));
    }

    @Test
    @Order(11)
    void buscarPorAutor_deberiaRetornar200YPaginaDeLibros() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroRepository.findByAutorContainingIgnoreCase(eq("Joshua"), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Libro>> response = libroController.buscarPorAutor("Joshua", 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().get(0).getAutor().contains("Joshua"));

        verify(libroRepository, times(1)).findByAutorContainingIgnoreCase(eq("Joshua"), any(Pageable.class));
    }

}