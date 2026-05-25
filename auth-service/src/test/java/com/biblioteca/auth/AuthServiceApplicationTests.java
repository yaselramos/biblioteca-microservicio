package com.biblioteca.auth;

import com.biblioteca.AuthServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
        // Assert that the context loads successfully
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {});
    }

    @Test
    void mainMethodTest() {
        // Ejecuta el método main para cubrir la línea de inicio de la aplicación
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> AuthServiceApplication.main(new String[]{}));
    }
}
