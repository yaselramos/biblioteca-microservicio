package com.biblioteca.prestamo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de caché para prestamo-service
 * Optimiza consultas frecuentes de préstamos
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "prestamos",           // Caché para todos los préstamos
                "prestamo",            // Caché para préstamo individual
                "prestamosUsuario",    // Caché para préstamos por usuario
                "prestamosActivos"     // Caché para préstamos activos
        );

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Configuración de Caffeine
     * - Expiración más corta para datos que cambian frecuentemente
     * - 5 minutos sin acceso
     * - 15 minutos máximo de vida
     */
    @Bean
    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .recordStats();
    }
}

