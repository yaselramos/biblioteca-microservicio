package com.biblioteca.auth.dto;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioDtoTest {

    @Test
    void testUsuarioDto() {
        UsuarioDto dto = UsuarioDto.builder()
                .id(1L)
                .usuario("test")
                .password("pass")
                .rol(Rol.ADMIN)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("test", dto.getUsuario());
        assertEquals("pass", dto.getPassword());
        assertEquals(Rol.ADMIN, dto.getRol());

        UsuarioDto dto2 = new UsuarioDto();
        dto2.setId(2L);
        dto2.setUsuario("user");
        dto2.setPassword("secret");
        dto2.setRol(Rol.USER);

        assertEquals(2L, dto2.getId());
        assertEquals("user", dto2.getUsuario());
        assertEquals("secret", dto2.getPassword());
        assertEquals(Rol.USER, dto2.getRol());
        
        assertNotNull(dto.toString());
        assertNotEquals(dto, dto2);
        assertNotEquals(0, dto.hashCode());
    }
}
