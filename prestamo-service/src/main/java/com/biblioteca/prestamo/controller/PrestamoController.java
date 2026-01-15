package com.biblioteca.prestamo.controller;

import com.biblioteca.prestamo.entity.Prestamo;
import com.biblioteca.prestamo.service.PrestamoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de Préstamos con soporte de paginación
 */
@RestController
@RequestMapping("/prestamos")
@Tag(name = "Préstamos", description = "API de gestión de préstamos con paginación")
public class PrestamoController {

    private final PrestamoService service;

    public PrestamoController(PrestamoService service) {
        this.service = service;
    }

    @PostMapping("/{libroId}")
    @Operation(summary = "Crear un nuevo préstamo")
    public ResponseEntity<Prestamo> prestar(@PathVariable Long libroId, Authentication auth) {
        String username = auth.getName();
        Prestamo prestamo = service.prestarLibro(username, libroId);
        return ResponseEntity.status(201).body(prestamo);
    }

    @PutMapping("/{prestamoId}/devolver")
    @Operation(summary = "Devolver un libro prestado")
    public ResponseEntity<Prestamo> devolver(@PathVariable Long prestamoId) {
        Prestamo prestamo = service.devolverLibro(prestamoId);
        return ResponseEntity.ok(prestamo);
    }

    /**
     * Mis préstamos SIN paginación (mantener compatibilidad)
     */
    @GetMapping
    @Operation(summary = "Obtener mis préstamos (sin paginación)")
    public ResponseEntity<List<Prestamo>> misPrestamos(Authentication auth) {
        String username = auth.getName();
        List<Prestamo> prestamos = service.obtenerPrestamosUsuario(username);
        return ResponseEntity.ok(prestamos);
    }

    /**
     * Mis préstamos CON paginación (RECOMENDADO)
     */
    @GetMapping("/paginated")
    @Operation(summary = "Obtener mis préstamos con paginación (RECOMENDADO)")
    public ResponseEntity<Page<Prestamo>> misPrestamosPaginados(
            Authentication auth,
            @Parameter(description = "Número de página (empezando en 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento")
            @RequestParam(defaultValue = "fechaPrestamo") String sortBy,
            @Parameter(description = "Dirección de ordenamiento (asc o desc)")
            @RequestParam(defaultValue = "desc") String direction
    ) {
        String username = auth.getName();
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Prestamo> prestamos = service.obtenerPrestamosUsuarioPaginados(username, pageable);

        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener mis préstamos activos (sin paginación)")
    public ResponseEntity<List<Prestamo>> misPrestamosActivos(Authentication auth) {
        String username = auth.getName();
        List<Prestamo> prestamos = service.obtenerPrestamosActivos(username);
        return ResponseEntity.ok(prestamos);
    }

    /**
     * Mis préstamos activos CON paginación
     */
    @GetMapping("/activos/paginated")
    @Operation(summary = "Obtener mis préstamos activos con paginación")
    public ResponseEntity<Page<Prestamo>> misPrestamosActivosPaginados(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String username = auth.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPrestamo"));
        Page<Prestamo> prestamos = service.obtenerPrestamosActivosPaginados(username, pageable);
        return ResponseEntity.ok(prestamos);
    }

    /**
     * Mis préstamos devueltos CON paginación
     */
    @GetMapping("/devueltos/paginated")
    @Operation(summary = "Obtener mis préstamos devueltos con paginación")
    public ResponseEntity<Page<Prestamo>> misPrestamosDevueltosPaginados(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String username = auth.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaDevolucion"));
        Page<Prestamo> prestamos = service.obtenerPrestamosDevueltosPaginados(username, pageable);
        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/todos")
    @Operation(summary = "Listar todos los préstamos (sin paginación - ADMIN)")
    public ResponseEntity<List<Prestamo>> listarTodos() {
        List<Prestamo> prestamos = service.listarTodos();
        return ResponseEntity.ok(prestamos);
    }

    /**
     * Todos los préstamos CON paginación (ADMIN)
     */
    @GetMapping("/todos/paginated")
    @Operation(summary = "Listar todos los préstamos con paginación (ADMIN)")
    public ResponseEntity<Page<Prestamo>> listarTodosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaPrestamo") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Prestamo> prestamos = service.obtenerPrestamosPaginados(pageable);

        return ResponseEntity.ok(prestamos);
    }

    /**
     * Préstamos por libro con paginación
     */
    @GetMapping("/libro/{libroId}")
    @Operation(summary = "Obtener préstamos de un libro específico con paginación")
    public ResponseEntity<Page<Prestamo>> prestamosPorLibro(
            @PathVariable Long libroId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPrestamo"));
        Page<Prestamo> prestamos = service.obtenerPrestamosPorLibro(libroId, pageable);
        return ResponseEntity.ok(prestamos);
    }

    /**
     * Préstamos por rango de fechas con paginación
     */
    @GetMapping("/fecha-rango")
    @Operation(summary = "Obtener préstamos por rango de fechas con paginación")
    public ResponseEntity<Page<Prestamo>> prestamosPorFecha(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPrestamo"));
        Page<Prestamo> prestamos = service.obtenerPrestamosPorFecha(fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(prestamos);
    }

    /**
     * Préstamos vencidos con paginación
     */
    @GetMapping("/vencidos")
    @Operation(summary = "Obtener préstamos vencidos (más de 30 días sin devolver)")
    public ResponseEntity<Page<Prestamo>> prestamosVencidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fechaPrestamo"));
        Page<Prestamo> prestamos = service.obtenerPrestamosVencidos(pageable);
        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar préstamo por ID")
    public ResponseEntity<Prestamo> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


