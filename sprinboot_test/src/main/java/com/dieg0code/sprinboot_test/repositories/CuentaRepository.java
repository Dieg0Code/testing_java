package com.dieg0code.sprinboot_test.repositories;

import com.dieg0code.sprinboot_test.models.Cuenta;

import java.util.List;

public interface CuentaRepository {
    List<Cuenta> findAll();
    Cuenta findById(Long id);
    void update(Cuenta cuenta);
}
