package com.dieg0code.sprinboot_test.controllers;

import com.dieg0code.sprinboot_test.models.Cuenta;
import com.dieg0code.sprinboot_test.models.TransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.MethodOrderer.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Tag("integracion_rt")
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    //@LocalServerPort
    //private int port; // Se puede usar para obtener el puerto en el que se está ejecutando la aplicación

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void transferirTest() {
        /// Given
        TransactionDTO dto = new TransactionDTO();
        dto.setMonto( new BigDecimal("100"));
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);

        // When
        ResponseEntity<String> response = client.postForEntity("/api/cuentas/transferir", dto, String.class);
        String json = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con éxito"));
    }

    @Test
    @Order(2)
    void trasferirTest2() throws JsonProcessingException {
        TransactionDTO dto = new TransactionDTO();
        dto.setMonto( new BigDecimal("100"));
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);

        // When
        ResponseEntity<String> response = client.postForEntity("/api/cuentas/transferir", dto, String.class);
        String json = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con éxito"));

        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transferencia realizada con éxito", jsonNode.path("message").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaction").path("monto").asText());
        assertEquals(1L, jsonNode.path("transaction").path("cuentaOrigenId").asLong());

        Map<String, Object> res = new HashMap<>();
        res.put("date", LocalDate.now().toString());
        res.put("status", "OK");
        res.put("message", "Transferencia realizada con éxito");
        res.put("transaction", dto);

        assertEquals(objectMapper.writeValueAsString(res), json);

    }

    @Test
    @Order(3)
    void detailsTest() {
        ResponseEntity<Cuenta> response = client.getForEntity("/api/cuentas/1", Cuenta.class);
        Cuenta cuenta = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(cuenta);
        assertEquals(1L, cuenta.getId());
        assertEquals("Diego", cuenta.getNombre());
        assertEquals(new BigDecimal("800.00"), cuenta.getSaldo());
    }

    @Test
    @Order(4)
    void testListar() {
        ResponseEntity<Cuenta[]> response = client.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(cuentas);
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());

        assertEquals(1L, cuentas.get(0).getId());
        assertEquals("Diego", cuentas.get(0).getNombre());
        assertEquals(new BigDecimal("800.00"), cuentas.get(0).getSaldo());

        assertEquals(2L, cuentas.get(1).getId());
        assertEquals("Pedro", cuentas.get(1).getNombre());
        assertEquals(new BigDecimal("2200.00"), cuentas.get(1).getSaldo());

    }

    @Test
    @Order(5)
    void testGuardar() {
        Cuenta cuenta = new Cuenta(null, "John", new BigDecimal("3000.00"));

        ResponseEntity<Cuenta> response = client.postForEntity("/api/cuentas", cuenta, Cuenta.class);
        Cuenta cuentaResponse = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(cuentaResponse);
        assertEquals(3L, cuentaResponse.getId());
        assertEquals("John", cuentaResponse.getNombre());
        assertEquals(new BigDecimal("3000.00"), cuentaResponse.getSaldo());
    }

    @Test
    @Order(6)
    void testEliminar() {
        ResponseEntity<Cuenta[]> response = client.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(cuentas);
        assertFalse(cuentas.isEmpty());
        assertEquals(3, cuentas.size());
        assertTrue(cuentas.stream().anyMatch(c -> c.getId() == 3));

        client.delete("/api/cuentas/3");

        response = client.getForEntity("/api/cuentas", Cuenta[].class);
        cuentas = Arrays.asList(response.getBody());

        assertEquals(2, cuentas.size());

        ResponseEntity<Cuenta> response2 = client.getForEntity("/api/cuentas/3", Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        assertFalse(response2.hasBody());
    }
}