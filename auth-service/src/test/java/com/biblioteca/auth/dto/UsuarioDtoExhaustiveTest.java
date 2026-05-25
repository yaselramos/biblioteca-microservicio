package com.biblioteca.auth.dto;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioDtoExhaustiveTest {

    @Test
    void testEqualsAndHashCode() {
        UsuarioDto d1 = UsuarioDto.builder().id(1L).usuario("u").password("p").rol(Rol.ADMIN).build();
        UsuarioDto d2 = UsuarioDto.builder().id(1L).usuario("u").password("p").rol(Rol.ADMIN).build();
        
        assertEquals(d1, d1);
        assertEquals(d1, d2);
        assertNotEquals(null, d1);
        
        // Branches
        assertNotEquals(d1, UsuarioDto.builder().id(2L).usuario("u").password("p").rol(Rol.ADMIN).build());
        assertNotEquals(d1, UsuarioDto.builder().id(1L).usuario("z").password("p").rol(Rol.ADMIN).build());
        assertNotEquals(d1, UsuarioDto.builder().id(1L).usuario("u").password("z").rol(Rol.ADMIN).build());
        assertNotEquals(d1, UsuarioDto.builder().id(1L).usuario("u").password("p").rol(Rol.USER).build());

        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void testAllArgsConstructor() {
        UsuarioDto dto = new UsuarioDto(1L, "u", "p", Rol.ADMIN);
        assertEquals("u", dto.getUsuario());
    }
}
