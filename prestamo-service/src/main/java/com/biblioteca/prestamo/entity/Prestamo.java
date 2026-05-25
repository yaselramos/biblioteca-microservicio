package com.biblioteca.prestamo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidad Prestamo optimizada con índices para búsquedas frecuentes
 *
 * Optimizaciones:
 * - Índices en username, libroId y fechaPrestamo
 * - Índice compuesto para búsquedas de préstamos activos por usuario
 * - Configuración explícita de columnas
 */
@Entity
@Table(name = "prestamo", indexes = {
    @Index(name = "idx_prestamo_username", columnList = "username"),
    @Index(name = "idx_prestamo_libro_id", columnList = "libro_id"),
    @Index(name = "idx_prestamo_fecha", columnList = "fecha_prestamo"),
    @Index(name = "idx_prestamo_devuelto", columnList = "devuelto"),
    @Index(name = "idx_prestamo_usuario_activo", columnList = "username, devuelto")
})
@Getter
@Setter
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libro_id", nullable = false)
    private Long libroId;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Column(name = "devuelto", nullable = false)
    private boolean devuelto = false;

    public Prestamo() {

    }

    public Prestamo(Long id, Long libroId, String username, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.id = id;
        this.libroId = libroId;
        this.username = username;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    public Prestamo(long l, String testuser, LocalDate now) {
        this.libroId = l;
        this.username = testuser;
        this.fechaPrestamo = now;
    }
}
