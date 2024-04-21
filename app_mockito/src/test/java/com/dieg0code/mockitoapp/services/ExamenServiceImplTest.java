package com.dieg0code.mockitoapp.services;

import com.dieg0code.mockitoapp.models.Examen;
import com.dieg0code.mockitoapp.repositories.ExamenRepository;
import com.dieg0code.mockitoapp.repositories.ExamenRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamenServiceImplTest {

    @Test
    void findExamenPorNombre() {
        ExamenRepository repository = mock(ExamenRepository.class);
        ExamenService service = new ExamenServiceImpl(repository);

        List<Examen> data = Arrays.asList(new Examen(5L, "Matematicas"), new Examen(6L, "Lenguaje"), new Examen(7L, "Historia"));
        when(repository.findAll()).thenReturn(data);

        Examen examen = service.findExamenPorNombre("Matematicas");

        assertNotNull(examen, "Examen no encontrado");
        assertEquals(5L, examen.getId(), "El id del examen no es el esperado");
        assertEquals("Matematicas", examen.getNombre(), "El nombre del examen no es el esperado");
    }
}