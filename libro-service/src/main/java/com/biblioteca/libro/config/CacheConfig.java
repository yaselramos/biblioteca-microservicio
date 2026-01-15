package com.biblioteca.libro.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de caché para libro-service
 * Utiliza Caffeine como implementación de caché en memoria de alto rendimiento
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configuración del CacheManager con Caffeine
     * Define las políticas de expiración y tamaño máximo
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "libros",           // Caché para todos los libros
                "libro",            // Caché para libro individual por ID
                "librosDisponibles" // Caché para libros con stock
        );

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Configuración de Caffeine
     * - Máximo 1000 entradas por caché
     * - Expiración después de 10 minutos sin acceso
     * - Expiración después de 30 minutos desde creación
     */
    @Bean
    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats(); // Habilitar estadísticas de caché
    }
}

