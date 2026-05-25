package com.biblioteca.common.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PrestamoEventCompleteTest {

    @Test
    void testEqualsExhaustive() {
        LocalDate date = LocalDate.now();
        PrestamoEvent base = new PrestamoEvent(1L, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO);
        
        assertEquals(base, base);
        assertNotEquals(null, base);
        assertNotEquals("not-a-dto", base);
        
        // Field permutations (one null, one not null)
        assertNotEquals(base, new PrestamoEvent(null, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(null, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        assertNotEquals(base, new PrestamoEvent(1L, null, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, null, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        assertNotEquals(base, new PrestamoEvent(1L, 1L, null, date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, 1L, null, date, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "u", null, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, 1L, "u", null, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "u", date, null));
        assertNotEquals(new PrestamoEvent(1L, 1L, "u", date, null), base);

        // Different values
        assertNotEquals(base, new PrestamoEvent(2L, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(base, new PrestamoEvent(1L, 2L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "x", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "u", date.plusDays(1), PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_DEVUELTO));

        assertTrue(base.canEqual(new PrestamoEvent()));
    }

    @Test
    void testHashCodeExhaustive() {
        PrestamoEvent e1 = new PrestamoEvent();
        PrestamoEvent e2 = new PrestamoEvent();
        assertEquals(e1.hashCode(), e2.hashCode());
    }
}
