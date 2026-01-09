package com.biblioteca.prestamo.messaging;

import com.biblioteca.prestamo.config.KafkaConfig;
import com.biblioteca.prestamo.dto.PrestamoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PrestamoEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PrestamoEventPublisher.class);

    private final KafkaTemplate<String, PrestamoEvent> kafkaTemplate;

    public PrestamoEventPublisher(KafkaTemplate<String, PrestamoEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPrestamoEvent(PrestamoEvent event) {
        logger.info("📤 Publicando evento de préstamo: {}", event);
        try {
            kafkaTemplate.send(KafkaConfig.PRESTAMO_TOPIC, event);
            logger.info("✅ Evento publicado exitosamente");
        } catch (Exception e) {
            logger.error("❌ Error al publicar evento: {}", e.getMessage(), e);
            throw new RuntimeException("Error al publicar evento de préstamo", e);
        }
    }
}
