package com.biblioteca.auth.entity;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioPerfectTest {

    @Test
    void testEqualsAndHashCodeFullPermutations() {
        Usuario base = new Usuario(1L, "u", "p", Rol.ADMIN);

        assertEquals(base, base);
        assertNotEquals(null, base);
        assertNotEquals(new Object(), base);

        assertNotEquals(new Usuario(2L, "u", "p", Rol.ADMIN), base);
        assertNotEquals(new Usuario(null, "u", "p", Rol.ADMIN), base);

        assertNotEquals(new Usuario(1L, "x", "p", Rol.ADMIN), base);
        assertNotEquals(new Usuario(1L, null, "p", Rol.ADMIN), base);

        assertNotEquals(new Usuario(1L, "u", "x", Rol.ADMIN), base);
        assertNotEquals(new Usuario(1L, "u", null, Rol.ADMIN), base);

        assertNotEquals(new Usuario(1L, "u", "p", Rol.USER), base);
        assertNotEquals(new Usuario(1L, "u", "p", null), base);

        assertTrue(base.canEqual(new Usuario()));
        assertEquals(base.hashCode(), new Usuario(1L, "u", "p", Rol.ADMIN).hashCode());
    }
}
