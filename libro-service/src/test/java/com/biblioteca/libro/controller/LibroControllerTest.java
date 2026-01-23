package com.biblioteca.libro.controller;

import com.biblioteca.libro.config.JwtFilter;
import com.biblioteca.libro.entity.Libro;
import com.biblioteca.libro.service.JwtService;
import com.biblioteca.libro.service.LibroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = LibroController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class LibroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibroService libroService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void crear_deberiaRetornar201YLibroCreado() throws Exception {
        // Arrange
        Libro libroCrear = new Libro("Java Efectivo", "Joshua Bloch", 5);
        Libro libroCreado = new Libro("Java Efectivo", "Joshua Bloch", 5);
        libroCreado.setId(1L);

        when(libroService.guardar(any(Libro.class))).thenReturn(libroCreado);

        // Act & Assert
        mockMvc.perform(post("/libros")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroCrear)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Java Efectivo"))
                .andExpect(jsonPath("$.autor").value("Joshua Bloch"))
                .andExpect(jsonPath("$.stock").value(5));

        verify(libroService, times(1)).guardar(any(Libro.class));
    }

    @Test
    @WithMockUser
    void listar_deberiaRetornar200YListaDeLibros() throws Exception {
        // Arrange
        Libro libro1 = new Libro("Java Efectivo", "Joshua Bloch", 5);
        libro1.setId(1L);

        Libro libro2 = new Libro("Spring Boot en Accion", "Craig Walls", 3);
        libro2.setId(2L);

        when(libroService.listar()).thenReturn(Arrays.asList(libro1, libro2));

        // Act & Assert
        mockMvc.perform(get("/libros"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Java Efectivo"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].titulo").value("Spring Boot en Accion"));

        verify(libroService, times(1)).listar();
    }

    @Test
    @WithMockUser
    void buscarPorId_existente_deberiaRetornar200YLibro() throws Exception {
        // Arrange
        Libro libro = new Libro("Java Efectivo", "Joshua Bloch", 5);
        libro.setId(1L);

        when(libroService.buscarPorId(1L)).thenReturn(Optional.of(libro));

        // Act & Assert
        mockMvc.perform(get("/libros/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Java Efectivo"))
                .andExpect(jsonPath("$.autor").value("Joshua Bloch"))
                .andExpect(jsonPath("$.stock").value(5));

        verify(libroService, times(1)).buscarPorId(1L);
    }

    @Test
    @WithMockUser
    void buscarPorId_noExistente_deberiaRetornar404() throws Exception {
        // Arrange
        when(libroService.buscarPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/libros/999"))
                .andExpect(status().isNotFound());

        verify(libroService, times(1)).buscarPorId(999L);
    }

    @Test
    @WithMockUser
    void actualizar_existente_deberiaRetornar200YLibroActualizado() throws Exception {
        // Arrange
        Libro libroActualizar = new Libro("Java Efectivo 2da Edicion", "Joshua Bloch", 8);
        Libro libroActualizado = new Libro("Java Efectivo 2da Edicion", "Joshua Bloch", 8);
        libroActualizado.setId(1L);

        when(libroService.actualizar(eq(1L), any(Libro.class))).thenReturn(libroActualizado);

        // Act & Assert
        mockMvc.perform(put("/libros/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroActualizar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Java Efectivo 2da Edicion"))
                .andExpect(jsonPath("$.autor").value("Joshua Bloch"))
                .andExpect(jsonPath("$.stock").value(8));

        verify(libroService, times(1)).actualizar(eq(1L), any(Libro.class));
    }

    @Test
    @WithMockUser
    void actualizar_noExistente_deberiaRetornar404() throws Exception {
        // Arrange
        Libro libro = new Libro("Libro No Existente", "Autor Desconocido", 1);

        when(libroService.actualizar(eq(999L), any(Libro.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/libros/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libro)))
                .andExpect(status().isNotFound());

        verify(libroService, times(1)).actualizar(eq(999L), any(Libro.class));
    }

    @Test
    @WithMockUser
    void eliminar_existente_deberiaRetornar204() throws Exception {
        // Arrange
        when(libroService.eliminar(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/libros/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(libroService, times(1)).eliminar(1L);
    }

    @Test
    @WithMockUser
    void eliminar_noExistente_deberiaRetornar404() throws Exception {
        // Arrange
        when(libroService.eliminar(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/libros/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(libroService, times(1)).eliminar(999L);
    }

    @Test
    @WithMockUser
    void listarPaginado_deberiaRetornar200YPaginaDeLibros() throws Exception {
        // Arrange
        Libro libro = new Libro("Java Efectivo", "Joshua Bloch", 5);
        libro.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroService.listarPaginado(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/libros/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "titulo")
                .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("Java Efectivo"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(libroService, times(1)).listarPaginado(any(Pageable.class));
    }

    @Test
    @WithMockUser
    void buscarPorTitulo_deberiaRetornar200YPaginaDeLibros() throws Exception {
        // Arrange
        Libro libro = new Libro("Java Efectivo", "Joshua Bloch", 5);
        libro.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroService.buscarPorTitulo(eq("Java"), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/libros/search/titulo")
                .param("q", "Java")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("Java Efectivo"));

        verify(libroService, times(1)).buscarPorTitulo(eq("Java"), any(Pageable.class));
    }

    @Test
    @WithMockUser
    void buscarPorAutor_deberiaRetornar200YPaginaDeLibros() throws Exception {
        // Arrange
        Libro libro = new Libro("Java Efectivo", "Joshua Bloch", 5);
        libro.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroService.buscarPorAutor(eq("Joshua"), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/libros/search/autor")
                .param("q", "Joshua")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].autor").value("Joshua Bloch"));

        verify(libroService, times(1)).buscarPorAutor(eq("Joshua"), any(Pageable.class));
    }

    @Test
    @WithMockUser
    void obtenerDisponibles_deberiaRetornar200YLibrosConStock() throws Exception {
        // Arrange
        Libro libro = new Libro("Java Efectivo", "Joshua Bloch", 5);
        libro.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Libro> page = new PageImpl<>(Collections.singletonList(libro), pageable, 1);

        when(libroService.obtenerLibrosDisponibles(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/libros/disponibles")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].stock").value(5));

        verify(libroService, times(1)).obtenerLibrosDisponibles(any(Pageable.class));
    }
}

