package com.dieg0code.sprinboot_test;

import com.dieg0code.sprinboot_test.models.Banco;
import com.dieg0code.sprinboot_test.models.Cuenta;

import java.math.BigDecimal;

public class Data {
    public static final Cuenta CUENTA_001 = new Cuenta(1L, "Diego", new BigDecimal("1000"));
    public static final Cuenta CUENTA_002 = new Cuenta(2L, "Pedro", new BigDecimal("2000"));
    public static final Banco BANCO = new Banco(1L, "Banco de la plaza", 0);
}
