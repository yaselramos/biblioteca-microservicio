package com.biblioteca.auth.service;

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

    public Usuario guardar(Usuario u) {
        return repo.save(u);
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Usuario actualizar(Long id, Usuario u) {
        return repo.findById(id)
                .map(existente -> {
                    if (u.getUsername() != null) {
                        existente.setUsername(u.getUsername());
                    }
                    if (u.getPassword() != null) {
                        existente.setPassword(u.getPassword());
                    }
                    if (u.getRol() != null) {
                        existente.setRol(u.getRol());
                    }
                    return repo.save(existente);
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
}
