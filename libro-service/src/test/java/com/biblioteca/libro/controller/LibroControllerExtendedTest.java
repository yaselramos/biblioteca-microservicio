package com.biblioteca.libro.controller;

import com.biblioteca.libro.entity.Libro;
import com.biblioteca.libro.service.LibroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LibroControllerExtendedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibroService service;

    @Test
    @WithMockUser
    void listarPaginado_DeberiaFuncionarConDesc() throws Exception {
        Libro l = new Libro(1L, "T", "A", 1);
        Page<Libro> page = new PageImpl<>(java.util.List.of(l), PageRequest.of(0, 10), 1);
        when(service.listarPaginado(any())).thenReturn(page);
        
        mockMvc.perform(get("/libros/paginated").param("direction", "desc"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void buscarPorTitulo_DeberiaRetornar200() throws Exception {
        Page<Libro> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.buscarPorTitulo(anyString(), any())).thenReturn(page);
        mockMvc.perform(get("/libros/search/titulo").param("q", "Java")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void buscarPorAutor_DeberiaRetornar200() throws Exception {
        Page<Libro> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.buscarPorAutor(anyString(), any())).thenReturn(page);
        mockMvc.perform(get("/libros/search/autor").param("q", "Autor")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void buscarGeneral_DeberiaRetornar200() throws Exception {
        Page<Libro> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.buscar(anyString(), any())).thenReturn(page);
        mockMvc.perform(get("/libros/search").param("q", "Spring")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerDisponibles_DeberiaRetornar200() throws Exception {
        Page<Libro> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.obtenerLibrosDisponibles(any())).thenReturn(page);
        mockMvc.perform(get("/libros/disponibles")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void buscarPorId_DeberiaRetornar404SiNoExiste() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/libros/99")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_DeberiaRetornar404SiNoExiste() throws Exception {
        when(service.actualizar(eq(99L), any())).thenReturn(null);
        mockMvc.perform(put("/libros/99")
                .contentType("application/json")
                .content("{\"titulo\":\"T\",\"autor\":\"A\",\"stock\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void listar_DeberiaRetornarListaCompleta() throws Exception {
        when(service.listar()).thenReturn(java.util.List.of(new Libro(1L, "T", "A", 1)));
        mockMvc.perform(get("/libros")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_DeberiaRetornar404SiNoExiste() throws Exception {
        when(service.eliminar(99L)).thenReturn(false);
        mockMvc.perform(delete("/libros/99")).andExpect(status().isNotFound());
    }
}
