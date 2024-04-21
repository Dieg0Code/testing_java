package com.dieg0code.mockitoapp.services;

import com.dieg0code.mockitoapp.models.Examen;

public interface ExamenService {
    Examen findExamenPorNombre(String nombre);
}
