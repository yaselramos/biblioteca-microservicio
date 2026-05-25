package com.biblioteca.libro.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibroCompleteTest {

    @Test
    void testEqualsExhaustive() {
        Libro base = new Libro(1L, "T", "A", 10);

        assertEquals(base, base);
        assertNotEquals(null, base);
        
        // ID
        assertNotEquals(new Libro(2L, "T", "A", 10), base);
        assertNotEquals(new Libro(null, "T", "A", 10), base);
        
        // Titulo
        assertNotEquals(new Libro(1L, "X", "A", 10), base);
        assertNotEquals(new Libro(1L, null, "A", 10), base);
        
        // Autor
        assertNotEquals(new Libro(1L, "T", "X", 10), base);
        assertNotEquals(new Libro(1L, "T", null, 10), base);
        
        // Stock
        assertNotEquals(new Libro(1L, "T", "A", 99), base);

        assertTrue(base.canEqual(new Libro()));
        assertNotEquals(0, base.hashCode());
        assertNotNull(base.toString());
    }
}
