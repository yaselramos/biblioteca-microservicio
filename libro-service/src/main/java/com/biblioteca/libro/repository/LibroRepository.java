package com.biblioteca.libro.repository;

import com.biblioteca.libro.entity.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio de Libros con soporte de paginación
 */
public interface LibroRepository extends JpaRepository<Libro, Long> {

    /**
     * Buscar libros por título con paginación
     */
    Page<Libro> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    /**
     * Buscar libros por autor con paginación
     */
    Page<Libro> findByAutorContainingIgnoreCase(String autor, Pageable pageable);

    /**
     * Buscar libros con stock disponible con paginación
     */
    @Query("SELECT l FROM Libro l WHERE l.stock > 0")
    Page<Libro> findLibrosDisponibles(Pageable pageable);

    /**
     * Buscar libros sin stock con paginación
     */
    @Query("SELECT l FROM Libro l WHERE l.stock = 0")
    Page<Libro> findLibrosSinStock(Pageable pageable);

    /**
     * Búsqueda avanzada por título o autor con paginación
     */
    @Query("SELECT l FROM Libro l WHERE " +
           "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.autor) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Libro> buscarPorTituloOAutor(@Param("searchTerm") String searchTerm, Pageable pageable);
}


