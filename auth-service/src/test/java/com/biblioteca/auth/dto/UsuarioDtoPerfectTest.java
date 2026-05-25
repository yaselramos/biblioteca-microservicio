package com.biblioteca.auth.dto;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioDtoPerfectTest {

    @Test
    void testEqualsAndHashCodeFullPermutations() {
        UsuarioDto base = new UsuarioDto(1L, "u", "p", Rol.ADMIN);

        assertEquals(base, base);
        assertNotEquals(null, base);
        assertNotEquals("not-a-dto", base);

        // Permute each field (different value and null cases)
        assertNotEquals(new UsuarioDto(2L, "u", "p", Rol.ADMIN), base);
        assertNotEquals(new UsuarioDto(null, "u", "p", Rol.ADMIN), base);

        assertNotEquals(new UsuarioDto(1L, "x", "p", Rol.ADMIN), base);
        assertNotEquals(new UsuarioDto(1L, null, "p", Rol.ADMIN), base);

        assertNotEquals(new UsuarioDto(1L, "u", "x", Rol.ADMIN), base);
        assertNotEquals(new UsuarioDto(1L, "u", null, Rol.ADMIN), base);

        assertNotEquals(new UsuarioDto(1L, "u", "p", Rol.USER), base);
        assertNotEquals(new UsuarioDto(1L, "u", "p", null), base);

        assertTrue(base.canEqual(new UsuarioDto()));
        assertEquals(base.hashCode(), new UsuarioDto(1L, "u", "p", Rol.ADMIN).hashCode());
    }
}
