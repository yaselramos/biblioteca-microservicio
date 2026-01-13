package com.biblioteca.auth.controller;

import com.biblioteca.auth.dto.AuthRequest;
import com.biblioteca.auth.dto.AuthResponse;
import com.biblioteca.auth.dto.RegisterRequest;
import com.biblioteca.auth.entity.Usuario;
import com.biblioteca.auth.repository.UsuarioRepository;
import com.biblioteca.auth.service.JwtService;
import com.biblioteca.auth.service.Rol;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        Usuario user = usuarioRepository.findByUsername(request.username())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }

        if (!encoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String token = jwtService.generarToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRol().name()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.status(409).body("El usuario ya existe");
        }

        // Crear el nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.username());
        nuevoUsuario.setPassword(encoder.encode(request.password()));
        nuevoUsuario.setEmail(request.email());

        // Asignar rol (por defecto USER si no se especifica)
        if (request.rol() != null && !request.rol().isEmpty()) {
            try {
                nuevoUsuario.setRol(Rol.valueOf(request.rol().toUpperCase()));
            } catch (IllegalArgumentException e) {
                nuevoUsuario.setRol(Rol.USER);
            }
        } else {
            nuevoUsuario.setRol(Rol.USER);
        }

        // Guardar el usuario
        Usuario savedUser = usuarioRepository.save(nuevoUsuario);

        // Devolver el usuario (sin contraseña)
        return ResponseEntity.status(201).body(savedUser);
    }
}

