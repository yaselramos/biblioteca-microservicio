package com.biblioteca.auth.controller;

import com.biblioteca.auth.dto.RegisterRequest;
import com.biblioteca.auth.entity.Usuario;
import com.biblioteca.auth.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private com.biblioteca.auth.service.JwtServiceAuth jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginDebeRetornar200() throws Exception {
        Usuario user = new Usuario();
        user.setUsername("yasel");
        user.setPassword("encoded");
        user.setRol(com.biblioteca.auth.service.Rol.USER);

        when(usuarioRepository.findByUsername("yasel")).thenReturn(Optional.of(user));
        when(encoder.matches("1234", "encoded")).thenReturn(true);
        when(jwtService.generarToken("yasel")).thenReturn("token");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"yasel\",\"password\":\"1234\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void registerDebeRetornar201() throws Exception {
        RegisterRequest request = new RegisterRequest("nuevo", "1234", "ADMIN");
        when(usuarioRepository.findByUsername("nuevo")).thenReturn(Optional.empty());
        when(encoder.encode("1234")).thenReturn("hash");
        when(jwtService.generarToken("nuevo")).thenReturn("token");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void loginDebeRetornar401SiUsuarioNoExiste() throws Exception {
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"noexiste\",\"password\":\"1234\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginDebeRetornar401SiPasswordIncorrecta() throws Exception {
        Usuario user = new Usuario();
        user.setUsername("user");
        user.setPassword("correct");
        when(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "correct")).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerDebeRetornar409SiUsuarioExiste() throws Exception {
        RegisterRequest request = new RegisterRequest("existe", "1234", "USER");
        when(usuarioRepository.findByUsername("existe")).thenReturn(Optional.of(new Usuario()));
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
