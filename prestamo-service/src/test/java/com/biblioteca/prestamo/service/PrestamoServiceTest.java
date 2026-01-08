package com.biblioteca.prestamo.service;

import com.biblioteca.prestamo.dto.LibroDTO;
import com.biblioteca.prestamo.dto.PrestamoEvent;
import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.exception.ResourceNotFoundException;
import com.biblioteca.prestamo.messaging.PrestamoEventPublisher;
import com.biblioteca.prestamo.repository.PrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private PrestamoEventPublisher eventPublisher;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
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

        libroDTO = new LibroDTO();
        libroDTO.setId(1L);
        libroDTO.setTitulo("Cien años de soledad");
        libroDTO.setAutor("Gabriel García Márquez");
        libroDTO.setStock(5);
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
        doNothing().when(eventPublisher).publishPrestamoEvent(any(PrestamoEvent.class));

        // When
        Prestamo resultado = prestamoService.prestarLibro(username, libroId);

        // Then
        assertNotNull(resultado);
        assertEquals(username, resultado.getUsername());
        assertEquals(libroId, resultado.getLibroId());
        assertFalse(resultado.isDevuelto());
        verify(prestamoRepository, times(1)).save(any(Prestamo.class));
        verify(eventPublisher, times(1)).publishPrestamoEvent(any(PrestamoEvent.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el libro no existe")
    void deberiaLanzarExcepcionCuandoLibroNoExiste() {
        // Given
        String username = "testuser";
        Long libroId = 999L;

        when(restTemplate.getForEntity(anyString(), eq(LibroDTO.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            prestamoService.prestarLibro(username, libroId);
        });

        verify(prestamoRepository, never()).save(any());
        verify(eventPublisher, never()).publishPrestamoEvent(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando no hay stock")
    void deberiaLanzarExcepcionCuandoNoHayStock() {
        // Given
        String username = "testuser";
        Long libroId = 1L;
        libroDTO.setStock(0);

        when(restTemplate.getForEntity(anyString(), eq(LibroDTO.class)))
                .thenReturn(new ResponseEntity<>(libroDTO, HttpStatus.OK));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            prestamoService.prestarLibro(username, libroId);
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
        doNothing().when(eventPublisher).publishPrestamoEvent(any(PrestamoEvent.class));

        // When
        Prestamo resultado = prestamoService.devolverLibro(1L);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isDevuelto());
        assertNotNull(resultado.getFechaDevolucion());
        verify(prestamoRepository, times(1)).save(prestamo);
        verify(eventPublisher, times(1)).publishPrestamoEvent(any(PrestamoEvent.class));
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

