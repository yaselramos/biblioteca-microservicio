package com.biblioteca.libro.messaging;

import com.biblioteca.common.dto.PrestamoEvent;
import com.biblioteca.libro.service.LibroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoEventListenerTest {

    @Mock
    private LibroService libroService;

    @InjectMocks
    private PrestamoEventListener prestamoEventListener;

    private PrestamoEvent event;

    @BeforeEach
    void setUp() {
        event = new PrestamoEvent();
        event.setLibroId(1L);
    }

    @Test
    void handlePrestamoEvent_DeberiaDecrementarStockCuandoEsPrestamoCreado() {
        event.setEventType(PrestamoEvent.EventType.PRESTAMO_CREADO);

        prestamoEventListener.handlePrestamoEvent(event);

        verify(libroService, times(1)).decrementarStock(1L);
        verify(libroService, never()).incrementarStock(anyLong());
    }

    @Test
    void handlePrestamoEvent_DeberiaIncrementarStockCuandoEsPrestamoDevuelto() {
        event.setEventType(PrestamoEvent.EventType.PRESTAMO_DEVUELTO);

        prestamoEventListener.handlePrestamoEvent(event);

        verify(libroService, times(1)).incrementarStock(1L);
        verify(libroService, never()).decrementarStock(anyLong());
    }

    @Test
    void handlePrestamoEvent_NoDeberiaHacerNadaCuandoEventoEsNulo() {
        event.setEventType(null); 
        prestamoEventListener.handlePrestamoEvent(event);
        verify(libroService, never()).decrementarStock(anyLong());
        verify(libroService, never()).incrementarStock(anyLong());
    }

    @Test
    void handlePrestamoEvent_DeberiaLlamarDecrementarStockEnSwitch() {
        event.setEventType(PrestamoEvent.EventType.PRESTAMO_CREADO);
        prestamoEventListener.handlePrestamoEvent(event);
        verify(libroService).decrementarStock(1L);
    }

    @Test
    void handlePrestamoEvent_DeberiaLlamarIncrementarStockEnSwitch() {
        event.setEventType(PrestamoEvent.EventType.PRESTAMO_DEVUELTO);
        prestamoEventListener.handlePrestamoEvent(event);
        verify(libroService).incrementarStock(1L);
    }

    @Test
    void handlePrestamoEvent_DeberiaCapturarExcepcionYNoLanzarla() {
        event.setEventType(PrestamoEvent.EventType.PRESTAMO_CREADO);
        doThrow(new RuntimeException("Error de DB")).when(libroService).decrementarStock(1L);

        // No debería lanzar excepción porque tiene un bloque try-catch
        prestamoEventListener.handlePrestamoEvent(event);

        verify(libroService, times(1)).decrementarStock(1L);
    }
}
