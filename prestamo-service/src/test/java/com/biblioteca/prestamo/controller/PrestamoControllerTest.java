package com.biblioteca.prestamo.controller;

import com.biblioteca.prestamo.config.JwtFilter;
import com.biblioteca.prestamo.config.SecurityConfig;
import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.service.JwtService;
import com.biblioteca.prestamo.service.PrestamoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PrestamoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, SecurityConfig.class}))
class PrestamoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrestamoService prestamoService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "testuser")
    void prestar_deberiaRetornar201YPrestamoCreado() throws Exception {
        // Arrange
        Prestamo prestamo = new Prestamo(1L, "testuser", LocalDate.now());
        prestamo.setId(1L);

        when(prestamoService.prestarLibro(eq("testuser"), eq(1L))).thenReturn(prestamo);

        // Act & Assert
        mockMvc.perform(post("/prestamos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.libroId").value(1))
                .andExpect(jsonPath("$.devuelto").value(false));

        verify(prestamoService, times(1)).prestarLibro("testuser", 1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void devolver_deberiaRetornar200YPrestamoActualizado() throws Exception {
        // Arrange
        Prestamo prestamo = new Prestamo(1L, "testuser", LocalDate.now().minusDays(5));
        prestamo.setId(1L);
        prestamo.setFechaDevolucion(LocalDate.now());
        prestamo.setDevuelto(true);

        when(prestamoService.devolverLibro(1L)).thenReturn(prestamo);

        // Act & Assert
        mockMvc.perform(put("/prestamos/1/devolver")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.devuelto").value(true))
                .andExpect(jsonPath("$.fechaDevolucion").exists());

        verify(prestamoService, times(1)).devolverLibro(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void misPrestamos_deberiaRetornar200YListaDePrestamos() throws Exception {
        // Arrange
        Prestamo prestamo1 = new Prestamo(1L, "testuser", LocalDate.now().minusDays(5));
        prestamo1.setId(1L);

        Prestamo prestamo2 = new Prestamo(2L, "testuser", LocalDate.now().minusDays(3));
        prestamo2.setId(2L);
        prestamo2.setDevuelto(true);
        prestamo2.setFechaDevolucion(LocalDate.now().minusDays(1));

        when(prestamoService.obtenerPrestamosUsuario("testuser"))
                .thenReturn(Arrays.asList(prestamo1, prestamo2));

        // Act & Assert
        mockMvc.perform(get("/prestamos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].devuelto").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].devuelto").value(true));

        verify(prestamoService, times(1)).obtenerPrestamosUsuario("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void misPrestamosPaginados_deberiaRetornar200YPaginaDePrestamos() throws Exception {
        // Arrange
        Prestamo prestamo = new Prestamo(1L, "testuser", LocalDate.now().minusDays(2));
        prestamo.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo), pageable, 1);

        when(prestamoService.obtenerPrestamosUsuarioPaginados(eq("testuser"), any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/prestamos/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "fechaPrestamo")
                .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(prestamoService, times(1)).obtenerPrestamosUsuarioPaginados(eq("testuser"), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void misPrestamosActivos_deberiaRetornar200YPrestamosActivos() throws Exception {
        // Arrange
        Prestamo prestamo = new Prestamo(1L, "testuser", LocalDate.now().minusDays(2));
        prestamo.setId(1L);

        when(prestamoService.obtenerPrestamosActivos("testuser"))
                .thenReturn(Collections.singletonList(prestamo));

        // Act & Assert
        mockMvc.perform(get("/prestamos/activos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].devuelto").value(false));

        verify(prestamoService, times(1)).obtenerPrestamosActivos("testuser");
    }

    @Test
    @WithMockUser(username = "admin")
    void listarTodos_deberiaRetornar200YListaDeTodosPrestamos() throws Exception {
        // Arrange
        Prestamo prestamo1 = new Prestamo(1L, "user1", LocalDate.now().minusDays(5));
        prestamo1.setId(1L);

        Prestamo prestamo2 = new Prestamo(2L, "user2", LocalDate.now().minusDays(3));
        prestamo2.setId(2L);

        when(prestamoService.listarTodos()).thenReturn(Arrays.asList(prestamo1, prestamo2));

        // Act & Assert
        mockMvc.perform(get("/prestamos/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(prestamoService, times(1)).listarTodos();
    }

    @Test
    @WithMockUser(username = "admin")
    void listarTodosPaginados_deberiaRetornar200YPaginaDeTodosPrestamos() throws Exception {
        // Arrange
        Prestamo prestamo = new Prestamo(1L, "testuser", LocalDate.now().minusDays(2));
        prestamo.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo), pageable, 1);

        when(prestamoService.obtenerPrestamosPaginados(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/prestamos/todos/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(prestamoService, times(1)).obtenerPrestamosPaginados(any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "admin")
    void prestamosPorLibro_deberiaRetornar200YPaginaDePrestamos() throws Exception {
        // Arrange
        Prestamo prestamo = new Prestamo(1L, "testuser", LocalDate.now().minusDays(2));
        prestamo.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Prestamo> page = new PageImpl<>(Collections.singletonList(prestamo), pageable, 1);

        when(prestamoService.obtenerPrestamosPorLibro(eq(1L), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/prestamos/libro/1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].libroId").value(1));

        verify(prestamoService, times(1)).obtenerPrestamosPorLibro(eq(1L), any(Pageable.class));
    }
}

