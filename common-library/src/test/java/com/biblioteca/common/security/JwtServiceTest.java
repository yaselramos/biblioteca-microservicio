package com.biblioteca.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "9a4f2c8d3b5e1f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e"; // 256-bit secret

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
    }

    @Test
    void generarToken_DeberiaGenerarTokenValido() {
        String username = "testuser";
        String token = jwtService.generarToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtService.extraerUsuario(token));
    }

    @Test
    void extraerUsuario_DeberiaRetornarUsernameCorrecto() {
        String username = "admin";
        String token = jwtService.generarToken(username);

        String extraido = jwtService.extraerUsuario(token);

        assertEquals(username, extraido);
    }

    @Test
    void validarToken_DeberiaRetornarTrueSiEsValido() {
        String username = "user1";
        String token = jwtService.generarToken(username);

        boolean esValido = jwtService.validarToken(token, username);

        assertTrue(esValido);
    }

    @Test
    void validarToken_DeberiaRetornarFalseSiUsernameNoCoincide() {
        String username = "user1";
        String token = jwtService.generarToken(username);

        boolean esValido = jwtService.validarToken(token, "otroUser");

        assertFalse(esValido);
    }

    @Test
    void extraerRol_DeberiaRetornarRolCorrecto() {
        // Generar un token con rol manualmente para probar la extracción
        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject("user")
                .claim("rol", "ADMIN")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        String rol = jwtService.extraerRol(token);
        assertEquals("ADMIN", rol);
    }

    @Test
    void validarToken_DeberiaRetornarFalseSiTokenExpirado() {
        // Generar un token ya expirado
        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject("user")
                .setExpiration(new java.util.Date(System.currentTimeMillis() - 1000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtService.validarToken(token, "user"));
    }

    @Test
    void extraerRol_DeberiaRetornarNullSiNoExisteReclamo() {
        String token = jwtService.generarToken("user");
        assertNull(jwtService.extraerRol(token));
    }

    @Test
    void validarToken_DeberiaRetornarFalseSiTokenMalformado() {
        assertFalse(jwtService.validarToken("token.invalido.aqui", "user"));
    }

    @Test
    void validarToken_DeberiaRetornarFalseSiExceptionGenerica() {
        // Token con firma inválida o estructura incorrecta que no sea de expiración
        assertFalse(jwtService.validarToken(null, "user"));
    }

    @Test
    void extraerClaims_DeberiaManejarTokenInvalido() {
        assertThrows(Exception.class, () -> ReflectionTestUtils.invokeMethod(jwtService, "extraerClaims", "invalid"));
    }
}
