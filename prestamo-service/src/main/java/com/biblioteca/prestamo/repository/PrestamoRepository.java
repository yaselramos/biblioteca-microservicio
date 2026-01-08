package com.biblioteca.prestamo.repository;

import com.biblioteca.prestamo.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsername(String username);

    List<Prestamo> findByUsernameAndDevueltoFalse(String username);

    List<Prestamo> findByLibroId(Long libroId);
}

