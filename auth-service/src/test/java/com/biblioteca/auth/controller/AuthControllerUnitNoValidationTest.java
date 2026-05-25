package com.biblioteca.auth.controller;

import com.biblioteca.auth.dto.RegisterRequest;
import com.biblioteca.auth.repository.UsuarioRepository;
import com.biblioteca.auth.service.Rol;
import com.biblioteca.common.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerUnitNoValidationTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_DeberiaManejarIllegalArgumentException_AlAsignarRol() {
        // Al llamar directamente al método del controlador, saltamos la validación @Pattern de Spring
        RegisterRequest request = new RegisterRequest("user", "pass", "INVALID_ROLE");
        
        when(usuarioRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(encoder.encode(any())).thenReturn("hash");
        when(jwtService.generarToken(any())).thenReturn("token");

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // El catch debería haber asignado el rol por defecto (USER)
        verify(usuarioRepository).save(argThat(u -> u.getRol() == Rol.USER));
    }

    @Test
    void register_DeberiaUsarRolPorDefectoSiVacio() {
        RegisterRequest request = new RegisterRequest("user", "pass", "");
        when(usuarioRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(encoder.encode(any())).thenReturn("hash");
        when(jwtService.generarToken(any())).thenReturn("token");

        authController.register(request);

        verify(usuarioRepository).save(argThat(u -> u.getRol() == Rol.USER));
    }
}
