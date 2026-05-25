package com.biblioteca.prestamo.messaging;

import com.biblioteca.common.dto.PrestamoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PrestamoEventPublisher publisher;

    private PrestamoEvent event;

    @BeforeEach
    void setUp() {
        event = new PrestamoEvent();
        event.setLibroId(1L);
    }

    @Test
    void publishPrestamoEvent_DeberiaLlamarConvertAndSend() {
        publisher.publishPrestamoEvent(event);
        verify(rabbitTemplate, times(1))
                .convertAndSend("prestamo.queue", (event));
    }

    @Test
    void publishPrestamoEvent_DeberiaLanzarExcepcionCuandoFallaRabbit() {
        doThrow(new RuntimeException("Rabbit down"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), any(Object.class));

        assertThrows(RuntimeException.class, () -> publisher.publishPrestamoEvent(event));
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), any(Object.class));
    }
}
