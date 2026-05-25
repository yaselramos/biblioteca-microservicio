package com.biblioteca.auth.entity;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioCompleteTest {

    @Test
    void testEqualsExhaustive() {
        Usuario base = new Usuario(1L, "u", "p", Rol.ADMIN);

        assertEquals(base, base);
        assertNotEquals(null, base);
        
        // ID
        assertNotEquals(new Usuario(2L, "u", "p", Rol.ADMIN), base);
        assertNotEquals(new Usuario(null, "u", "p", Rol.ADMIN), base);
        
        // Username
        assertNotEquals(new Usuario(1L, "x", "p", Rol.ADMIN), base);
        assertNotEquals(new Usuario(1L, null, "p", Rol.ADMIN), base);
        
        // Password
        assertNotEquals(new Usuario(1L, "u", "x", Rol.ADMIN), base);
        assertNotEquals(new Usuario(1L, "u", null, Rol.ADMIN), base);
        
        // Rol
        assertNotEquals(new Usuario(1L, "u", "p", Rol.USER), base);
        assertNotEquals(new Usuario(1L, "u", "p", null), base);

        assertTrue(base.canEqual(new Usuario()));
        assertNotEquals(0, base.hashCode());
        assertNotNull(base.toString());
    }
}
