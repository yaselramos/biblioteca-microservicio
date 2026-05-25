package com.biblioteca.auth.dto;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioDtoBruteForceTest {

    @Test
    void testEqualsAndHashCode() {
        UsuarioDto b = new UsuarioDto(1L, "u", "p", Rol.ADMIN);

        assertEquals(b, b);
        assertNotEquals(null, b);
        
        // Permute each field
        assertNotEquals(new UsuarioDto(2L, "u", "p", Rol.ADMIN), b);
        assertNotEquals(new UsuarioDto(null, "u", "p", Rol.ADMIN), b);

        assertNotEquals(new UsuarioDto(1L, "x", "p", Rol.ADMIN), b);
        assertNotEquals(new UsuarioDto(1L, null, "p", Rol.ADMIN), b);

        assertNotEquals(new UsuarioDto(1L, "u", "x", Rol.ADMIN), b);
        assertNotEquals(new UsuarioDto(1L, "u", null, Rol.ADMIN), b);

        assertNotEquals(new UsuarioDto(1L, "u", "p", Rol.USER), b);
        assertNotEquals(new UsuarioDto(1L, "u", "p", null), b);

        assertTrue(b.canEqual(new UsuarioDto()));
    }
}
