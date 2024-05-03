package com.dieg0code.sprinboot_test.repositories;

import com.dieg0code.sprinboot_test.models.Banco;

import java.util.List;

public interface BancoRepository {
    List<Banco> findAll();
    Banco findById(Long id);
    void update(Banco banco);
}
