package com.biblioteca.auth.entity;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioExtremeTest {

    @Test
    void testEqualsConditions() {
        Usuario base = new Usuario(1L, "u", "p", Rol.ADMIN);

        assertNotEquals(new Usuario(null, "u", "p", Rol.ADMIN), base);
        assertNotEquals(new Usuario(null, "u", "p", Rol.ADMIN), base);

        assertNotEquals(new Usuario(1L, null, "p", Rol.ADMIN), base);
        assertNotEquals(new Usuario(1L, null, "p", Rol.ADMIN), base);

        assertNotEquals(new Usuario(1L, "u", null, Rol.ADMIN), base);
        assertNotEquals(new Usuario(1L, "u", null, Rol.ADMIN), base);

        assertNotEquals(new Usuario(1L, "u", "p", null), base);
        assertNotEquals(new Usuario(1L, "u", "p", null), base);
    }
}
