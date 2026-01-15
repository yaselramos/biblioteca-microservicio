package com.biblioteca.libro.service;

import com.biblioteca.libro.entity.Libro;
import com.biblioteca.libro.repository.LibroRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de libros con caché optimizado y paginación
 * Utiliza Spring Cache para reducir consultas a la base de datos
 */
@Service
public class LibroService {

    private final LibroRepository repo;

    public LibroService(LibroRepository repo) {
        this.repo = repo;
    }

    /**
     * Guarda un libro y actualiza la caché
     * @CachePut actualiza la caché con el libro guardado
     * @CacheEvict invalida la lista de libros
     */
    @Caching(
        put = @CachePut(value = "libro", key = "#result.id"),
        evict = @CacheEvict(value = "libros", allEntries = true)
    )
    public Libro guardar(Libro l) {
        return repo.save(l);
    }

    /**
     * Lista todos los libros con caché
     * Los resultados se cachean por 30 minutos
     */
    @Cacheable(value = "libros")
    public List<Libro> listar() {
        return repo.findAll();
    }

    /**
     * Busca un libro por ID con caché
     * Solo cachea si el libro existe
     */
    @Cacheable(value = "libro", key = "#id", unless = "#result.isEmpty()")
    public Optional<Libro> buscarPorId(Long id) {
        return repo.findById(id);
    }

    /**
     * Actualiza un libro y su caché
     * Invalida la lista general de libros
     */
    @Caching(
        put = @CachePut(value = "libro", key = "#id"),
        evict = @CacheEvict(value = "libros", allEntries = true)
    )
    public Libro actualizar(Long id, Libro l) {
        return repo.findById(id)
                .map(existente -> {
                    if (l.getTitulo() != null) {
                        existente.setTitulo(l.getTitulo());
                    }
                    if (l.getAutor() != null) {
                        existente.setAutor(l.getAutor());
                    }
                    if (l.getStock() != null) {
                        existente.setStock(l.getStock());
                    }
                    return repo.save(existente);
                })
                .orElse(null);
    }

    /**
     * Elimina un libro e invalida sus cachés
     */
    @Caching(evict = {
        @CacheEvict(value = "libro", key = "#id"),
        @CacheEvict(value = "libros", allEntries = true),
        @CacheEvict(value = "librosDisponibles", allEntries = true)
    })
    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Decrementa el stock e invalida las cachés relacionadas
     * El stock cambia frecuentemente, por lo que invalidamos la caché
     */
    @Caching(evict = {
        @CacheEvict(value = "libro", key = "#libroId"),
        @CacheEvict(value = "libros", allEntries = true),
        @CacheEvict(value = "librosDisponibles", allEntries = true)
    })
    public void decrementarStock(Long libroId) {
        repo.findById(libroId).ifPresent(libro -> {
            if (libro.getStock() > 0) {
                libro.setStock(libro.getStock() - 1);
                repo.save(libro);
            } else {
                throw new RuntimeException("No hay stock disponible para el libro ID: " + libroId);
            }
        });
    }

    /**
     * Incrementa el stock e invalida las cachés relacionadas
     */
    @Caching(evict = {
        @CacheEvict(value = "libro", key = "#libroId"),
        @CacheEvict(value = "libros", allEntries = true),
        @CacheEvict(value = "librosDisponibles", allEntries = true)
    })
    public void incrementarStock(Long libroId) {
        repo.findById(libroId).ifPresent(libro -> {
            libro.setStock(libro.getStock() + 1);
            repo.save(libro);
        });
    }

    /**
     * Verifica stock con caché temporal
     * Cachea solo resultados positivos (con stock)
     */
    @Cacheable(value = "librosDisponibles", key = "#libroId", unless = "#result == false")
    public boolean verificarStock(Long libroId) {
        return repo.findById(libroId)
                .map(libro -> libro.getStock() > 0)
                .orElse(false);
    }

    // ========== MÉTODOS CON PAGINACIÓN ==========

    /**
     * Lista libros con paginación
     * Más eficiente para grandes volúmenes de datos
     *
     * @param pageable configuración de paginación (página, tamaño, ordenamiento)
     * @return Page con libros y metadatos de paginación
     */
    @Cacheable(value = "librosPaginados", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Libro> listarPaginado(Pageable pageable) {
        return repo.findAll(pageable);
    }

    /**
     * Buscar libros por título con paginación
     */
    @Cacheable(value = "librosPorTitulo", key = "#titulo + '_' + #pageable.pageNumber")
    public Page<Libro> buscarPorTitulo(String titulo, Pageable pageable) {
        return repo.findByTituloContainingIgnoreCase(titulo, pageable);
    }

    /**
     * Buscar libros por autor con paginación
     */
    @Cacheable(value = "librosPorAutor", key = "#autor + '_' + #pageable.pageNumber")
    public Page<Libro> buscarPorAutor(String autor, Pageable pageable) {
        return repo.findByAutorContainingIgnoreCase(autor, pageable);
    }

    /**
     * Obtener libros disponibles (con stock) con paginación
     */
    @Cacheable(value = "librosDisponiblesPaginados", key = "#pageable.pageNumber")
    public Page<Libro> obtenerLibrosDisponibles(Pageable pageable) {
        return repo.findLibrosDisponibles(pageable);
    }

    /**
     * Buscar por título o autor con paginación
     * Búsqueda flexible para el frontend
     */
    @Cacheable(value = "busquedaLibros", key = "#searchTerm + '_' + #pageable.pageNumber")
    public Page<Libro> buscar(String searchTerm, Pageable pageable) {
        return repo.buscarPorTituloOAutor(searchTerm, pageable);
    }
}


