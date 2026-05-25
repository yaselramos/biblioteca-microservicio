package com.biblioteca.common.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PrestamoEventTest {

    @Test
    void testGetterSetterAndBuilder() {
        LocalDate fecha = LocalDate.now();
        PrestamoEvent event = PrestamoEvent.builder()
                .prestamoId(1L)
                .libroId(2L)
                .username("user")
                .fechaPrestamo(fecha)
                .eventType(PrestamoEvent.EventType.PRESTAMO_CREADO)
                .build();

        assertEquals(1L, event.getPrestamoId());
        assertEquals(2L, event.getLibroId());
        assertEquals("user", event.getUsername());
        assertEquals(fecha, event.getFechaPrestamo());
        assertEquals(PrestamoEvent.EventType.PRESTAMO_CREADO, event.getEventType());

        PrestamoEvent event2 = new PrestamoEvent();
        event2.setPrestamoId(1L);
        assertEquals(1L, event2.getPrestamoId());
        
        assertNotNull(event.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        PrestamoEvent event1 = new PrestamoEvent(1L, 1L, "user", LocalDate.now(), PrestamoEvent.EventType.PRESTAMO_CREADO);
        PrestamoEvent event2 = new PrestamoEvent(1L, 1L, "user", event1.getFechaPrestamo(), PrestamoEvent.EventType.PRESTAMO_CREADO);

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }
}
