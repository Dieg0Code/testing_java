package com.dieg0code.mockitoapp.services;

import com.dieg0code.mockitoapp.models.Examen;
import com.dieg0code.mockitoapp.repositories.ExamenRepository;
import com.dieg0code.mockitoapp.repositories.ExamenRepositoryImpl;
import com.dieg0code.mockitoapp.repositories.PreguntaRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamenServiceImplTest {

    ExamenRepository repository;
    ExamenService service;
    PreguntaRespository preguntaRespository;

    @BeforeEach
    void setUp() {
        repository = mock(ExamenRepository.class);
        preguntaRespository = mock(PreguntaRespository.class);
        service = new ExamenServiceImpl(repository, preguntaRespository);
    }

    @Test
    void findExamenPorNombre() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);

        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertTrue(examen.isPresent(), "Examen no encontrado");
        assertEquals(5L, examen.orElseThrow().getId(), "El id del examen no es el esperado");
        assertEquals("Matematicas", examen.orElseThrow().getNombre(), "El nombre del examen no es el esperado");
    }

    @Test
    void findExamenPorNombreEmptyList() {
        List<Examen> data = Collections.emptyList();
        when(repository.findAll()).thenReturn(data);

        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertFalse(examen.isPresent(), "Examen no encontrado");
    }

    @Test
    void testPreguntaExamen() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
    }

    @Test
    void testPreguntaExamenVerify() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(repository).findAll();
        verify(preguntaRespository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testNotExistExamenVerify() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Fisica");

        assertNull(examen);

        verify(repository).findAll();
        verify(preguntaRespository).findPreguntasPorExamenId(5L);
    }
}