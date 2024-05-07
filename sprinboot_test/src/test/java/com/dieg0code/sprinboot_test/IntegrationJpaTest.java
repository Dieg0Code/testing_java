package com.dieg0code.sprinboot_test;

import com.dieg0code.sprinboot_test.models.Cuenta;
import com.dieg0code.sprinboot_test.repositories.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegrationJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void findByIdTest() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Diego", cuenta.orElseThrow().getNombre());
    }

    @Test
    void findByNombreTest() {
        Optional<Cuenta> cuenta = cuentaRepository.findByNombre("Diego");
        assertTrue(cuenta.isPresent());
        assertEquals("Diego", cuenta.orElseThrow().getNombre());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void findByNombreThrowExceptionTest() {
        Optional<Cuenta> cuenta = cuentaRepository.findByNombre("Don Ramon");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void findAllTest() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void saveCuentaTest() {
        // Given
        Cuenta cuentaFabian = new Cuenta(null, "Fabian", new BigDecimal("3000"));

        // When
        Cuenta cuenta = cuentaRepository.save(cuentaFabian);

        // Then
        assertEquals("Fabian", cuenta.getNombre());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
    }

    @Test
    void updateTest() {
        // Given
        Cuenta cuenta = cuentaRepository.findById(1L).orElseThrow();
        cuenta.setSaldo(new BigDecimal("500"));

        // When
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        // Then
        assertEquals("500", cuentaActualizada.getSaldo().toPlainString());
    }

    @Test
    void deleteTest() {
        // Given
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();

        // When
        cuentaRepository.delete(cuenta);

        // Then
        assertThrows(NoSuchElementException.class, () -> cuentaRepository.findById(2L).orElseThrow());
        assertEquals(1, cuentaRepository.findAll().size());
    }
}
