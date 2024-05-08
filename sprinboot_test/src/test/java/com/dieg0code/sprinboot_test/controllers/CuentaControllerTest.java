package com.dieg0code.sprinboot_test.controllers;

import com.dieg0code.sprinboot_test.Data;
import com.dieg0code.sprinboot_test.models.Cuenta;
import com.dieg0code.sprinboot_test.models.TransactionDTO;
import com.dieg0code.sprinboot_test.services.CuentaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CuentaService cuentaService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void detailsTest() throws Exception {
        //Given
        when(cuentaService.findById(1L)).thenReturn(Data.crearCuenta001().orElseThrow());

        // When
        mockMvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Diego"))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(cuentaService, times(1)).findById(1L);
    }

    @Test
    void transferirTest() throws Exception {
        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia realizada con éxito");
        response.put("transaction", dto);

        // When
        mockMvc.perform(post("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.message").value("Transferencia realizada con éxito"))
                .andExpect(jsonPath("$.transaction.cuentaOrigenId").value(dto.getCuentaOrigenId()))
                .andExpect(jsonPath("$.transaction.cuentaDestinoId").value(dto.getCuentaDestinoId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(cuentaService, times(1)).transferir(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());
    }

    @Test
    void listarTest() throws Exception {
        // Given
        List<Cuenta> cuentas = Arrays.asList(Data.crearCuenta001().orElseThrow(), Data.crearCuenta002().orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);

        // When
        mockMvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Diego"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].nombre").value("John"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));
    }

    @Test
    void saveTest() throws Exception {
        // Given
        Cuenta cuenta = new Cuenta(null, "Maria", new BigDecimal("3000"));
        when(cuentaService.save(any())).then(invocation -> {
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        // When
        mockMvc.perform(post("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuenta)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre").value("Maria"))
                .andExpect(jsonPath("$.saldo").value(3000));

        verify(cuentaService, times(1)).save(any());
    }
}