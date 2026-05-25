package com.biblioteca.prestamo.controller;

import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.service.PrestamoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PrestamoControllerExtendedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrestamoService service;

    @Test
    @WithMockUser(username = "testuser")
    void misPrestamosPaginados_DeberiaFuncionarConAsc() throws Exception {
        Page<Prestamo> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.obtenerPrestamosUsuarioPaginados(eq("testuser"), any())).thenReturn(page);

        mockMvc.perform(get("/prestamos/paginated")
                .param("direction", "asc"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void misPrestamosActivosPaginados_DeberiaRetornar200() throws Exception {
        Page<Prestamo> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.obtenerPrestamosActivosPaginados(anyString(), any())).thenReturn(page);
        mockMvc.perform(get("/prestamos/activos/paginated")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void misPrestamosDevueltosPaginados_DeberiaRetornar200() throws Exception {
        Page<Prestamo> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.obtenerPrestamosDevueltosPaginados(anyString(), any())).thenReturn(page);
        mockMvc.perform(get("/prestamos/devueltos/paginated")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodosPaginados_DeberiaFuncionarConAsc() throws Exception {
        Page<Prestamo> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.obtenerPrestamosPaginados(any())).thenReturn(page);
        mockMvc.perform(get("/prestamos/todos/paginated").param("direction", "asc")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void prestamosPorLibro_DeberiaRetornar200() throws Exception {
        Page<Prestamo> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.obtenerPrestamosPorLibro(anyLong(), any())).thenReturn(page);
        mockMvc.perform(get("/prestamos/libro/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void prestamosPorFecha_DeberiaRetornar200() throws Exception {
        Page<Prestamo> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.obtenerPrestamosPorFecha(any(), any(), any())).thenReturn(page);
        mockMvc.perform(get("/prestamos/fecha-rango")
                .param("fechaInicio", "2023-01-01")
                .param("fechaFin", "2023-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void prestamosVencidos_DeberiaRetornar200() throws Exception {
        Page<Prestamo> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.obtenerPrestamosVencidos(any())).thenReturn(page);
        mockMvc.perform(get("/prestamos/vencidos")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void buscarPorId_DeberiaRetornar404SiNoExiste() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/prestamos/99")).andExpect(status().isNotFound());
    }
}
