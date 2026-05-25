package com.biblioteca.libro.dto;

public record LibroDto(Long id,
                       String titulo,
                       String autor,
                       Integer stock) {
}
