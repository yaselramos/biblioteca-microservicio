package com.biblioteca.libro.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibroExtremeTest {

    @Test
    void testEqualsConditions() {
        Libro base = new Libro(1L, "T", "A", 10);

        assertNotEquals(new Libro(null, "T", "A", 10), base);
        assertNotEquals(new Libro(null, "T", "A", 10), base);

        assertNotEquals(new Libro(1L, null, "A", 10), base);
        assertNotEquals(new Libro(1L, null, "A", 10), base);

        assertNotEquals(new Libro(1L, "T", null, 10), base);
        assertNotEquals(new Libro(1L, "T", null, 10), base);
    }
}
