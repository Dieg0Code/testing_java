package com.dieg0code.mockitoapp.services;

import com.dieg0code.mockitoapp.models.Examen;
import com.dieg0code.mockitoapp.repositories.ExamenRepository;
import com.dieg0code.mockitoapp.repositories.ExamenRepositoryImpl;
import com.dieg0code.mockitoapp.repositories.PreguntaRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    @Mock
    ExamenRepository repository;
    @Mock
    PreguntaRespository preguntaRespository;

    @InjectMocks
    ExamenServiceImpl service;

    /*
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
     */

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

    @Test
    void guardarExamenTest() {
        // Given
        Examen newExamen = Data.EXAMEN;
        newExamen.setPreguntas(Data.PREGUNTAS);

        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        // When
        Examen examen = service.guardar(newExamen);

        // Then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(repository).guardar(any(Examen.class));
        verify(preguntaRespository).guardarVarias(anyList());
    }

    @Test
    void testManejoException() {
        when(repository.findAll()).thenReturn(Data.EXAMENES_ID_NULL);
        when(preguntaRespository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.findExamenPorNombreConPreguntas("Matematicas"));

        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(preguntaRespository).findPreguntasPorExamenId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRespository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg > 0));
        verify(preguntaRespository).findPreguntasPorExamenId(argThat(new MiArgsMatcher()));

    }

    public static class MiArgsMatcher implements ArgumentMatcher<Long> {
        @Override
        public boolean matches(Long aLong) {
            return aLong != null && aLong > 0;
        }

        @Override
        public String toString() {
            return "El argumento debe ser mayor que cero y no nulo";
        }
    }

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        //when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(preguntaRespository).findPreguntasPorExamenId(captor.capture());
        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        //when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Data.PREGUNTAS : Collections.emptyList();
        }).when(preguntaRespository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(preguntaRespository).findPreguntasPorExamenId(anyLong());

    }

    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);

        doCallRealMethod().when(preguntaRespository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }

    @Test
    void testSpy() {
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);

        Examen examen = new Examen(8L, "Fisica");
        when(examenRepository.findAll()).thenReturn(List.of(examen));

        Examen examen1 = examenRepository.findAll().get(0);

        assertEquals("Fisica", examen1.getNombre());
        assertEquals(8L, examen1.getId());

        when(examenRepository.guardar(any(Examen.class))).thenReturn(new Examen(8L, "Fisica"));

        Examen examenGuardado = examenRepository.guardar(new Examen(8L, "Fisica"));

        assertEquals("Fisica", examenGuardado.getNombre());
        assertEquals(8L, examenGuardado.getId());

        verify(examenRepository).guardar(any(Examen.class));

    }

    @Test
    void testOrdenInvocaciones() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(preguntaRespository);
        inOrder.verify(preguntaRespository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntaRespository).findPreguntasPorExamenId(6L);
    }

    @Test
    void testOrdenInvocaciones2() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(repository, preguntaRespository);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRespository).findPreguntasPorExamenId(5L);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRespository).findPreguntasPorExamenId(6L);
    }

    @Test
    void testNumeroInvocaciones() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository, times(2)).findAll();
        verify(preguntaRespository, never()).findPreguntasPorExamenId(6L);
        verify(preguntaRespository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRespository, atLeast(2)).findPreguntasPorExamenId(5L);
        verify(preguntaRespository, atMost(2)).findPreguntasPorExamenId(5L);
    }
}