package com.biblioteca.prestamo.entity;

import jakarta.persistence.*;
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

    // Constructor vacío requerido por JPA
    public Prestamo() {
    }

    // Constructor con parámetros para facilitar creación
    public Prestamo(Long libroId, String username, LocalDate fechaPrestamo) {
        this.libroId = libroId;
        this.username = username;
        this.fechaPrestamo = fechaPrestamo;
        this.devuelto = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }
}
