package com.biblioteca.libro.service;

import com.biblioteca.libro.entity.Libro;
import com.biblioteca.libro.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para LibroService")
class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    private Libro libro;

    @BeforeEach
    void setUp() {
        libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Cien años de soledad");
        libro.setAutor("Gabriel García Márquez");
        libro.setStock(5);
    }

    @Test
    @DisplayName("Debería guardar un libro exitosamente")
    void deberiaGuardarLibroExitosamente() {
        // Given
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        // When
        Libro resultado = libroService.guardar(libro);

        // Then
        assertNotNull(resultado);
        assertEquals("Cien años de soledad", resultado.getTitulo());
        assertEquals(5, resultado.getStock());
        verify(libroRepository, times(1)).save(libro);
    }

    @Test
    @DisplayName("Debería listar todos los libros")
    void deberiaListarTodosLosLibros() {
        // Given
        Libro libro2 = new Libro();
        libro2.setId(2L);
        libro2.setTitulo("El amor en los tiempos del cólera");
        libro2.setAutor("Gabriel García Márquez");
        libro2.setStock(3);

        List<Libro> libros = Arrays.asList(libro, libro2);
        when(libroRepository.findAll()).thenReturn(libros);

        // When
        List<Libro> resultado = libroService.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(libroRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar libro por ID exitosamente")
    void deberiaBuscarLibroPorId() {
        // Given
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

        // When
        Optional<Libro> resultado = libroService.buscarPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Cien años de soledad", resultado.get().getTitulo());
        verify(libroRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería decrementar stock exitosamente")
    void deberiaDecrementarStockExitosamente() {
        // Given
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        // When
        libroService.decrementarStock(1L);

        // Then
        assertEquals(4, libro.getStock());
        verify(libroRepository, times(1)).findById(1L);
        verify(libroRepository, times(1)).save(libro);
    }

    @Test
    @DisplayName("Debería lanzar excepción al decrementar stock sin disponibilidad")
    void deberiaLanzarExcepcionAlDecrementarStockSinDisponibilidad() {
        // Given
        libro.setStock(0);
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            libroService.decrementarStock(1L);
        });

        assertTrue(exception.getMessage().contains("No hay stock disponible"));
        verify(libroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería incrementar stock exitosamente")
    void deberiaIncrementarStockExitosamente() {
        // Given
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        // When
        libroService.incrementarStock(1L);

        // Then
        assertEquals(6, libro.getStock());
        verify(libroRepository, times(1)).findById(1L);
        verify(libroRepository, times(1)).save(libro);
    }

    @Test
    @DisplayName("Debería verificar stock disponible")
    void deberiaVerificarStockDisponible() {
        // Given
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

        // When
        boolean tieneStock = libroService.verificarStock(1L);

        // Then
        assertTrue(tieneStock);
        verify(libroRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería retornar false cuando no hay stock")
    void deberiaRetornarFalseCuandoNoHayStock() {
        // Given
        libro.setStock(0);
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

        // When
        boolean tieneStock = libroService.verificarStock(1L);

        // Then
        assertFalse(tieneStock);
    }

    @Test
    @DisplayName("Debería actualizar libro exitosamente")
    void deberiaActualizarLibroExitosamente() {
        // Given
        Libro libroActualizado = new Libro();
        libroActualizado.setTitulo("Título Actualizado");
        libroActualizado.setAutor("Autor Actualizado");
        libroActualizado.setStock(10);

        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        // When
        Libro resultado = libroService.actualizar(1L, libroActualizado);

        // Then
        assertNotNull(resultado);
        assertEquals("Título Actualizado", resultado.getTitulo());
        assertEquals("Autor Actualizado", resultado.getAutor());
        assertEquals(10, resultado.getStock());
        verify(libroRepository, times(1)).save(libro);
    }

    @Test
    @DisplayName("Debería eliminar libro exitosamente")
    void deberiaEliminarLibroExitosamente() {
        // Given
        when(libroRepository.existsById(1L)).thenReturn(true);
        doNothing().when(libroRepository).deleteById(1L);

        // When
        boolean resultado = libroService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(libroRepository, times(1)).deleteById(1L);
    }
}

