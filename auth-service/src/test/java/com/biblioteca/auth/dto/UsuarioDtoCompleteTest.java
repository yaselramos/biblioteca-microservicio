package com.biblioteca.auth.dto;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioDtoCompleteTest {

    @Test
    void testEqualsExhaustive() {
        UsuarioDto base = new UsuarioDto(1L, "u", "p", Rol.ADMIN);
        
        // Basicos
        assertEquals(base, base);
        assertNotEquals(null, base);
        assertNotEquals("not-a-dto", base);

        // Permutaciones de campo nulo vs no nulo y valores diferentes
        // ID
        assertNotEquals(new UsuarioDto(2L, "u", "p", Rol.ADMIN), base);
        assertNotEquals(new UsuarioDto(null, "u", "p", Rol.ADMIN), base);
        assertEquals(new UsuarioDto(null, "u", "p", Rol.ADMIN), new UsuarioDto(null, "u", "p", Rol.ADMIN));
        
        // Usuario
        assertNotEquals(new UsuarioDto(1L, "x", "p", Rol.ADMIN), base);
        assertNotEquals(new UsuarioDto(1L, null, "p", Rol.ADMIN), base);
        
        // Password
        assertNotEquals(new UsuarioDto(1L, "u", "x", Rol.ADMIN), base);
        assertNotEquals(new UsuarioDto(1L, "u", null, Rol.ADMIN), base);

        // Rol
        assertNotEquals(new UsuarioDto(1L, "u", "p", Rol.USER), base);
        assertNotEquals(new UsuarioDto(1L, "u", "p", null), base);
        
        // canEqual
        assertTrue(base.canEqual(new UsuarioDto()));
        assertFalse(base.canEqual(""));
    }

    @Test
    void testHashCodeExhaustive() {
        UsuarioDto d1 = new UsuarioDto(1L, "u", "p", Rol.ADMIN);
        UsuarioDto d2 = new UsuarioDto(1L, "u", "p", Rol.ADMIN);
        assertEquals(d1.hashCode(), d2.hashCode());
        
        // Test hashCode with nulls
        assertNotEquals(d1.hashCode(), new UsuarioDto().hashCode());
    }
}
