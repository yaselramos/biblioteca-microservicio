package com.biblioteca.libro.messaging;


import com.biblioteca.common.dto.PrestamoEvent;
import com.biblioteca.common.config.RabbitMQConfig;
import com.biblioteca.libro.service.LibroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PrestamoEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PrestamoEventListener.class);

    private final LibroService libroService;

    public PrestamoEventListener(LibroService libroService) {
        this.libroService = libroService;
    }

    @RabbitListener(queues = RabbitMQConfig.PRESTAMO_QUEUE)
    public void handlePrestamoEvent(PrestamoEvent event) {
        try {
            logger.info("📥 Recibido evento de préstamo: {}", event);

            if (event.getEventType() == null) {
                logger.warn("⚠️ Evento recibido sin tipo (eventType es null). Ignorando.");
                return;
            }

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

