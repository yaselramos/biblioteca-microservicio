package com.biblioteca.libro.repository;

import com.biblioteca.libro.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {
}
