package com.biblioteca.libro.messaging;

import com.biblioteca.libro.config.KafkaConfig;
import com.biblioteca.libro.dto.PrestamoEvent;
import com.biblioteca.libro.service.LibroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PrestamoEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PrestamoEventListener.class);

    private final LibroService libroService;

    public PrestamoEventListener(LibroService libroService) {
        this.libroService = libroService;
    }

    @KafkaListener(topics = KafkaConfig.PRESTAMO_TOPIC, groupId = "libro-group")
    public void handlePrestamoEvent(PrestamoEvent event) {
        logger.info("📥 Recibido evento de préstamo: {}", event);

        try {
            switch (event.getEventType()) {
                case PRESTAMO_CREADO:
                    logger.info("📖 Procesando préstamo creado para libro ID: {}", event.getLibroId());
                    libroService.decrementarStock(event.getLibroId());
                    logger.info("✅ Stock decrementado exitosamente para libro ID: {}", event.getLibroId());
                    break;

                case PRESTAMO_DEVUELTO:
                    logger.info("🔄 Procesando devolución para libro ID: {}", event.getLibroId());
                    libroService.incrementarStock(event.getLibroId());
                    logger.info("✅ Stock incrementado exitosamente para libro ID: {}", event.getLibroId());
                    break;

                default:
                    logger.warn("⚠️ Tipo de evento desconocido: {}", event.getEventType());
            }
        } catch (Exception e) {
            logger.error("❌ Error al procesar evento de préstamo: {}", e.getMessage(), e);
            // Aquí podrías implementar lógica de retry o dead letter queue
        }
    }
}
