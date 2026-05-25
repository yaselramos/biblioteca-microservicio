package com.biblioteca.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    // Clase anónima para testear la clase abstracta SwaggerConfig
    private final SwaggerConfig config = new SwaggerConfig() {
        @Override
        protected String getServiceTitle() {
            return "Test Title";
        }

        @Override
        protected String getServiceDescription() {
            return "Test Description";
        }
    };

    @Test
    void customOpenAPI_DeberiaTenerInfoCorrecta() {
        OpenAPI api = config.customOpenAPI();
        
        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Test Title", api.getInfo().getTitle());
        assertEquals("Test Description", api.getInfo().getDescription());
        assertEquals("1.0.0", api.getInfo().getVersion());
        
        // Verificar configuración de seguridad JWT
        assertNotNull(api.getComponents());
        assertNotNull(api.getComponents().getSecuritySchemes());
        assertTrue(api.getComponents().getSecuritySchemes().containsKey("Bearer Authentication"));
        assertEquals("bearer", api.getComponents().getSecuritySchemes().get("Bearer Authentication").getScheme());
    }

    @Test
    void defaultMethods_DeberianRetornarValoresPorDefecto() {
        SwaggerConfig defaultConfig = new SwaggerConfig();
        assertEquals("API Service", defaultConfig.getServiceTitle());
        assertEquals("API del Sistema de Biblioteca", defaultConfig.getServiceDescription());
    }
}
