package com.biblioteca.auth.controller;

import com.biblioteca.auth.dto.AuthRequest;
import com.biblioteca.auth.dto.RegisterRequest;
import com.biblioteca.auth.entity.Usuario;
import com.biblioteca.auth.repository.UsuarioRepository;
import com.biblioteca.auth.service.Rol;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Tests de Integración para AuthController")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Debería registrar usuario exitosamente")
    void deberiaRegistrarUsuarioExitosamente() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest("newuser", "password123", "USER");

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("Debería rechazar registro con username duplicado")
    void deberiaRechazarRegistroConUsernameDuplicado() throws Exception {
        // Given
        Usuario existente = new Usuario();
        existente.setUsername("existinguser");
        existente.setPassword(passwordEncoder.encode("password"));
        existente.setRol(Rol.USER);
        usuarioRepository.save(existente);

        RegisterRequest request = new RegisterRequest("existinguser", "password123", "USER");

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("El usuario ya existe"));
    }

    @Test
    @DisplayName("Debería rechazar registro con username vacío")
    void deberiaRechazarRegistroConUsernameVacio() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest("", "password123", "USER");

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("Debería rechazar registro con password corto")
    void deberiaRechazarRegistroConPasswordCorto() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest("newuser", "123", "USER");

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("Debería hacer login exitosamente")
    void deberiaHacerLoginExitosamente() throws Exception {
        // Given
        Usuario usuario = new Usuario();
        usuario.setUsername("loginuser");
        usuario.setPassword(passwordEncoder.encode("password123"));
        usuario.setRol(Rol.USER);
        usuarioRepository.save(usuario);

        AuthRequest request = new AuthRequest("loginuser", "password123");

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("Debería rechazar login con credenciales incorrectas")
    void deberiaRechazarLoginConCredencialesIncorrectas() throws Exception {
        // Given
        Usuario usuario = new Usuario();
        usuario.setUsername("loginuser");
        usuario.setPassword(passwordEncoder.encode("password123"));
        usuario.setRol(Rol.USER);
        usuarioRepository.save(usuario);

        AuthRequest request = new AuthRequest("loginuser", "wrongpassword");

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales inválidas"));
    }

    @Test
    @DisplayName("Debería rechazar login con usuario inexistente")
    void deberiaRechazarLoginConUsuarioInexistente() throws Exception {
        // Given
        AuthRequest request = new AuthRequest("nonexistent", "password123");

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    @DisplayName("Debería asignar rol USER por defecto si no se especifica")
    void deberiaAsignarRolUserPorDefecto() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest("defaultuser", "password123", null);

        // When
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Then
        Usuario usuario = usuarioRepository.findByUsername("defaultuser").orElseThrow();
        assert usuario.getRol() == Rol.USER;
    }
}

