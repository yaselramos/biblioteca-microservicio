package com.biblioteca.prestamo.controller;

import com.biblioteca.common.dto.PrestamoEvent;
import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.messaging.PrestamoEventPublisher;
import com.biblioteca.prestamo.repository.PrestamoRepository;
import com.biblioteca.prestamo.service.PrestamoService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private PrestamoRepository prestamoRepository;

    // no necesitamos mockear RabbitTemplate: usaremos un publicador no-op en los tests

    // ya no usamos RestTemplate en los tests unitarios del controller

    private PrestamoService prestamoService;

    private Authentication authentication;

    private PrestamoController prestamoController;

    private Prestamo prestamo;

    @BeforeEach
    void setUp() {
        prestamo = new Prestamo(1L, "testuser", LocalDate.now());
        prestamo.setId(1L);
        // crear servicio real con repositorio e dependencias mockeadas
        // crear un servicio de pruebas que usa el repo mockeado y un publicador no-op
        PrestamoEventPublisher eventPublisher = new NoopEventPublisher();
        prestamoService = new TestPrestamoService(prestamoRepository, eventPublisher);
        prestamoController = new PrestamoController(prestamoService);
        // crear una implementación simple de Authentication para los tests
        authentication = new Authentication() {
            @Override
            public java.util.Collection<org.springframework.security.core.GrantedAuthority> getAuthorities() {
                return java.util.Collections.emptyList();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return "testuser";
            }
        };
    }

    // Publicador no-op para tests: evita interacción con RabbitMQ y no requiere mocks
    static class NoopEventPublisher extends PrestamoEventPublisher {
        public NoopEventPublisher() {
            super(null);
        }

        @Override
        public void publishPrestamoEvent(PrestamoEvent event) {
            // no hace nada
        }
    }

    // Servicio de prueba que evita llamadas externas (RestTemplate) y usa el repo mockeado
    static class TestPrestamoService extends PrestamoService {
        private final PrestamoRepository repo;
        private final PrestamoEventPublisher publisher;

        public TestPrestamoService(PrestamoRepository repo, PrestamoEventPublisher publisher) {
            super(repo, publisher, null);
            this.repo = repo;
            this.publisher = publisher;
        }

        @Override
        public Prestamo prestarLibro(String username, Long libroId) {
            // validar que no tenga préstamo activo
            java.util.List<Prestamo> prestamosActivos = repo.findByUsernameAndDevueltoFalse(username);
            boolean tiene = prestamosActivos.stream().anyMatch(p -> p.getLibroId().equals(libroId));
            if (tiene) throw new IllegalStateException("Ya tienes un préstamo activo de este libro");

            Prestamo prestamo = new Prestamo();
            prestamo.setUsername(username);
            prestamo.setLibroId(libroId);
            prestamo.setFechaPrestamo(java.time.LocalDate.now());
            prestamo.setDevuelto(false);

            Prestamo guardado = repo.save(prestamo);
            // publicar evento (no-op en tests)
            publisher.publishPrestamoEvent(null);
            return guardado;
        }

        @Override
        public Prestamo devolverLibro(Long prestamoId) {
            Prestamo prestamo = repo.findById(prestamoId).orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
            prestamo.setDevuelto(true);
            prestamo.setFechaDevolucion(java.time.LocalDate.now());
            Prestamo actualizado = repo.save(prestamo);
            publisher.publishPrestamoEvent(null);
            return actualizado;
        }
    }

    @Test
    void prestar_deberiaRetornar201YPrestamoCreado() {
        // Arrange
        when(prestamoRepository.findByUsernameAndDevueltoFalse("testuser")).thenReturn(Arrays.asList());
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamo);
        // no-op publisher; no stubbing necesario

        // Act
        ResponseEntity<Prestamo> response = prestamoController.prestar(1L, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(1L, response.getBody().getLibroId());
        assertFalse(response.getBody().isDevuelto());

        verify(prestamoRepository, times(1)).save(any(Prestamo.class));
    }

    @Test
    void devolver_deberiaRetornar200YPrestamoActualizado() {
        // Arrange
        prestamo.setDevuelto(true);
        prestamo.setFechaDevolucion(LocalDate.now());
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamo);
        // no-op publisher; no stubbing necesario

        // Act
        ResponseEntity<Prestamo> response = prestamoController.devolver(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isDevuelto());
        assertNotNull(response.getBody().getFechaDevolucion());

        verify(prestamoRepository, times(1)).findById(1L);
        verify(prestamoRepository, times(1)).save(any(Prestamo.class));
    }

    @Test
    void misPrestamos_deberiaRetornar200YListaDePrestamos() {
        // Arrange
        Prestamo prestamo2 = new Prestamo(2L, "testuser", LocalDate.now().minusDays(3));
        prestamo2.setId(2L);
        prestamo2.setDevuelto(true);

        when(prestamoRepository.findByUsername("testuser")).thenReturn(Arrays.asList(prestamo, prestamo2));

        // Act
        ResponseEntity<List<Prestamo>> response = prestamoController.misPrestamos(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).getUsername());

        verify(prestamoRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void misPrestamosPaginados_deberiaRetornar200YPaginaDePrestamos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo), pageable, 1);

        when(prestamoRepository.findByUsername(eq("testuser"), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Prestamo>> response = prestamoController.misPrestamosPaginados(
                authentication, 0, 10, "fechaPrestamo", "desc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getContent().size());

        verify(prestamoRepository, times(1)).findByUsername(eq("testuser"), any(Pageable.class));
    }

    @Test
    void misPrestamosActivos_deberiaRetornar200YPrestamosActivos() {
        // Arrange
        when(prestamoRepository.findByUsernameAndDevueltoFalse("testuser")).thenReturn(Collections.singletonList(prestamo));

        // Act
        ResponseEntity<List<Prestamo>> response = prestamoController.misPrestamosActivos(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertFalse(response.getBody().get(0).isDevuelto());

        verify(prestamoRepository, times(1)).findByUsernameAndDevueltoFalse("testuser");
    }

    @Test
    void listarTodos_deberiaRetornar200YListaDeTodosPrestamos() {
        // Arrange
        Prestamo prestamo2 = new Prestamo();
        prestamo2.setUsername("user2");
        prestamo2.setFechaPrestamo(LocalDate.now().minusDays(3));
        prestamo2.setId(2L);

        when(prestamoRepository.findAll()).thenReturn(Arrays.asList(prestamo, prestamo2));

        // Act
        ResponseEntity<List<Prestamo>> response = prestamoController.listarTodos();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(prestamoRepository, times(1)).findAll();
    }

    @Test
    void listarTodosPaginados_deberiaRetornar200YPaginaDeTodosPrestamos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo), pageable, 1);

        when(prestamoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Prestamo>> response = prestamoController.listarTodosPaginados(
                0, 10, "fechaPrestamo", "desc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());

        verify(prestamoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void prestamosPorLibro_deberiaRetornar200YPaginaDePrestamos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo), pageable, 1);

        when(prestamoRepository.findByLibroId(eq(1L), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Prestamo>> response = prestamoController.prestamosPorLibro(1L, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1L, response.getBody().getContent().get(0).getLibroId());

        verify(prestamoRepository, times(1)).findByLibroId(eq(1L), any(Pageable.class));
    }
}

