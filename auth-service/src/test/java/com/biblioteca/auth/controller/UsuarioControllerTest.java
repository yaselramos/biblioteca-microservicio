package com.biblioteca.auth.controller;

import com.biblioteca.auth.dto.UsuarioDto;
import com.biblioteca.auth.service.Rol;
import com.biblioteca.auth.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService service;

    @MockBean
    private PasswordEncoder encoder;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioDto usuarioDto;

    @BeforeEach
    void setUp() {
        usuarioDto = new UsuarioDto();
        usuarioDto.setId(1L);
        usuarioDto.setUsuario("admin_test");
        usuarioDto.setPassword("password123");
        usuarioDto.setRol(Rol.ADMIN);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_DeberiaRetornar201() throws Exception {
        when(encoder.encode(any())).thenReturn("encoded_pass");
        when(service.guardar(any())).thenReturn(usuarioDto);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario").value("admin_test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_DeberiaRetornarLista() throws Exception {
        when(service.listar()).thenReturn(Collections.singletonList(usuarioDto));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuario").value("admin_test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorId_DeberiaRetornarUsuarioSiExiste() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(usuarioDto));

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("admin_test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorId_DeberiaRetornar404SiNoExiste() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_DeberiaRetornarUsuarioSiExiste() throws Exception {
        when(service.actualizar(eq(1L), any())).thenReturn(usuarioDto);

        mockMvc.perform(put("/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("admin_test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_DeberiaRetornar404SiNoExiste() throws Exception {
        when(service.actualizar(eq(99L), any())).thenReturn(null);

        mockMvc.perform(put("/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_DeberiaRetornar204SiExiste() throws Exception {
        when(service.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_DeberiaRetornar404SiNoExiste() throws Exception {
        when(service.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void listar_DeberiaRetornar403ParaUsuarioNoAdmin() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }
}
