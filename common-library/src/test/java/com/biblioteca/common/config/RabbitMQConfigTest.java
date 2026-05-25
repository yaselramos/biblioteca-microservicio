package com.biblioteca.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    void prestamoQueue_DeberiaTenerNombreCorrecto() {
        Queue queue = config.prestamoQueue();
        assertNotNull(queue);
        assertEquals("prestamo.queue", queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void jsonMessageConverter_DeberiaInstanciarse() {
        MessageConverter converter = config.jsonMessageConverter();
        assertNotNull(converter);
    }

    @Test
    void rabbitTemplate_DeberiaConfigurarseConConverter() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        RabbitTemplate template = config.rabbitTemplate(connectionFactory);
        
        assertNotNull(template);
        assertEquals(connectionFactory, template.getConnectionFactory());
        assertNotNull(template.getMessageConverter());
    }
}
