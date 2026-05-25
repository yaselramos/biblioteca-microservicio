package com.biblioteca.libro.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import static org.junit.jupiter.api.Assertions.*;

class CacheConfigTest {

    private final CacheConfig config = new CacheConfig();

    @Test
    void cacheManager_DeberiaTenerCachesDeLibros() {
        CacheManager manager = config.cacheManager();
        assertNotNull(manager);
        assertTrue(manager.getCacheNames().contains("libros"));
        assertTrue(manager.getCacheNames().contains("libro"));
    }
}
