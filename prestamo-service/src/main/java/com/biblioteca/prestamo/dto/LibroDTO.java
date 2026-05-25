package com.biblioteca.prestamo.dto;
// los record son inmutables no admiten modificaciones.
// Set solo se accede al al valor por el atributo y no por el get
public record LibroDTO( Long id,
                        String titulo,
                        String autor,
                        Integer stock) {
    }




