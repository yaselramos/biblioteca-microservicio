package com.biblioteca.prestamo.repository;

import com.biblioteca.prestamo.entity.Prestamo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PrestamoRepositoryTest {

    @Autowired
    private PrestamoRepository repository;

    @Test
    void findByUsername_DeberiaRetornarLista() {
        Prestamo p = new Prestamo();
        p.setUsername("user1");
        p.setLibroId(1L);
        p.setFechaPrestamo(LocalDate.now());
        repository.save(p);

        List<Prestamo> result = repository.findByUsername("user1");
        assertFalse(result.isEmpty());
        assertEquals("user1", result.get(0).getUsername());
    }

    @Test
    void findByFechaPrestamoEntre_DeberiaFiltrarCorrectamente() {
        LocalDate hoy = LocalDate.now();
        Prestamo p = new Prestamo();
        p.setUsername("user2");
        p.setLibroId(2L);
        p.setFechaPrestamo(hoy);
        repository.save(p);

        Page<Prestamo> result = repository.findByFechaPrestamoEntre(
                hoy.minusDays(1), hoy.plusDays(1), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findPrestamosVencidos_DeberiaRetornarSoloVencidos() {
        LocalDate hace40Dias = LocalDate.now().minusDays(40);
        
        Prestamo vencido = new Prestamo();
        vencido.setUsername("vencido");
        vencido.setLibroId(1L);
        vencido.setFechaPrestamo(hace40Dias);
        vencido.setDevuelto(false);
        repository.save(vencido);

        Prestamo alDia = new Prestamo();
        alDia.setUsername("aldia");
        alDia.setLibroId(2L);
        alDia.setFechaPrestamo(LocalDate.now());
        alDia.setDevuelto(false);
        repository.save(alDia);

        Page<Prestamo> result = repository.findPrestamosVencidos(
                LocalDate.now().minusDays(30), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("vencido", result.getContent().get(0).getUsername());
    }
}
