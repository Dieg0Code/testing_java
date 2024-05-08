package com.dieg0code.sprinboot_test.controllers;

import com.dieg0code.sprinboot_test.models.Cuenta;
import com.dieg0code.sprinboot_test.models.TransactionDTO;
import com.dieg0code.sprinboot_test.services.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;


    // ***************************************************************************************
    // ***************************************************************************************
    // ***************************************************************************************

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> listar() {
        return cuentaService.findAll();
    }

    // ***************************************************************************************
    // ***************************************************************************************
    // ***************************************************************************************


    @Operation(
            /* -------------------------------------------------------------------------- */
            summary = "Retorna el detalle de una cuenta",
            description = "Retorna el detalle de una cuenta mediante un identificador único"
            /* -------------------------------------------------------------------------- */
    )
    @ApiResponse(
            /* ---------------------------------------------------- */
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Cuenta.class)
            )
            /* ---------------------------------------------------- */
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cuenta details(@PathVariable(name = "id") Long id) {
        return cuentaService.findById(id);
    }

    // ***************************************************************************************
    // ***************************************************************************************
    // ***************************************************************************************


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cuenta save(@RequestBody Cuenta cuenta) {
        return cuentaService.save(cuenta);
    }


    // ***************************************************************************************
    // ***************************************************************************************
    // ***************************************************************************************


    @Operation(
            /* -------------------------------------------------------------------------- */
            summary = "Realiza una transferencia entre cuentas",
            description = "Realiza una transferencia entre cuentas de un mismo banco"
            /* -------------------------------------------------------------------------- */
    )
    @ApiResponse(
            /* -------------------------------------------------------------------------- */
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TransactionDTO.class)
            )
            /* -------------------------------------------------------------------------- */
    )
    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransactionDTO dto) {
        cuentaService.transferir(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia realizada con éxito");
        response.put("transaction", dto);

        return ResponseEntity.ok(response);
    }
}
