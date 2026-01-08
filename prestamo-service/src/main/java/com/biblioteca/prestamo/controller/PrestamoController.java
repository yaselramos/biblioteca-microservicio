package com.biblioteca.prestamo.controller;

import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.service.PrestamoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService service;

    public PrestamoController(PrestamoService service) {
        this.service = service;
    }

    @PostMapping("/{libroId}")
    public ResponseEntity<Prestamo> prestar(@PathVariable Long libroId, Authentication auth) {
        String username = auth.getName();
        Prestamo prestamo = service.prestarLibro(username, libroId);
        return ResponseEntity.status(201).body(prestamo);
    }

    @PutMapping("/{prestamoId}/devolver")
    public ResponseEntity<Prestamo> devolver(@PathVariable Long prestamoId) {
        Prestamo prestamo = service.devolverLibro(prestamoId);
        return ResponseEntity.ok(prestamo);
    }

    @GetMapping
    public ResponseEntity<List<Prestamo>> misPrestamos(Authentication auth) {
        String username = auth.getName();
        List<Prestamo> prestamos = service.obtenerPrestamosUsuario(username);
        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Prestamo>> misPrestamosActivos(Authentication auth) {
        String username = auth.getName();
        List<Prestamo> prestamos = service.obtenerPrestamosActivos(username);
        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Prestamo>> listarTodos() {
        List<Prestamo> prestamos = service.listarTodos();
        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
