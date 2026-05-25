package com.biblioteca.common.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PrestamoEventLombokTest {

    @Test
    void testExhaustiveEquals() {
        LocalDate date = LocalDate.now();
        PrestamoEvent e1 = new PrestamoEvent(1L, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO);
        PrestamoEvent e2 = new PrestamoEvent(1L, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO);
        
        assertEquals(e1, e1);
        assertEquals(e1, e2);
        
        assertNotEquals(e1, new PrestamoEvent(9L, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(e1, new PrestamoEvent(1L, 9L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(e1, new PrestamoEvent(1L, 1L, "x", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(e1, new PrestamoEvent(1L, 1L, "u", date.plusDays(1), PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(e1, new PrestamoEvent(1L, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_DEVUELTO));
        
        assertNotEquals(null, e1);
        assertNotEquals(0, e1.hashCode());
        assertNotNull(e1.toString());
    }
}
