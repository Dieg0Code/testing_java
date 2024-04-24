package com.dieg0code.mockitoapp.repositories;

import com.dieg0code.mockitoapp.models.Examen;

import java.util.List;

public interface ExamenRepository {
    Examen guardar(Examen examen);

    List<Examen> findAll();
}
