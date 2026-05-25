package com.biblioteca.auth.entity;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioBruteForceTest {

    @Test
    void testEqualsAndHashCode() {
        Usuario b = new Usuario(1L, "u", "p", Rol.ADMIN);

        assertNotEquals(new Usuario(null, "u", "p", Rol.ADMIN), b);
        assertNotEquals(new Usuario(null, "u", "p", Rol.ADMIN), b);

        assertNotEquals(new Usuario(1L, null, "p", Rol.ADMIN), b);
        assertNotEquals(new Usuario(1L, null, "p", Rol.ADMIN), b);

        assertNotEquals(new Usuario(1L, "u", null, Rol.ADMIN), b);
        assertNotEquals(new Usuario(1L, "u", null, Rol.ADMIN), b);

        assertNotEquals(new Usuario(1L, "u", "p", null), b);
        assertNotEquals(new Usuario(1L, "u", "p", null), b);

        assertTrue(b.canEqual(new Usuario()));
    }
}
