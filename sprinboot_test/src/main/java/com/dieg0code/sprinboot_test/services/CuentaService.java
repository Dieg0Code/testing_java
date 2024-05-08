package com.dieg0code.sprinboot_test.services;

import com.dieg0code.sprinboot_test.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {
    List<Cuenta> findAll();

    Cuenta findById(Long id);

    Cuenta save(Cuenta cuenta);

    int revisarTotalTransferencias(Long bancoId);

    BigDecimal revisarSaldo(Long cuentaId);

    void transferir(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto, Long bancoId);
}
