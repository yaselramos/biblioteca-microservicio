package com.biblioteca.prestamo.service;

import com.biblioteca.common.exception.ResourceNotFoundException;
import com.biblioteca.prestamo.dto.LibroDTO;
import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.messaging.PrestamoEventPublisher;
import com.biblioteca.prestamo.repository.PrestamoRepository;
import com.biblioteca.prestamo.util.TestPrestamoEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para PrestamoService")
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private RestTemplate restTemplate;

    private PrestamoEventPublisher eventPublisher;
    private PrestamoService prestamoService;

    private Prestamo prestamo;
    private LibroDTO libroDTO;

    @BeforeEach
    void setUp() {
        prestamo = new Prestamo();
        prestamo.setId(1L);
        prestamo.setLibroId(1L);
        prestamo.setUsername("testuser");
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setDevuelto(false);

        libroDTO = new LibroDTO(1L,"Cien años de soledad","Gabriel García Márquez",5);

        // Create test-only event publisher (no mocking required)
        eventPublisher = new TestPrestamoEventPublisher();

        // Create real PrestamoService with mocked dependencies
        prestamoService = new PrestamoService(prestamoRepository, eventPublisher, restTemplate);
    }

    @Test
    @DisplayName("Debería crear préstamo exitosamente")
    void deberiaCrearPrestamoExitosamente() {
        // Given
        String username = "testuser";
        Long libroId = 1L;

        when(restTemplate.getForEntity(anyString(), eq(LibroDTO.class)))
                .thenReturn(new ResponseEntity<>(libroDTO, HttpStatus.OK));
        when(prestamoRepository.findByUsernameAndDevueltoFalse(username))
                .thenReturn(Arrays.asList());
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamo);

        // When
        Prestamo resultado = prestamoService.prestarLibro(username, libroId);

        // Then
        assertNotNull(resultado);
        assertEquals(username, resultado.getUsername());
        assertEquals(libroId, resultado.getLibroId());
        assertFalse(resultado.isDevuelto());
        verify(prestamoRepository, times(1)).save(any(Prestamo.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el libro no existe (NotFound exception)")
    void deberiaLanzarExcepcionCuandoLibroNoExisteNotFound() {
        when(restTemplate.getForEntity(anyString(), eq(LibroDTO.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

        assertThrows(ResourceNotFoundException.class, () -> prestamoService.prestarLibro("user", 1L));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el libro no existe (BadRequest genérico)")
    void deberiaLanzarExcepcionCuandoLibroNoExisteBadRequest() {
        when(restTemplate.getForEntity(anyString(), eq(LibroDTO.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        assertThrows(HttpClientErrorException.class, () -> prestamoService.prestarLibro("user", 1L));
    }

    @Test
    void devolverLibro_DeberiaLanzarExcepcionSiYaFueDevuelto() {
        Prestamo p = new Prestamo();
        p.setDevuelto(true);
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(IllegalStateException.class, () -> prestamoService.devolverLibro(1L));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el body de respuesta es nulo")
    void deberiaLanzarExcepcionCuandoBodyEsNulo() {
        when(restTemplate.getForEntity(anyString(), eq(LibroDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(ResourceNotFoundException.class, () -> prestamoService.prestarLibro("user", 1L));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando no hay stock")
    void deberiaLanzarExcepcionCuandoNoHayStock() {
        // Given
        String username = "testuser";

        libroDTO=new LibroDTO(1L,null,null,0);

        when(restTemplate.getForEntity(anyString(), eq(LibroDTO.class)))
                .thenReturn(new ResponseEntity<>(libroDTO, HttpStatus.OK));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            prestamoService.prestarLibro(username, libroDTO.id());
        });

        assertTrue(exception.getMessage().contains("No hay stock disponible"));
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando usuario ya tiene préstamo activo")
    void deberiaLanzarExcepcionCuandoUsuarioTienePrestamoActivo() {
        // Given
        String username = "testuser";
        Long libroId = 1L;

        Prestamo prestamoExistente = new Prestamo();
        prestamoExistente.setLibroId(1L);
        prestamoExistente.setUsername(username);
        prestamoExistente.setDevuelto(false);

        when(restTemplate.getForEntity(anyString(), eq(LibroDTO.class)))
                .thenReturn(new ResponseEntity<>(libroDTO, HttpStatus.OK));
        when(prestamoRepository.findByUsernameAndDevueltoFalse(username))
                .thenReturn(Arrays.asList(prestamoExistente));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            prestamoService.prestarLibro(username, libroId);
        });

        assertTrue(exception.getMessage().contains("Ya tienes un préstamo activo"));
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería devolver libro exitosamente")
    void deberiaDevolverLibroExitosamente() {
        // Given
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamo);

        // When
        Prestamo resultado = prestamoService.devolverLibro(1L);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isDevuelto());
        assertNotNull(resultado.getFechaDevolucion());
        verify(prestamoRepository, times(1)).save(prestamo);
    }

    @Test
    @DisplayName("Debería lanzar excepción al devolver préstamo inexistente")
    void deberiaLanzarExcepcionAlDevolverPrestamoInexistente() {
        // Given
        when(prestamoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            prestamoService.devolverLibro(999L);
        });

        verify(prestamoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción al devolver libro ya devuelto")
    void deberiaLanzarExcepcionAlDevolverLibroYaDevuelto() {
        // Given
        prestamo.setDevuelto(true);
        prestamo.setFechaDevolucion(LocalDate.now());

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            prestamoService.devolverLibro(1L);
        });

        assertTrue(exception.getMessage().contains("ya fue devuelto"));
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería obtener préstamos de usuario")
    void deberiaObtenerPrestamosDeUsuario() {
        // Given
        String username = "testuser";
        List<Prestamo> prestamos = Arrays.asList(prestamo);

        when(prestamoRepository.findByUsername(username)).thenReturn(prestamos);

        // When
        List<Prestamo> resultado = prestamoService.obtenerPrestamosUsuario(username);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(prestamoRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Debería obtener préstamos activos de usuario")
    void deberiaObtenerPrestamosActivosDeUsuario() {
        // Given
        String username = "testuser";
        List<Prestamo> prestamos = Arrays.asList(prestamo);

        when(prestamoRepository.findByUsernameAndDevueltoFalse(username)).thenReturn(prestamos);

        // When
        List<Prestamo> resultado = prestamoService.obtenerPrestamosActivos(username);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertFalse(resultado.get(0).isDevuelto());
        verify(prestamoRepository, times(1)).findByUsernameAndDevueltoFalse(username);
    }

    @Test
    @DisplayName("Debería listar todos los préstamos")
    void deberiaListarTodosLosPrestamos() {
        // Given
        List<Prestamo> prestamos = Arrays.asList(prestamo);
        when(prestamoRepository.findAll()).thenReturn(prestamos);

        // When
        List<Prestamo> resultado = prestamoService.listarTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(prestamoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar préstamo por ID")
    void deberiaBuscarPrestamoPorId() {
        // Given
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));

        // When
        Optional<Prestamo> resultado = prestamoService.buscarPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(prestamoRepository, times(1)).findById(1L);
    }
}

