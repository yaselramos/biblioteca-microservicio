package com.biblioteca.common.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PrestamoEventBruteForceTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDate d = LocalDate.now();
        PrestamoEvent b = new PrestamoEvent(1L, 1L, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO);
        
        // canEqual
        assertTrue(b.canEqual(new PrestamoEvent()));
        
        // Field by field permutations (null vs non-null on both sides)
        // 1. prestamoId
        assertNotEquals(b, new PrestamoEvent(null, 1L, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(null, 1L, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO), b);
        
        // 2. libroId
        assertNotEquals(b, new PrestamoEvent(1L, null, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, null, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO), b);
        
        // 3. username
        assertNotEquals(b, new PrestamoEvent(1L, 1L, null, d, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, 1L, null, d, PrestamoEvent.EventType.PRESTAMO_CREADO), b);
        
        // 4. fechaPrestamo
        assertNotEquals(b, new PrestamoEvent(1L, 1L, "u", null, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, 1L, "u", null, PrestamoEvent.EventType.PRESTAMO_CREADO), b);
        
        // 5. eventType
        assertNotEquals(b, new PrestamoEvent(1L, 1L, "u", d, null));
        assertNotEquals(new PrestamoEvent(1L, 1L, "u", d, null), b);
        
        // All different values
        assertNotEquals(b, new PrestamoEvent(2L, 2L, "x", d.plusDays(1), PrestamoEvent.EventType.PRESTAMO_DEVUELTO));
    }
}
