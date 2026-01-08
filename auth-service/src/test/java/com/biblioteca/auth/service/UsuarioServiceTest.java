package com.biblioteca.auth.service;

import com.biblioteca.auth.entity.Usuario;
import com.biblioteca.auth.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USER);
    }

    @Test
    @DisplayName("Debería guardar un usuario exitosamente")
    void deberiaGuardarUsuarioExitosamente() {
        // Given
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = usuarioService.guardar(usuario);

        // Then
        assertNotNull(resultado);
        assertEquals("testuser", resultado.getUsername());
        assertEquals(Rol.USER, resultado.getRol());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Debería listar todos los usuarios")
    void deberiaListarTodosLosUsuarios() {
        // Given
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("admin");
        usuario2.setRol(Rol.ADMIN);

        List<Usuario> usuarios = Arrays.asList(usuario, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When
        List<Usuario> resultado = usuarioService.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar usuario por ID exitosamente")
    void deberiaBuscarUsuarioPorId() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // When
        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería retornar vacío cuando usuario no existe")
    void deberiaRetornarVacioCuandoUsuarioNoExiste() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Usuario> resultado = usuarioService.buscarPorId(999L);

        // Then
        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debería actualizar usuario exitosamente")
    void deberiaActualizarUsuarioExitosamente() {
        // Given
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setUsername("updateduser");
        usuarioActualizado.setPassword("newpassword");
        usuarioActualizado.setRol(Rol.ADMIN);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = usuarioService.actualizar(1L, usuarioActualizado);

        // Then
        assertNotNull(resultado);
        assertEquals("updateduser", resultado.getUsername());
        assertEquals("newpassword", resultado.getPassword());
        assertEquals(Rol.ADMIN, resultado.getRol());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Debería retornar null cuando actualizar usuario inexistente")
    void deberiaRetornarNullCuandoActualizarUsuarioInexistente() {
        // Given
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setUsername("updateduser");

        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Usuario resultado = usuarioService.actualizar(999L, usuarioActualizado);

        // Then
        assertNull(resultado);
        verify(usuarioRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar usuario exitosamente")
    void deberiaEliminarUsuarioExitosamente() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        // When
        boolean resultado = usuarioService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debería retornar false al eliminar usuario inexistente")
    void deberiaRetornarFalseAlEliminarUsuarioInexistente() {
        // Given
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        // When
        boolean resultado = usuarioService.eliminar(999L);

        // Then
        assertFalse(resultado);
        verify(usuarioRepository, times(1)).existsById(999L);
        verify(usuarioRepository, never()).deleteById(any());
    }
}

