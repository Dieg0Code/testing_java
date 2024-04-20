package com.dieg0code.junitapp.models;

import com.dieg0code.junitapp.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class CuentaTest {

    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName() + " con Tags " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el método de pruebas");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Test
    @DisplayName("Probando nombre de la cuenta")
    void testNombreCuenta() {
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();

        assertNotNull(actualValue, "La cuenta no puede ser nula");

        assertEquals(expectedValue, actualValue, "El nombre de la cuenta no es el que se esperaba");

        assertTrue(actualValue.equals("Diego"), "El nombre de la cuenta debe ser Diego");
    }

    @Test
    @DisplayName("Probando saldo de la cuenta")
    void testSaldoCuenta() {
        assertNotNull(cuenta.getSaldo(), "El saldo no puede ser nulo");
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue(), "El saldo de la cuenta no es el esperado");
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0, "El saldo debe ser mayor a cero");
    }

    @Test
    @DisplayName("Probando que el saldo sea menor a cero")
    void testReferenciaCuenta() {
        cuenta = new Cuenta("John Doe", new BigDecimal("8900.999"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.999"));

        // assertNotEquals(cuenta, cuenta2);
        assertEquals(cuenta, cuenta2, "Las cuentas no son iguales");
    }

    @Test
    @DisplayName("[CUENTA :: TEST] Debito Cuenta")
    void testDebitoCuenta() {
        cuenta.debito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo(), "El saldo no puede ser nulo");
        assertEquals(900, cuenta.getSaldo().intValue(), "El saldo debe ser 900");
        assertEquals("900.12345", cuenta.getSaldo().toPlainString(), "El saldo debe ser 900.12345");
    }

    @Test
    @DisplayName("[CUENTA :: TEST] Credito Cuenta")
    void testCreditoCuenta() {
        cuenta.credito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo(), "El saldo no puede ser nulo");
        assertEquals(1100, cuenta.getSaldo().intValue(), "El saldo no es el esperado, se esperaba 1100, pero fue " + cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString(), "El saldo no es el esperado, se esperaba 1100.12345, pero fue " + cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("[CUENTA :: TEST] Dinero Insuficiente")
    void testDineroInsuficienteException() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> cuenta.debito(new BigDecimal(1500)));
        String actual = exception.getMessage();
        String expected = "Dinero insuficiente";
        assertEquals(expected, actual, "El mensaje de la excepción no es el esperado");
    }

    @Test
    @DisplayName("[CUENTA :: TEST] Transferir Dinero Cuentas")
    void transferirDineroCuentas() {
        Cuenta cuentaDiego = new Cuenta("Diego", new BigDecimal("1000.12345"));
        Cuenta cuentaJohn = new Cuenta("John", new BigDecimal("2500.12345"));

        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.transferir(cuentaJohn, cuentaDiego, new BigDecimal(500));
        assertEquals("2000.12345", cuentaJohn.getSaldo().toPlainString(), "El saldo de John no es el esperado, se esperaba 2000.12345, pero fue " + cuentaJohn.getSaldo().toPlainString());
        assertEquals("1500.12345", cuentaDiego.getSaldo().toPlainString(), "El saldo de Diego no es el esperado, se esperaba 1500.12345, pero fue " + cuentaDiego.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("[CUENTA :: TEST] Relación entre Cuenta y Banco")
    void testRelacionBancoCuenta() {
        Cuenta cuentaDiego = new Cuenta("Diego", new BigDecimal("1000.12345"));
        Cuenta cuentaJohn = new Cuenta("John", new BigDecimal("2500.12345"));

        Banco banco = new Banco();
        banco.addCuenta(cuentaDiego);
        banco.addCuenta(cuentaJohn);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuentaJohn, cuentaDiego, new BigDecimal(500));

        assertAll(
                () -> assertEquals("2000.12345", cuentaJohn.getSaldo().toPlainString(), "El saldo de John no es el esperado"),
                () -> assertEquals("1500.12345", cuentaDiego.getSaldo().toPlainString(), "El saldo de Diego no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(), "El banco no tiene las cuentas esperadas"),
                () -> assertTrue(banco.getCuentas().contains(cuentaDiego), "La cuenta de Diego no está en el banco"),
                () -> assertTrue(banco.getCuentas().contains(cuentaJohn), "La cuenta de John no está en el banco"),
                () -> assertEquals("Banco del Estado", cuentaDiego.getBanco().getNombre(), "El nombre del banco de Diego no es el esperado"),
                () -> assertEquals("Banco del Estado", cuentaJohn.getBanco().getNombre(), "El nombre del banco de John no es el esperado"),
                () -> assertEquals("Diego", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Diego"))
                        .findFirst()
                        .get()
                        .getPersona(),
                        "La cuenta de Diego no está en el banco"
                ),
                () -> assertTrue(banco.getCuentas().stream()
                        .anyMatch(c -> c.getPersona().equals("John")),
                        "La cuenta de John no está en el banco"
                )
        );

    }

    @Test
    @DisplayName("[CUENTA :: TEST] Probando Assupmtions")
    void testSaldoCuentaAssumption() {
        boolean esDev = "Dev".equals(System.getProperty("ENV"));

        assumeTrue(esDev);

        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
    }

    @Test
    @DisplayName("[CUENTA :: TEST] Probando Assupmtions 2")
    void testSaldoCuentaAssumption2() {
        boolean esDev = "Dev".equals(System.getProperty("ENV"));

        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
    }
}