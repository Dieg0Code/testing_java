package com.dieg0code.mockitoapp.services;

import com.dieg0code.mockitoapp.models.Examen;
import com.dieg0code.mockitoapp.repositories.ExamenRepository;
import com.dieg0code.mockitoapp.repositories.PreguntaRespository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService {

    private ExamenRepository examenRepository;
    private PreguntaRespository preguntaRespository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntaRespository preguntaRespository) {
        this.examenRepository = examenRepository;
        this.preguntaRespository = preguntaRespository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return examenRepository.findAll().stream()
                .filter(examen -> examen.getNombre().equals(nombre))
                .findFirst();

    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamenPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()) {
            examen = examenOptional.orElseThrow();
            List<String> preguntas = preguntaRespository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }

        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()) {
            preguntaRespository.guardarVarias(examen.getPreguntas());
        }

        return examenRepository.guardar(examen);
    }
}
