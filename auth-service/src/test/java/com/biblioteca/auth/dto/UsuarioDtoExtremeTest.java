package com.biblioteca.auth.dto;

import com.biblioteca.auth.service.Rol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioDtoExtremeTest {

    @Test
    void testEqualsConditions() {
        UsuarioDto base = new UsuarioDto(1L, "u", "p", Rol.ADMIN);
        
        // ID
        assertNotEquals(new UsuarioDto(null, "u", "p", Rol.ADMIN), base);
        
        // usuario
        assertNotEquals(new UsuarioDto(1L, null, "p", Rol.ADMIN), base);
        
        // password
        assertNotEquals(new UsuarioDto(1L, "u", null, Rol.ADMIN), base);
        
        // rol
        assertNotEquals(new UsuarioDto(1L, "u", "p", null), base);
    }
}
