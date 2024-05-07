package com.dieg0code.sprinboot_test;

import com.dieg0code.sprinboot_test.models.Banco;
import com.dieg0code.sprinboot_test.models.Cuenta;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;

public class Data {

    public static Optional<Cuenta> crearCuenta001() {
        return Optional.of(new Cuenta(1L, "Diego", new BigDecimal("1000")));
    }

    public static Optional<Cuenta> crearCuenta002() {
        return Optional.of(new Cuenta(2L, "John", new BigDecimal("2000")));
    }

    public static Optional<Banco> crearBanco() {
        return Optional.of(new Banco(1L, "Banco de la Nación", 0));
    }
}
