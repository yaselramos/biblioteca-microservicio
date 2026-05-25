package com.biblioteca.common.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PrestamoEventExtremeTest {

    @Test
    void testEqualsConditions() {
        LocalDate date = LocalDate.now();
        PrestamoEvent base = new PrestamoEvent(1L, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO);
        
        // Test combinations of null vs non-null for each field to satisfy JaCoCo conditions
        // prestamoId
        assertNotEquals(base, new PrestamoEvent(null, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(null, 1L, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        // libroId
        assertNotEquals(base, new PrestamoEvent(1L, null, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, null, "u", date, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        // username
        assertNotEquals(base, new PrestamoEvent(1L, 1L, null, date, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, 1L, null, date, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        // fechaPrestamo
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "u", null, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, 1L, "u", null, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        // eventType
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "u", date, null));
        assertNotEquals(new PrestamoEvent(1L, 1L, "u", date, null), base);
    }
}
