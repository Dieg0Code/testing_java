package com.dieg0code.mockitoapp.repositories;

import java.util.List;

public interface PreguntaRespository {
    List<String> findPreguntasPorExamenId(Long id);
}
