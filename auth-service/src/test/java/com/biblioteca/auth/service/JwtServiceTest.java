package com.biblioteca.auth.service;

import com.biblioteca.common.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para JwtService")
class JwtServiceTest {


    private JwtService jwtService;
    private String secret = "mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong12345678";
    private String username = "testuser";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
    }

    @Test
    @DisplayName("Debería generar token JWT válido")
    void deberiaGenerarTokenValido() {
        // When
        String token = jwtService.generarToken(username);

        // Then
        assertNotNull(token);
        assertEquals(3,token.split("\\.").length ); // JWT tiene 3 partes
    }

    @Test
    @DisplayName("Debería extraer username del token")
    void deberiaExtraerUsernameDelToken() {
        // Given
        String token = jwtService.generarToken(username);

        // When
        String extractedUsername = jwtService.extraerUsuario(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Debería validar token correctamente")
    void deberiaValidarTokenCorrectamente() {
        // Given
        String token = jwtService.generarToken(username);

        // When
        boolean isValid = jwtService.validarToken(token, username);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Debería rechazar token con username incorrecto")
    void deberiaRechazarTokenConUsernameIncorrecto() {
        // Given
        String token = jwtService.generarToken(username);

        // When
        boolean isValid = jwtService.validarToken(token, "wronguser");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debería generar token con fecha de expiración")
    void deberiaGenerarTokenConFechaExpiracion() {
        // Given
        String token = jwtService.generarToken(username);

        // When
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date())); // Fecha de expiración es futura
    }

    @Test
    @DisplayName("Token debería expirar en 24 horas")
    void tokenDeberiaExpirarEn24Horas() {
        // Given
        String token = jwtService.generarToken(username);

        // When
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        long diffInMillis = expiration.getTime() - issuedAt.getTime();
        long diffInHours = diffInMillis / (1000 * 60 * 60);

        // Then
        assertEquals(24, diffInHours);
    }
}

