package com.biblioteca.libro.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibroExhaustiveTest {

    @Test
    void testEqualsAndHashCode() {
        Libro libro1 = Libro.builder().id(1L).titulo("A").autor("X").stock(1).build();
        Libro libro2 = Libro.builder().id(1L).titulo("A").autor("X").stock(1).build();
        Libro libro3 = Libro.builder().id(2L).titulo("B").autor("Y").stock(2).build();
        Libro libroNull = null;
        String noEsUnLibro = "Not a libro";

        // Reflexivo
        assertEquals(libro1, libro1);
        
        // Simétrico
        assertEquals(libro1, libro2);
        assertEquals(libro2, libro1);
        
        // No igual
        assertNotEquals(libro1, libro3);
        assertNotEquals(libroNull, libro1);
        assertNotEquals(noEsUnLibro, libro1);

        // Diferencias campo por campo (para cubrir branches de equals)
        assertNotEquals(libro1, Libro.builder().id(99L).titulo("A").autor("X").stock(1).build());
        assertNotEquals(libro1, Libro.builder().id(1L).titulo("Z").autor("X").stock(1).build());
        assertNotEquals(libro1, Libro.builder().id(1L).titulo("A").autor("Z").stock(1).build());
        assertNotEquals(libro1, Libro.builder().id(1L).titulo("A").autor("X").stock(99).build());

        // HashCode
        assertEquals(libro1.hashCode(), libro2.hashCode());
        assertNotEquals(libro1.hashCode(), libro3.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        Libro libro = new Libro();
        assertNotNull(libro);
    }
}
