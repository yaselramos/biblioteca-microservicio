package com.biblioteca.libro.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibroPerfectTest {

    @Test
    void testEqualsAndHashCodeFullPermutations() {
        Libro base = new Libro(1L, "T", "A", 10);

        assertEquals(base, base);
        assertNotEquals(null, base);
        assertNotEquals(new Object(), base);

        assertNotEquals(new Libro(2L, "T", "A", 10), base);
        assertNotEquals(new Libro(null, "T", "A", 10), base);

        assertNotEquals(new Libro(1L, "X", "A", 10), base);
        assertNotEquals(new Libro(1L, null, "A", 10), base);

        assertNotEquals(new Libro(1L, "T", "X", 10), base);
        assertNotEquals(new Libro(1L, "T", null, 10), base);

        assertNotEquals(new Libro(1L, "T", "A", 99), base);

        assertTrue(base.canEqual(new Libro()));
        assertEquals(base.hashCode(), new Libro(1L, "T", "A", 10).hashCode());
    }
}
