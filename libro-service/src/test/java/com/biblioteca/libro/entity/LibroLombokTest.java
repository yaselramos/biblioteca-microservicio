package com.biblioteca.libro.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibroLombokTest {

    @Test
    void testExhaustiveEquals() {
        Libro base = new Libro(1L, "T", "A", 10);
        Libro baseCopy = new Libro(1L, "T", "A", 10);
        
        // Basicos
        assertEquals(base, base);
        assertEquals(base, baseCopy);
        assertNotEquals(null, base);
        assertNotEquals(new Object(), base);
        
        // Combinaciones de campos para cubrir todas las ramas de equals/canEqual
        assertNotEquals(new Libro(2L, "T", "A", 10), base);
        assertNotEquals(new Libro(1L, "X", "A", 10), base);
        assertNotEquals(new Libro(1L, "T", "X", 10), base);
        assertNotEquals(new Libro(1L, "T", "A", 99), base);
        
        // Manejo de nulos en campos
        Libro conNulos = new Libro();
        Libro otroConNulos = new Libro();
        assertEquals(conNulos, otroConNulos);
        assertNotEquals(base, conNulos);
        assertNotEquals(conNulos, base);
    }

    @Test
    void testExhaustiveHashCode() {
        Libro l1 = new Libro(1L, "T", "A", 10);
        Libro l2 = new Libro(1L, "T", "A", 10);
        assertEquals(l1.hashCode(), l2.hashCode());
        
        Libro l3 = new Libro();
        assertNotEquals(l1.hashCode(), l3.hashCode());
    }

    @Test
    void testToStringAndBuilder() {
        Libro l = Libro.builder().id(1L).titulo("T").build();
        assertNotNull(l.toString());
        assertTrue(l.toString().contains("titulo=T"));
    }
}
