package com.biblioteca.prestamo.service;

import com.biblioteca.common.dto.PrestamoEvent;
import com.biblioteca.common.exception.ResourceNotFoundException;
import com.biblioteca.prestamo.dto.LibroDTO;
import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.messaging.PrestamoEventPublisher;
import com.biblioteca.prestamo.repository.PrestamoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de préstamos con caché optimizado y paginación
 */
@Service
public class PrestamoService {

    private final PrestamoRepository repo;
    private final PrestamoEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    private static final String LIBRO_SERVICE_URL = "http://localhost:8081/libros/";

    public PrestamoService(PrestamoRepository repo,
                          PrestamoEventPublisher eventPublisher,
                          RestTemplate restTemplate) {
        this.repo = repo;
        this.eventPublisher = eventPublisher;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public Prestamo prestarLibro(String username, Long libroId) {
        // 1. Verificar si el libro existe y tiene stock
        try {
            ResponseEntity<LibroDTO> response = restTemplate.getForEntity(
                LIBRO_SERVICE_URL + libroId,
                LibroDTO.class
            );

            LibroDTO libro = response.getBody();

            if (libro == null) {
                throw new ResourceNotFoundException("Libro no encontrado: " + libroId);
            }

            if (libro.stock() <= 0) {
                throw new IllegalStateException(
                    "No hay stock disponible para el libro: " + libro.titulo()
                );
            }

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Libro no encontrado: " + libroId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("Libro no encontrado: " + libroId);
            }
            throw e;
        }

        // 2. Verificar si el usuario ya tiene un préstamo activo de este libro
        List<Prestamo> prestamosActivos = repo.findByUsernameAndDevueltoFalse(username);
        boolean tienePrestamoActivo = prestamosActivos.stream()
            .anyMatch(p -> p.getLibroId().equals(libroId));

        if (tienePrestamoActivo) {
            throw new IllegalStateException("Ya tienes un préstamo activo de este libro");
        }

        // 3. Crear el préstamo
        Prestamo prestamo = new Prestamo();
        prestamo.setUsername(username);
        prestamo.setLibroId(libroId);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(null);
        prestamo.setDevuelto(false);

        Prestamo prestamoGuardado = repo.save(prestamo);

        // 4. Publicar evento para que libro-service actualice el stock
        PrestamoEvent event = new PrestamoEvent(
                prestamoGuardado.getId(),
                libroId,
                username,
                LocalDate.now(),
                PrestamoEvent.EventType.PRESTAMO_CREADO
        );

        eventPublisher.publishPrestamoEvent(event);

        return prestamoGuardado;
    }

    @Transactional
    public Prestamo devolverLibro(Long prestamoId) {
        Prestamo prestamo = repo.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));

        if (prestamo.isDevuelto()) {
            throw new IllegalStateException("El libro ya fue devuelto");
        }

        prestamo.setDevuelto(true);
        prestamo.setFechaDevolucion(LocalDate.now());
        Prestamo prestamoActualizado = repo.save(prestamo);

        // Publicar evento para que libro-service incremente el stock
        PrestamoEvent event = new PrestamoEvent(
                prestamo.getId(),
                prestamo.getLibroId(),
                prestamo.getUsername(),
                prestamo.getFechaDevolucion(),
                PrestamoEvent.EventType.PRESTAMO_DEVUELTO
        );

        eventPublisher.publishPrestamoEvent(event);

        return prestamoActualizado;
    }

    /**
     * Obtiene préstamos de un usuario con caché
     */
    @Cacheable(value = "prestamosUsuario", key = "#username")
    public List<Prestamo> obtenerPrestamosUsuario(String username) {
        return repo.findByUsername(username);
    }

    /**
     * Lista todos los préstamos con caché
     */
    @Cacheable(value = "prestamos")
    public List<Prestamo> listarTodos() {
        return repo.findAll();
    }

    /**
     * Busca un préstamo por ID con caché
     */
    @Cacheable(value = "prestamo", key = "#id", unless = "#result.isEmpty()")
    public Optional<Prestamo> buscarPorId(Long id) {
        return repo.findById(id);
    }

    /**
     * Obtiene préstamos activos de un usuario con caché
     */
    @Cacheable(value = "prestamosActivos", key = "#username")
    public List<Prestamo> obtenerPrestamosActivos(String username) {
        return repo.findByUsernameAndDevueltoFalse(username);
    }

    // ========== MÉTODOS CON PAGINACIÓN ==========

    /**
     * Obtener todos los préstamos con paginación
     */
    @Cacheable(value = "prestamosPaginados", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Prestamo> obtenerPrestamosPaginados(Pageable pageable) {
        return repo.findAll(pageable);
    }

    /**
     * Obtener préstamos de un usuario con paginación
     */
    @Cacheable(value = "prestamosUsuarioPaginados", key = "#username + '_' + #pageable.pageNumber")
    public Page<Prestamo> obtenerPrestamosUsuarioPaginados(String username, Pageable pageable) {
        return repo.findByUsername(username, pageable);
    }

    /**
     * Obtener préstamos activos de un usuario con paginación
     */
    @Cacheable(value = "prestamosActivosPaginados", key = "#username + '_' + #pageable.pageNumber")
    public Page<Prestamo> obtenerPrestamosActivosPaginados(String username, Pageable pageable) {
        return repo.findByUsernameAndDevueltoFalse(username, pageable);
    }

    /**
     * Obtener préstamos devueltos de un usuario con paginación
     */
    @Cacheable(value = "prestamosDevueltosPaginados", key = "#username + '_' + #pageable.pageNumber")
    public Page<Prestamo> obtenerPrestamosDevueltosPaginados(String username, Pageable pageable) {
        return repo.findByUsernameAndDevueltoTrue(username, pageable);
    }

    /**
     * Obtener préstamos de un libro específico con paginación
     */
    @Cacheable(value = "prestamosLibroPaginados", key = "#libroId + '_' + #pageable.pageNumber")
    public Page<Prestamo> obtenerPrestamosPorLibro(Long libroId, Pageable pageable) {
        return repo.findByLibroId(libroId, pageable);
    }

    /**
     * Obtener préstamos por rango de fechas con paginación
     */
    public Page<Prestamo> obtenerPrestamosPorFecha(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        return repo.findByFechaPrestamoEntre(fechaInicio, fechaFin, pageable);
    }

    /**
     * Obtener préstamos vencidos (más de 30 días sin devolver)
     */
    public Page<Prestamo> obtenerPrestamosVencidos(Pageable pageable) {
        LocalDate fechaLimite = LocalDate.now().minusDays(30);
        return repo.findPrestamosVencidos(fechaLimite, pageable);
    }
}

