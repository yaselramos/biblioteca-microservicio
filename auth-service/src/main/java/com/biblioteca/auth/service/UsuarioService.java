package com.biblioteca.auth.service;

import com.biblioteca.auth.dto.UsuarioDto;
import com.biblioteca.auth.entity.Usuario;
import com.biblioteca.auth.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public UsuarioDto guardar(UsuarioDto u) {
        Usuario user = new Usuario();
        user.setUsername(u.getUsuario());
        user.setPassword(u.getPassword());
        user.setRol(u.getRol());
        Usuario persistido = repo.save(user);
        return toDto(persistido);
    }

    public List<UsuarioDto> listar() {
        return repo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<UsuarioDto> buscarPorId(Long id) {
        return repo.findById(id).map(this::toDto);
    }

    public UsuarioDto actualizar(Long id, UsuarioDto u) {
        return repo.findById(id)
                .map(existente -> {
                    if (u.getUsuario() != null) {
                        existente.setUsername(u.getUsuario());
                    }
                    if (u.getPassword() != null) {
                        existente.setPassword(u.getPassword());
                    }
                    if (u.getRol() != null) {
                        existente.setRol(u.getRol());
                    }
                    return toDto(repo.save(existente));
                })
                .orElse(null);
    }

    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private UsuarioDto toDto(Usuario user) {
        return new UsuarioDto(user.getId(), user.getUsername(), user.getPassword(), user.getRol());
    }
}
