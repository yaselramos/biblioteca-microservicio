package com.biblioteca.prestamo.messaging;

import com.biblioteca.prestamo.config.RabbitMQConfig;
import com.biblioteca.prestamo.dto.PrestamoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PrestamoEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PrestamoEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public PrestamoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPrestamoEvent(PrestamoEvent event) {
        logger.info("📤 Publicando evento de préstamo: {}", event);
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.PRESTAMO_QUEUE, event);
            logger.info("✅ Evento publicado exitosamente");
        } catch (Exception e) {
            logger.error("❌ Error al publicar evento: {}", e.getMessage(), e);
            throw new RuntimeException("Error al publicar evento de préstamo", e);
        }
    }
}

