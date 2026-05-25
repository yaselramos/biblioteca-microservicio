package com.biblioteca.auth.entity;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioLombokTest {

    @Test
    void testExhaustiveEquals() {
        Usuario u1 = new Usuario(1L, "u", "p", Rol.ADMIN);
        Usuario u1Copy = new Usuario(1L, "u", "p", Rol.ADMIN);
        
        assertEquals(u1, u1);
        assertEquals(u1, u1Copy);
        assertNotEquals(null, u1);
        
        // Field by field
        assertNotEquals(new Usuario(2L, "u", "p", Rol.ADMIN), u1);
        assertNotEquals(new Usuario(1L, "x", "p", Rol.ADMIN), u1);
        assertNotEquals(new Usuario(1L, "u", "x", Rol.ADMIN), u1);
        assertNotEquals(new Usuario(1L, "u", "p", Rol.USER), u1);
        
        Usuario empty = new Usuario();
        assertEquals(new Usuario(), empty);
        assertNotEquals(u1, empty);
    }

    @Test
    void testHashCodeAndToString() {
        Usuario u = Usuario.builder().id(1L).username("u").build();
        assertNotNull(u.toString());
        assertNotEquals(0, u.hashCode());
    }
}
