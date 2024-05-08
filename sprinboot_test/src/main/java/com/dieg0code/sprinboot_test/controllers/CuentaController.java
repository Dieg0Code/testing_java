package com.dieg0code.sprinboot_test.controllers;

import com.dieg0code.sprinboot_test.models.Cuenta;
import com.dieg0code.sprinboot_test.models.TransactionDTO;
import com.dieg0code.sprinboot_test.services.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;


    // ***************************************************************************************
    // ***************************************************************************************
    // ***************************************************************************************

    @Operation(
            /* -------------------------------------------------------------------------- */
            summary = "Retorna una lista de cuentas",
            description = "Retorna una lista de cuentas registradas en el sistema"
            /* -------------------------------------------------------------------------- */
    )
    @ApiResponse(
            /* -------------------------------------------------------------------------- */
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = Cuenta.class),
                    /* -------------------------------------------------------------------------- */
                    examples = {
                            @ExampleObject(
                                    name = "Example",
                                    value = "[{'id':1, 'nombre':'Cuenta 1', 'saldo':1000}, {'id':2, 'nombre':'Cuenta 2', 'saldo':2000}]",
                                    summary = "Example Response"

                            )
                    }
                    /* -------------------------------------------------------------------------- */

            )
            /* -------------------------------------------------------------------------- */
    )
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
    public ResponseEntity<?> details(@PathVariable(name = "id") Long id) {

        Cuenta cuenta = null;

        try {
            cuenta = cuentaService.findById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cuenta);
    }

    // ***************************************************************************************
    // ***************************************************************************************
    // ***************************************************************************************


    @Operation(
            /* -------------------------------------------------------------------------- */
            summary = "Registra una nueva cuenta",
            description = "Registra una nueva cuenta en el sistema"
            /* -------------------------------------------------------------------------- */
    )
    @ApiResponse(
            /* -------------------------------------------------------------------------- */
            responseCode = "201",
            description = "CREATED",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Cuenta.class)
            )
            /* -------------------------------------------------------------------------- */
    )
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

    // ***************************************************************************************
    // ***************************************************************************************
    // ***************************************************************************************

    @Operation(
            /* -------------------------------------------------------------------------- */
            summary = "Elimina una cuenta",
            description = "Elimina una cuenta mediante un identificador único"
            /* -------------------------------------------------------------------------- */
    )
    @ApiResponse(
            /* -------------------------------------------------------------------------- */
            responseCode = "204",
            description = "NO CONTENT"
            /* -------------------------------------------------------------------------- */
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "id") Long id) {
        cuentaService.deleteById(id);
    }
}
