package com.biblioteca.auth.entity;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void testUsuarioEntity() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("test");
        usuario.setPassword("pass");
        usuario.setRol(Rol.USER);

        assertEquals(1L, usuario.getId());
        assertEquals("test", usuario.getUsername());
        assertEquals("pass", usuario.getPassword());
        assertEquals(Rol.USER, usuario.getRol());

        Usuario usuario2 = Usuario.builder()
                .id(1L)
                .username("test")
                .build();
        assertEquals(1L, usuario2.getId());
        assertNotNull(usuario.toString());
    }
}
