package com.dieg0code.sprinboot_test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dieg0code.sprinboot_test.exceptions.DineroInsuficienteException;
import com.dieg0code.sprinboot_test.models.Banco;
import com.dieg0code.sprinboot_test.models.Cuenta;
import com.dieg0code.sprinboot_test.repositories.BancoRepository;
import com.dieg0code.sprinboot_test.repositories.CuentaRepository;
import com.dieg0code.sprinboot_test.services.CuentaService;
import com.dieg0code.sprinboot_test.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SprinbootTestApplicationTests {

	@MockBean
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;

	@Autowired
	CuentaService cuentaService;

	@BeforeEach
	void setUp() {
		//cuentaRepository = mock(CuentaRepository.class);
		//bancoRepository = mock(BancoRepository.class);
		//cuentaService = new CuentaServiceImpl(cuentaRepository, bancoRepository);
	}

	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(Data.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Data.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Data.crearBanco());

		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		cuentaService.transferir(1L, 2L, new BigDecimal("100"), 1L);

		assertThrows(DineroInsuficienteException.class, () -> cuentaService.transferir(1L, 2L, new BigDecimal("1200"), 1L));

		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		int total = cuentaService.revisarTotalTransferencias(1L);
		assertEquals(2, total);

		verify(cuentaRepository, times(4)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);

		verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		verify(bancoRepository, times(3)).findById(1L);
		verify(bancoRepository, times(2)).save(any(Banco.class));

		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoad2() {
		when(cuentaRepository.findById(1L)).thenReturn(Data.crearCuenta001());

		Cuenta cuenta1 = cuentaService.findById(1L);
		Cuenta cuenta2 = cuentaService.findById(1L);

		assertSame(cuenta1, cuenta2);
		//assertTrue(cuenta1 == cuenta2);
	}

	@Test
	void findAllTest() {
		// Given
		List<Cuenta> cuentas = Arrays.asList(Data.crearCuenta001().orElseThrow(), Data.crearCuenta002().orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(cuentas);

		// When
		List<Cuenta> cuentasTest = cuentaService.findAll();

		// Then
		assertFalse(cuentasTest.isEmpty());
		assertEquals(2, cuentasTest.size());
		assertTrue(cuentasTest.contains(Data.crearCuenta002().orElseThrow()));

		verify(cuentaRepository).findAll();
	}

	@Test
	void saveTest() {
		//Given
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		//When
		Cuenta cuenta = cuentaService.save(cuentaPepe);

		//Then
		assertEquals("Pepe", cuenta.getNombre());
		assertEquals(3L, cuenta.getId());
		assertEquals("3000", cuenta.getSaldo().toPlainString());

		verify(cuentaRepository).save(any());
	}
}
