package com.biblioteca.prestamo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class TechConfigTest {

    private final CacheConfig cacheConfig = new CacheConfig();
    private final RestTemplateConfig restTemplateConfig = new RestTemplateConfig();

    @Test
    void cacheManager_DeberiaConfigurarse() {
        CacheManager manager = cacheConfig.cacheManager();
        assertNotNull(manager);
        assertTrue(manager.getCacheNames().contains("prestamos"));
    }

    @Test
    void caffeineBuilder_DeberiaTenerPropiedades() {
        Caffeine<Object, Object> builder = cacheConfig.caffeineCacheBuilder();
        assertNotNull(builder);
    }

    @Test
    void restTemplate_DeberiaInstanciarse() {
        RestTemplate template = restTemplateConfig.restTemplate();
        assertNotNull(template);
    }
}
