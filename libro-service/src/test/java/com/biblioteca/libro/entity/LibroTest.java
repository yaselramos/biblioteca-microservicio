package com.biblioteca.libro.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibroTest {

    @Test
    void testLibroEntity() {
        Libro libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Test");
        libro.setAutor("Autor");
        libro.setStock(10);

        assertEquals(1L, libro.getId());
        assertEquals("Test", libro.getTitulo());
        assertEquals("Autor", libro.getAutor());
        assertEquals(10, libro.getStock());

        Libro libro2 = Libro.builder()
                .id(1L)
                .titulo("Test")
                .build();
        assertEquals(1L, libro2.getId());
        assertNotNull(libro.toString());
    }
}
