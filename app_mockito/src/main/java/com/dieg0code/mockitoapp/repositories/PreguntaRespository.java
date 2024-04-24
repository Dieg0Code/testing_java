package com.dieg0code.mockitoapp.repositories;

import java.util.List;

public interface PreguntaRespository {
    void guardarVarias(List<String> preguntas);

    List<String> findPreguntasPorExamenId(Long id);
}
