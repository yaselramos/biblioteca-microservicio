package com.biblioteca.auth.dto;

public record AuthResponse(
    String token,
    String type,
    String username,
    String rol
) {
    public AuthResponse(String token, String username, String rol) {
        this(token, "Bearer", username, rol);
    }
}
