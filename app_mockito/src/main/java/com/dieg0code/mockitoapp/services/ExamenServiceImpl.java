package com.dieg0code.mockitoapp.services;

import com.dieg0code.mockitoapp.models.Examen;
import com.dieg0code.mockitoapp.repositories.ExamenRepository;

import java.util.Optional;

public class ExamenServiceImpl implements ExamenService {

    private ExamenRepository examenRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    @Override
    public Examen findExamenPorNombre(String nombre) {
        Optional<Examen> examenOptional = examenRepository.findAll().stream()
                .filter(examen -> examen.getNombre().equals(nombre))
                .findFirst();
        Examen examen = null;
        if (examenOptional.isPresent()) {
            examen = examenOptional.orElseThrow();
        }

        return examen;
    }
}
