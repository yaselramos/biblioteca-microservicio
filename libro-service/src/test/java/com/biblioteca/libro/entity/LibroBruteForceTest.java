package com.biblioteca.libro.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibroBruteForceTest {

    @Test
    void testEqualsAndHashCode() {
        Libro b = new Libro(1L, "T", "A", 10);

        assertNotEquals(new Libro(null, "T", "A", 10), b);
        assertNotEquals(new Libro(null, "T", "A", 10), b);

        assertNotEquals(new Libro(1L, null, "A", 10), b);

        assertNotEquals(new Libro(1L, "T", null, 10), b);

        assertTrue(b.canEqual(new Libro()));
    }
}
