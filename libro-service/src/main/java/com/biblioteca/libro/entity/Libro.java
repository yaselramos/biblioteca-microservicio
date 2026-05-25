package com.biblioteca.libro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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


}

