package com.biblioteca.common.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PrestamoEventExhaustiveTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDate now = LocalDate.now();
        PrestamoEvent e1 = new PrestamoEvent(1L, 1L, "u", now, PrestamoEvent.EventType.PRESTAMO_CREADO);
        PrestamoEvent e2 = new PrestamoEvent(1L, 1L, "u", now, PrestamoEvent.EventType.PRESTAMO_CREADO);

        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertNotEquals(null, e1);

        // Individual field comparisons for branch coverage
        assertNotEquals(e1, new PrestamoEvent(2L, 1L, "u", now, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(e1, new PrestamoEvent(1L, 2L, "u", now, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(e1, new PrestamoEvent(1L, 1L, "z", now, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(e1, new PrestamoEvent(1L, 1L, "u", now.plusDays(1), PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(e1, new PrestamoEvent(1L, 1L, "u", now, PrestamoEvent.EventType.PRESTAMO_DEVUELTO));

        assertEquals(e1.hashCode(), e2.hashCode());
    }
}
