package com.biblioteca.prestamo.repository;

import com.biblioteca.prestamo.entity.Prestamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio de Préstamos con soporte de paginación
 */
@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // Métodos sin paginación (mantener compatibilidad)
    List<Prestamo> findByUsername(String username);

    List<Prestamo> findByUsernameAndDevueltoFalse(String username);

    List<Prestamo> findByLibroId(Long libroId);

    // Métodos con paginación
    /**
     * Obtener préstamos de un usuario con paginación
     */
    Page<Prestamo> findByUsername(String username, Pageable pageable);

    /**
     * Obtener préstamos activos de un usuario con paginación
     */
    Page<Prestamo> findByUsernameAndDevueltoFalse(String username, Pageable pageable);

    /**
     * Obtener préstamos devueltos de un usuario con paginación
     */
    Page<Prestamo> findByUsernameAndDevueltoTrue(String username, Pageable pageable);

    /**
     * Obtener préstamos de un libro específico con paginación
     */
    Page<Prestamo> findByLibroId(Long libroId, Pageable pageable);

    /**
     * Obtener préstamos por rango de fechas con paginación
     */
    @Query("SELECT p FROM Prestamo p WHERE p.fechaPrestamo BETWEEN :fechaInicio AND :fechaFin")
    Page<Prestamo> findByFechaPrestamoEntre(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        Pageable pageable
    );

    /**
     * Obtener préstamos vencidos (más de 30 días sin devolver) con paginación
     */
    @Query("SELECT p FROM Prestamo p WHERE p.devuelto = false " +
           "AND p.fechaPrestamo < :fechaLimite")
    Page<Prestamo> findPrestamosVencidos(
        @Param("fechaLimite") LocalDate fechaLimite,
        Pageable pageable
    );
}

