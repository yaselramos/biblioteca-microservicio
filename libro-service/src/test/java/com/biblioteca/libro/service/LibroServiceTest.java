package com.biblioteca.libro.service;

import com.biblioteca.libro.dto.LibroDto;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
        LibroDto dto = new LibroDto(1L, "Cien años de soledad", "Gabriel García Márquez", 5);
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        // When
        Libro resultado = libroService.guardar(dto);

        // Then
        assertNotNull(resultado);
        assertEquals("Cien años de soledad", resultado.getTitulo());
        assertEquals(5, resultado.getStock());
        verify(libroRepository, times(1)).save(any(Libro.class));
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
        LibroDto libroActualizado = new LibroDto(1L, "Título Actualizado", "Autor Actualizado", 10);

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

    @Test
    void actualizar_DeberiaActualizarSoloCamposNoNulos() {
        // Test cada campo
        when(libroRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Solo titulo
        when(libroRepository.findById(1L)).thenReturn(Optional.of(new Libro(1L, "T", "A", 1)));
        assertEquals("newT", libroService.actualizar(1L, new LibroDto(1L, "newT", null, null)).getTitulo());
        
        // Solo autor
        when(libroRepository.findById(1L)).thenReturn(Optional.of(new Libro(1L, "T", "A", 1)));
        assertEquals("newA", libroService.actualizar(1L, new LibroDto(1L, null, "newA", null)).getAutor());
        
        // Solo stock
        when(libroRepository.findById(1L)).thenReturn(Optional.of(new Libro(1L, "T", "A", 1)));
        assertEquals(99, libroService.actualizar(1L, new LibroDto(1L, null, null, 99)).getStock());
    }

    @Test
    void actualizar_DeberiaRetornarNullSiNoExiste() {
        when(libroRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(libroService.actualizar(1L, new LibroDto(1L, "T", "A", 1)));
    }

    @Test
    void eliminar_DeberiaRetornarFalseSiNoExiste() {
        when(libroRepository.existsById(1L)).thenReturn(false);
        assertFalse(libroService.eliminar(1L));
    }

    @Test
    void buscar_DeberiaLlamarRepo() {
        when(libroRepository.buscarPorTituloOAutor(anyString(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        libroService.buscar("query", PageRequest.of(0, 10));
        verify(libroRepository).buscarPorTituloOAutor("query", PageRequest.of(0, 10));
    }

    @Test
    void obtenerLibrosDisponibles_DeberiaLlamarRepo() {
        when(libroRepository.findLibrosDisponibles(any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        libroService.obtenerLibrosDisponibles(PageRequest.of(0, 10));
        verify(libroRepository).findLibrosDisponibles(PageRequest.of(0, 10));
    }

    @Test
    void verificarStock_DeberiaRetornarFalseSiNoExiste() {
        when(libroRepository.findById(1L)).thenReturn(Optional.empty());
        assertFalse(libroService.verificarStock(1L));
    }
}

