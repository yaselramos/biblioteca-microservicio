package com.biblioteca.prestamo.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PrestamoTest {

    @Test
    void testPrestamoEntity() {
        LocalDate fecha = LocalDate.now();
        Prestamo prestamo = new Prestamo();
        prestamo.setId(1L);
        prestamo.setUsername("user");
        prestamo.setLibroId(2L);
        prestamo.setFechaPrestamo(fecha);
        prestamo.setDevuelto(true);

        assertEquals(1L, prestamo.getId());
        assertEquals("user", prestamo.getUsername());
        assertEquals(2L, prestamo.getLibroId());
        assertEquals(fecha, prestamo.getFechaPrestamo());
        assertTrue(prestamo.isDevuelto());

        Prestamo prestamo2 = Prestamo.builder()
                .id(1L)
                .username("user")
                .build();
        assertEquals(1L, prestamo2.getId());
        assertNotNull(prestamo.toString());
    }
}
