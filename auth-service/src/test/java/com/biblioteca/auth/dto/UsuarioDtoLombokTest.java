package com.biblioteca.auth.dto;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioDtoLombokTest {

    @Test
    void testExhaustiveEquals() {
        UsuarioDto d1 = new UsuarioDto(1L, "u", "p", Rol.ADMIN);
        UsuarioDto d2 = new UsuarioDto(1L, "u", "p", Rol.ADMIN);
        
        assertEquals(d1, d1);
        assertEquals(d1, d2);
        
        assertNotEquals(new UsuarioDto(2L, "u", "p", Rol.ADMIN), d1);
        assertNotEquals(new UsuarioDto(1L, "x", "p", Rol.ADMIN), d1);
        assertNotEquals(new UsuarioDto(1L, "u", "x", Rol.ADMIN), d1);
        assertNotEquals(new UsuarioDto(1L, "u", "p", Rol.USER), d1);
        
        assertNotEquals(null, d1);
        assertNotNull(d1.toString());
        assertNotEquals(0, d1.hashCode());
    }
}
