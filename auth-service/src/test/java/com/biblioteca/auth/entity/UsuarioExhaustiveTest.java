package com.biblioteca.auth.entity;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioExhaustiveTest {

    @Test
    void testEqualsAndHashCode() {
        Usuario u1 = Usuario.builder().id(1L).username("u").password("p").rol(Rol.ADMIN).build();
        Usuario u2 = Usuario.builder().id(1L).username("u").password("p").rol(Rol.ADMIN).build();
        Usuario u3 = Usuario.builder().id(2L).username("x").password("y").rol(Rol.USER).build();

        assertEquals(u1, u1);
        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertNotEquals(null, u1);
        assertNotEquals("string", u1);

        // Diferencias campo por campo para coverage de condiciones
        assertNotEquals(u1, Usuario.builder().id(9L).username("u").password("p").rol(Rol.ADMIN).build());
        assertNotEquals(u1, Usuario.builder().id(1L).username("z").password("p").rol(Rol.ADMIN).build());
        assertNotEquals(u1, Usuario.builder().id(1L).username("u").password("z").rol(Rol.ADMIN).build());
        assertNotEquals(u1, Usuario.builder().id(1L).username("u").password("p").rol(Rol.USER).build());

        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void testConstructors() {
        Usuario u = new Usuario(1L, "u", "p", Rol.ADMIN);
        assertEquals(1L, u.getId());
        
        Usuario empty = new Usuario();
        assertNotNull(empty);
    }
}
