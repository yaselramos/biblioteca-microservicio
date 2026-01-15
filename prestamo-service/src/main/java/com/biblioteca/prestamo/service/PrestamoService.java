package com.biblioteca.prestamo.service;

import com.biblioteca.prestamo.dto.LibroDTO;
import com.biblioteca.prestamo.dto.PrestamoEvent;
import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.exception.ResourceNotFoundException;
import com.biblioteca.prestamo.messaging.PrestamoEventPublisher;
import com.biblioteca.prestamo.repository.PrestamoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

            if (libro.getStock() <= 0) {
                throw new IllegalStateException(
                    "No hay stock disponible para el libro: " + libro.getTitulo()
                );
            }

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Libro no encontrado: " + libroId);
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
}

