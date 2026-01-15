package com.biblioteca.libro.controller;

import com.biblioteca.libro.entity.Libro;
import com.biblioteca.libro.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de Libros con soporte de paginación
 */
@RestController
@RequestMapping("/libros")
@Tag(name = "Libros", description = "API de gestión de libros con paginación")
public class LibroController {

    private final LibroService service;

    public LibroController(LibroService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo libro")
    public ResponseEntity<Libro> crear(@Valid @RequestBody Libro l) {
        Libro guardado = service.guardar(l);
        return ResponseEntity.status(201).body(guardado);
    }

    /**
     * Listar todos los libros SIN paginación (mantener compatibilidad)
     * Para datasets pequeños o clientes que no soporten paginación
     */
    @GetMapping
    @Operation(summary = "Listar todos los libros (sin paginación)")
    public ResponseEntity<List<Libro>> listar() {
        List<Libro> libros = service.listar();
        return ResponseEntity.ok(libros);
    }

    /**
     * Listar libros CON paginación (RECOMENDADO)
     * Ejemplo: GET /libros/paginated?page=0&size=10&sort=titulo,asc
     */
    @GetMapping("/paginated")
    @Operation(summary = "Listar libros con paginación (RECOMENDADO para grandes datasets)")
    public ResponseEntity<Page<Libro>> listarPaginado(
            @Parameter(description = "Número de página (empezando en 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo de ordenamiento (ej: titulo, autor, stock)")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Dirección de ordenamiento (asc o desc)")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Libro> libros = service.listarPaginado(pageable);

        return ResponseEntity.ok(libros);
    }

    /**
     * Buscar libros por título con paginación
     * Ejemplo: GET /libros/search/titulo?q=Java&page=0&size=10
     */
    @GetMapping("/search/titulo")
    @Operation(summary = "Buscar libros por título con paginación")
    public ResponseEntity<Page<Libro>> buscarPorTitulo(
            @Parameter(description = "Término de búsqueda en el título")
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo"));
        Page<Libro> libros = service.buscarPorTitulo(q, pageable);
        return ResponseEntity.ok(libros);
    }

    /**
     * Buscar libros por autor con paginación
     */
    @GetMapping("/search/autor")
    @Operation(summary = "Buscar libros por autor con paginación")
    public ResponseEntity<Page<Libro>> buscarPorAutor(
            @Parameter(description = "Término de búsqueda en el autor")
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("autor"));
        Page<Libro> libros = service.buscarPorAutor(q, pageable);
        return ResponseEntity.ok(libros);
    }

    /**
     * Búsqueda general (título o autor) con paginación
     * Ejemplo: GET /libros/search?q=spring&page=0&size=10
     */
    @GetMapping("/search")
    @Operation(summary = "Búsqueda general en título y autor con paginación")
    public ResponseEntity<Page<Libro>> buscar(
            @Parameter(description = "Término de búsqueda")
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Libro> libros = service.buscar(q, pageable);
        return ResponseEntity.ok(libros);
    }

    /**
     * Obtener libros disponibles (con stock) con paginación
     */
    @GetMapping("/disponibles")
    @Operation(summary = "Obtener libros con stock disponible")
    public ResponseEntity<Page<Libro>> obtenerDisponibles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo"));
        Page<Libro> libros = service.obtenerLibrosDisponibles(pageable);
        return ResponseEntity.ok(libros);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar libro por ID")
    public ResponseEntity<Libro> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un libro")
    public ResponseEntity<Libro> actualizar(@PathVariable Long id, @Valid @RequestBody Libro l) {
        Libro actualizado = service.actualizar(id, l);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un libro")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}


