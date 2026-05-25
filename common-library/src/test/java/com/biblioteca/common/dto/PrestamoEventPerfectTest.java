package com.biblioteca.common.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PrestamoEventPerfectTest {

    @Test
    void testEqualsAndHashCodeFullPermutations() {
        LocalDate d = LocalDate.now();
        PrestamoEvent base = new PrestamoEvent(1L, 1L, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO);
        
        // Reflexive & Null
        assertEquals(base, base);
        assertNotEquals(null, base);
        assertNotEquals("not-a-dto", base);

        // Permutations for EACH field (Covers all branches of Lombok's equals)
        // prestamoId
        assertNotEquals(base, new PrestamoEvent(2L, 1L, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(null, 1L, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        // libroId
        assertNotEquals(base, new PrestamoEvent(1L, 2L, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, null, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        // username
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "x", d, PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, 1L, null, d, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        // fechaPrestamo
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "u", d.plusDays(1), PrestamoEvent.EventType.PRESTAMO_CREADO));
        assertNotEquals(new PrestamoEvent(1L, 1L, "u", null, PrestamoEvent.EventType.PRESTAMO_CREADO), base);
        
        // eventType
        assertNotEquals(base, new PrestamoEvent(1L, 1L, "u", d, PrestamoEvent.EventType.PRESTAMO_DEVUELTO));
        assertNotEquals(new PrestamoEvent(1L, 1L, "u", d, null), base);

        // canEqual & HashCode
        assertTrue(base.canEqual(new PrestamoEvent()));
        assertEquals(new PrestamoEvent(1L, 1L, "u", d, PrestamoEvent.EventType.PRESTAMO_CREADO).hashCode(), base.hashCode());
        assertNotEquals(0, base.hashCode());
    }
}
