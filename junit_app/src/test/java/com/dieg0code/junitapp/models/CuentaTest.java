package com.dieg0code.junitapp.models;

import com.dieg0code.junitapp.exceptions.DineroInsuficienteException;
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

        assertNotNull(actualValue);

        assertEquals(expectedValue, actualValue);

        assertTrue(actualValue.equals("Diego"));
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.999"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.999"));

        // assertNotEquals(cuenta, cuenta2);
        assertEquals(cuenta, cuenta2);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> cuenta.debito(new BigDecimal(1500)));
        String actual = exception.getMessage();
        String expected = "Dinero insuficiente";
        assertEquals(expected, actual);
    }

    @Test
    void transferirDineroCuentas() {
        Cuenta cuentaDiego = new Cuenta("Diego", new BigDecimal("1000.12345"));
        Cuenta cuentaJohn = new Cuenta("John", new BigDecimal("2500.12345"));

        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.transferir(cuentaJohn, cuentaDiego, new BigDecimal(500));
        assertEquals("2000.12345", cuentaJohn.getSaldo().toPlainString());
        assertEquals("1500.12345", cuentaDiego.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuenta() {
        Cuenta cuentaDiego = new Cuenta("Diego", new BigDecimal("1000.12345"));
        Cuenta cuentaJohn = new Cuenta("John", new BigDecimal("2500.12345"));

        Banco banco = new Banco();
        banco.addCuenta(cuentaDiego);
        banco.addCuenta(cuentaJohn);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuentaJohn, cuentaDiego, new BigDecimal(500));
        assertEquals("2000.12345", cuentaJohn.getSaldo().toPlainString());
        assertEquals("1500.12345", cuentaDiego.getSaldo().toPlainString());

        assertEquals(2, banco.getCuentas().size());
        assertTrue(banco.getCuentas().contains(cuentaDiego));
        assertTrue(banco.getCuentas().contains(cuentaJohn));

        assertEquals("Banco del Estado", cuentaDiego.getBanco().getNombre());
        assertEquals("Banco del Estado", cuentaJohn.getBanco().getNombre());

        assertEquals("Diego", banco.getCuentas().stream()
                .filter(c -> c.getPersona().equals("Diego"))
                .findFirst()
                .get()
                .getPersona()
        );

        assertTrue(banco.getCuentas().stream()
                .anyMatch(c -> c.getPersona().equals("Diego"))
        );

    }
}