package com.biblioteca.libro.service;

import com.biblioteca.libro.entity.Libro;
import com.biblioteca.libro.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    private final LibroRepository repo;

    public LibroService(LibroRepository repo) {
        this.repo = repo;
    }

    public Libro guardar(Libro l) {
        return repo.save(l);
    }

    public List<Libro> listar() {
        return repo.findAll();
    }

    public Optional<Libro> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Libro actualizar(Long id, Libro l) {
        return repo.findById(id)
                .map(existente -> {
                    if (l.getTitulo() != null) {
                        existente.setTitulo(l.getTitulo());
                    }
                    if (l.getAutor() != null) {
                        existente.setAutor(l.getAutor());
                    }
                    if (l.getStock() != null) {
                        existente.setStock(l.getStock());
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

    public void decrementarStock(Long libroId) {
        repo.findById(libroId).ifPresent(libro -> {
            if (libro.getStock() > 0) {
                libro.setStock(libro.getStock() - 1);
                repo.save(libro);
            } else {
                throw new RuntimeException("No hay stock disponible para el libro ID: " + libroId);
            }
        });
    }

    public void incrementarStock(Long libroId) {
        repo.findById(libroId).ifPresent(libro -> {
            libro.setStock(libro.getStock() + 1);
            repo.save(libro);
        });
    }

    public boolean verificarStock(Long libroId) {
        return repo.findById(libroId)
                .map(libro -> libro.getStock() > 0)
                .orElse(false);
    }
}
