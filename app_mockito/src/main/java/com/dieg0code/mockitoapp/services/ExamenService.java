package com.dieg0code.mockitoapp.services;

import com.dieg0code.mockitoapp.models.Examen;

import java.util.Optional;

public interface ExamenService {
    Optional<Examen> findExamenPorNombre(String nombre);

    Examen findExamenPorNombreConPreguntas(String nombre);

    Examen guardar(Examen examen);
}
