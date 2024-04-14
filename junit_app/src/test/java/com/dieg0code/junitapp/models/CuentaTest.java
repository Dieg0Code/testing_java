package com.dieg0code.junitapp.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();

        assertEquals(expectedValue, actualValue);

        assertTrue(actualValue.equals("Diego"));
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

}