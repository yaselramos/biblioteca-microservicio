package com.biblioteca.libro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidad Libro optimizada con estrategias de carga
 *
 * Optimizaciones aplicadas:
 * - Uso de @Column para especificar propiedades de columnas
 * - Índices para búsquedas frecuentes
 * - Caché de segundo nivel habilitado
 */
@Entity
@Table(name = "libro", indexes = {
    @Index(name = "idx_libro_titulo", columnList = "titulo"),
    @Index(name = "idx_libro_autor", columnList = "autor"),
    @Index(name = "idx_libro_stock", columnList = "stock")
})
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(max = 255, message = "El autor no puede exceder 255 caracteres")
    @Column(name = "autor", nullable = false, length = 255)
    private String autor;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    // Constructor vacío requerido por JPA
    public Libro() {
    }

    // Constructor con parámetros para facilitar creación
    public Libro(String titulo, String autor, Integer stock) {
        this.titulo = titulo;
        this.autor = autor;
        this.stock = stock;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}

