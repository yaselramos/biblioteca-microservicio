package com.biblioteca.prestamo.service;

import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.repository.PrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServicePaginationTest {

    @Mock
    private PrestamoRepository repo;

    @InjectMocks
    private PrestamoService prestamoService;

    private Pageable pageable;
    private Prestamo prestamo;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        prestamo = new Prestamo();
        prestamo.setId(1L);
        prestamo.setUsername("user_test");
    }

    @Test
    void obtenerPrestamosPaginados_DeberiaLlamarRepo() {
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo));
        when(repo.findAll(any(Pageable.class))).thenReturn(page);

        Page<Prestamo> result = prestamoService.obtenerPrestamosPaginados(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repo).findAll(pageable);
    }

    @Test
    void obtenerPrestamosUsuarioPaginados_DeberiaLlamarRepo() {
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo));
        when(repo.findByUsername(eq("user_test"), any(Pageable.class))).thenReturn(page);

        Page<Prestamo> result = prestamoService.obtenerPrestamosUsuarioPaginados("user_test", pageable);

        assertEquals(1, result.getTotalElements());
        verify(repo).findByUsername("user_test", pageable);
    }

    @Test
    void obtenerPrestamosActivosPaginados_DeberiaLlamarRepo() {
        when(repo.findByUsernameAndDevueltoFalse(anyString(), any(Pageable.class))).thenReturn(Page.empty());
        prestamoService.obtenerPrestamosActivosPaginados("user_test", pageable);
        verify(repo).findByUsernameAndDevueltoFalse("user_test", pageable);
    }

    @Test
    void obtenerPrestamosDevueltosPaginados_DeberiaLlamarRepo() {
        when(repo.findByUsernameAndDevueltoTrue(anyString(), any(Pageable.class))).thenReturn(Page.empty());
        prestamoService.obtenerPrestamosDevueltosPaginados("user_test", pageable);
        verify(repo).findByUsernameAndDevueltoTrue("user_test", pageable);
    }

    @Test
    void obtenerPrestamosPorLibro_DeberiaLlamarRepo() {
        when(repo.findByLibroId(anyLong(), any(Pageable.class))).thenReturn(Page.empty());
        prestamoService.obtenerPrestamosPorLibro(1L, pageable);
        verify(repo).findByLibroId(1L, pageable);
    }

    @Test
    void obtenerPrestamosPorFecha_DeberiaLlamarRepo() {
        LocalDate inicio = LocalDate.now().minusDays(7);
        LocalDate fin = LocalDate.now();
        when(repo.findByFechaPrestamoEntre(any(), any(), any())).thenReturn(Page.empty());
        
        prestamoService.obtenerPrestamosPorFecha(inicio, fin, pageable);
        
        verify(repo).findByFechaPrestamoEntre(inicio, fin, pageable);
    }

    @Test
    void obtenerPrestamosVencidos_DeberiaLlamarRepo() {
        when(repo.findPrestamosVencidos(any(), any())).thenReturn(Page.empty());
        prestamoService.obtenerPrestamosVencidos(pageable);
        verify(repo).findPrestamosVencidos(any(LocalDate.class), eq(pageable));
    }
}
