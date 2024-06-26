# Mockito

Mockito es un framework de pruebas que nos permite crear objetos simulados **mock** en un entorno controlado y determinado. Le podemos dar comportamientos a un mock y verificar que se comporta como esperamos.

Cuando trabajamos con Mockito tenemos que preparar nuestro entorno de pruebas, por ejemplo un clase service va a tener dependencias, estas dependencias se comunican con fuentes externas como bases de datos, servicios web, etc. Para aislar estas dependencias y poder probar nuestro código de manera unitaria, vamos a utilizar Mockito para simular estas dependencias. La idea es que los test sean algo ágil por lo que no queremos esperar a que se conecte a una base de datos o a un servicio web, es por esto que se usa Mockito para crear mocks de estas dependencias, es decir, objetos simulados que se comportan como queremos.

En Mockito trabajamos con tres partes, **dado que**, **cuando** y **entonces**. En la parte de **dado que** preparamos el entorno de pruebas, en la parte de **cuando** ejecutamos el método que queremos probar y en la parte de **entonces** verificamos que el método se comporta como esperamos.

## Mocking

Por ejemplo supongamos que tenemos una aplicación de la siguiente forma:

El modelo es de un Examen que tiene un id, un nombre y una lista de preguntas.

```java
public class Examen {

    private Long id;
    private String nombre;
    private List<String> preguntas;

    public Examen(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.preguntas = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<String> preguntas) {
        this.preguntas = preguntas;
    }
}
```
Con un servicio que se encarga de encontrar un examen por nombre.

```java
public interface ExamenService {
    Examen findExamenPorNombre(String nombre);
}
```

```java
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
```

Y un repositorio simulado que devuelve una lista de exámenes.

```java
public interface ExamenRepository {
    List<Examen> findAll();
}
```

```java
public class ExamenRepositoryImpl implements ExamenRepository {
    @Override
    public List<Examen> findAll() {
        return Arrays.asList(new Examen(5L, "Matematicas"), new Examen(6L, "Lenguaje"), new Examen(7L, "Historia"));
    }
}
```

Queremos probar el servicio para ver si encuentra un examen según el nombre que le pasemos

```java
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
```

Mockito se usa para simular objetos, en este caso simulamos el repositorio **mock(ExamenRepository.class)** y se lo pasamos al servicio. Luego usamos **when()** para decirle a Mockito que cuando se llame al método **findAll()** del repositorio entonces devuelva **thenReturn(data)** el array de exámenes que creamos y guardamos en la variable **data**. Con esto simulamos una respuesta del repositorio, entonces cuando luego mediante el **service** llama al método **findExamenPorNombre()** con el nombre "Matematicas" debería devolver el examen con id 5 y nombre "Matematicas", lo cual es correcto, pero no es data que venga de alguna fuente externa, sino que es lo que creamos nosotros con Mockito. Eso hace que luego los **assert** de JUnit sean correctos y el test pase.


Como nota, no se puede hacer mock de cualquier método, solo de aquellos que son públicos o default, pero no de métodos privados,tampoco de métodos estáticos o métodos final.

### Verify

Otro método importante de Mockito al igual que **when** es **verify**. **verify** se usa para verificar que un método de un mock se haya llamado. Por ejemplo si queremos verificar que el método **findAll()** del repositorio se haya llamado una vez, podemos hacer lo siguiente:

```java
verify(repository).findAll();
```

Esto se usa porque quizás algún método se encuentra dentro de un if y podría no ejecutarse, por lo que con **verify** podemos asegurarnos que se haya llamado y en caso de que no se haya llamado el test fallará.

Podemos definir cuantas veces queremos que se llame un método, por ejemplo si queremos que se llame dos veces:

```java
verify(repository, times(2)).findAll();
```

Esto va a ir dependiendo de la lógica de nuestro método, si queremos que se llame una vez, dos veces, etc.

### Inyección de dependencias con @Mock, @InjectMocks y @ExtendWith()

Para no tener que estar creando mocks y servicios en cada test, Mockito nos provee de las anotaciones **@Mock** y **@InjectMocks**. **@Mock** se usa para crear mocks y **@InjectMocks** se usa para inyectar los mocks en la clase que queremos probar.

Por ejemplo. en vez de nosotros estar creando instancias de alguna clase para luego inyectarlas en algún servicio, Mockito nos provee de estas anotaciones para hacerlo de manera automática.

```java
    ExamenRepository repository;
    ExamenService service;
    PreguntaRespository preguntaRespository;

    @BeforeEach
    void setUp() {
        repository = mock(ExamenRepository.class);
        preguntaRespository = mock(PreguntaRespository.class);
        service = new ExamenServiceImpl(repository, preguntaRespository);
    }
```

Esto lo podríamos reemplazar mediante las anotaciones de la siguiente forma:

```java
    @Mock
    ExamenRepository repository;

    @Mock
    PreguntaRespository preguntaRespository;

    @InjectMocks
    ExamenServiceImpl service;
```

Con esto estamos instanciando los mocks de los repositorios y luego inyectándolos en el servicio, de la misma forma que lo hacíamos manualmente.

Para que esto funcione debemos habilitarlo, para eso existen dos formas:

```java
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
```

La otra forma es anotar la clase con **@ExtendWith(MockitoExtension.class)**

```java
@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {}
```

Con esto ya podemos usar las anotaciones **@Mock** y **@InjectMocks** para inyectar dependencias en nuestras pruebas.

También para tener esto, es importante tener la dependencia de mockito-junit-jupiter en nuestro archivo pom.xml

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>3.11.2</version>
    <scope>test</scope>
</dependency>
```

Esta dependencia integra Mockito con el framework de pruebas JUnit 5. La version debe ser la misma que la de Mockito-core, en este caso 3.11.2.

### Invocation Arguments

```java
 @Test
    void guardarExamenTest() {
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
        
        Examen examen = service.guardar(newExamen);
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(repository).guardar(any(Examen.class));
        verify(preguntaRespository).guardarVarias(anyList());
    }
```

En este caso estamos simulando el guardado de un examen, cuando se llame al método **guardar()** del repositorio, entonces creamos un nuevo examen simulando la respuesta con **new Answer\<Examen>()**. En este caso simulamos el id del examen, el cual lo hacemos autoincremental, de esta forma cuando se llame al método **guardar()** del repositorio, el examen que se guarde va a tener un id autoincremental.

Los Test con Mockito tienen 3 etapas, **given**, **when** y **then**. En la etapa de **given** preparamos el entorno de pruebas, en la etapa de **when** ejecutamos el método que queremos probar y en la etapa de **then** verificamos que el método se comporta como esperamos.

```java
    @Test
    void guardarExamenTest() {
        // given
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

        // when
        Examen examen = service.guardar(newExamen);

        // then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(repository).guardar(any(Examen.class));
        verify(preguntaRespository).guardarVarias(anyList());
    }
```

En el ejemplo del test anterior, en la etapa de **given** creamos un nuevo examen y le seteamos las preguntas, luego simulamos el guardado del examen con Mockito. En la etapa de **when** llamamos al método **guardar()** del servicio y en la etapa de **then** verificamos que el examen se haya guardado correctamente. Estas pruebas se clasifican como BDD (Behavior Driven Development) ya que se enfocan en el comportamiento del método que estamos probando.

### Comprobación de excepciones usando when y thenThrow

```java
    void testManejoException() {
        when(repository.findAll()).thenReturn(Data.EXAMENES_ID_NULL);
        when(preguntaRespository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.findExamenPorNombreConPreguntas("Matematicas"));

        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(preguntaRespository).findPreguntasPorExamenId(isNull());
    }
```

En este caso estamos simulando una excepción, cuando se llame al método **findPreguntasPorExamenId()** del repositorio de preguntas con un id nulo, entonces lanzamos una excepción **thenThrow(IllegalArgumentException.class)**. Luego en el test verificamos que se haya lanzado la excepción con **assertThrows(IllegalArgumentException.class, () -> service.findExamenPorNombreConPreguntas("Matematicas"))** y verificamos que se haya llamado al método **findPreguntasPorExamenId()** con un id nulo. Esto es útil para probar el manejo de excepciones en nuestro código.

### ArgumentMatchers

Los argument matchers permiten saber si coincide el valor real que se pasa por argumento en un método con el valor definido en el mock, por ejemplo, en el **when()** o en el **verify()**. En otras palabras, se usan para asegurarse de que ciertos argumentos se pasen a los mocks.

```java
@Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRespository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg > 0));

    }
```

El argument matcher seria:

```java
verify(preguntaRespository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg > 0));
```

En donde verificamos que el id sea diferente de nulo y mayor a 0.

También podemos implementar una clase que implemente la interfaz **ArgumentMatcher** para hacer una validación más compleja.

```java
class MiArgumentMatcher implements ArgumentMatcher<Long> {

    @Override
    public boolean matches(Long argument) {
        return argument != null && argument > 0;
    }
}
```

En esta podemos agregar la lógica que queramos para validar el o los argumentos que queramos.

```java
verify(preguntaRespository).findPreguntasPorExamenId(argThat(new MiArgumentMatcher()));
```

### Capturar argumentos con ArgumentCaptor

Con **ArgumentCaptor** podemos capturar los argumentos que se pasan a un método de un mock, esto es útil para verificar que se pasen los argumentos correctos a un método.

```java
    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        //when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(preguntaRespository).findPreguntasPorExamenId(captor.capture());
        assertEquals(5L, captor.getValue());
    }
```

En este caso estamos capturando el id que se pasa al método **findPreguntasPorExamenId()** del repositorio de preguntas. Luego verificamos que el id capturado sea igual a 5L ya que este es el esperado para "Matematicas".

Esto también se puede hacer usando una anotación:

```java
    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        //when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(preguntaRespository).findPreguntasPorExamenId(captor.capture());
        assertEquals(5L, captor.getValue());
    }
```

### doThrow para comprobaciones de excepciones en métodos void

Cunado el método que queremos probar es void, es decir, que no devuelve nada, no podemos usar el **when()** seguido de **thenReturn()**, en estos casos podemos usar **doThrow()**.

```java
    @Test
    void testDoThrow() {
        Examen newExamen = Data.EXAMEN;
        newExamen.setPreguntas(Data.PREGUNTAS);

        doThrow(IllegalArgumentException.class).when(preguntaRespository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> service.guardar(newExamen));
    }
```

La sintaxis es al revés de lo que veníamos viendo hasta ahora, primero definimos lo que va a pasar con **doThrow()** y luego el método que queremos probar.

### doAnswer

```java
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
```

Lo que hace **doAnswer()** es básicamente decir, responder esto cuando cuando esto pase, en este caso, respondemos con las preguntas de matemáticas cuando el id sea 5L, de lo contrario respondemos con una lista vacía cuando se llama al método **findPreguntasPorExamenId()** del repositorio de preguntas.

Es para personalizar la respuesta que queremos dar cuando se llama a un método de un mock.

### doCallRealMethod para llamar al método real

**doCallRealMethod()** se usa para llamar al método real de una clase, por ejemplo, si tenemos un método que queremos probar y queremos que se ejecute el método real, podemos usar **doCallRealMethod()**.

```java
    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);
        //when(preguntaRespository.findPreguntasPorExamenId(anyLong())).thenReturn(Data.PREGUNTAS);
        doCallRealMethod().when(preguntaRespository).findPreguntasPorExamenId(5L);

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(preguntaRespository).findPreguntasPorExamenId(5L);

    }
```

Llamar al método real es útil para probar el comportamiento de este tal cual, a diferencia de los mocks en los cuales nosotros definimos el comportamiento que queremos que tengan.

### Spy y doReturn

Los **Spy** son una mezcla entre el objeto real y un mock. Nos permiten invocar métodos sin tener que definir **when()**, ni tener que crear ningún mock() de algún objeto, simplemente cuando invoquemos un método va a usar el método real.

```java
    @Test
    void testDoReturn() {
        Examen newExamen = Data.EXAMEN;
        newExamen.setPreguntas(Data.PREGUNTAS);

        ExamenRepository spy = spy(ExamenRepositoryImpl.class);

        doReturn(Data.EXAMENES).when(spy).findAll();

        Examen examen = spy.guardar(newExamen);

        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(spy).guardar(newExamen);
    }
```

Es importante mencionar que para crear un **Spy** debe ser de una clase concreta, no de una interfaz, ya que este va a llamar métodos reales, lo cual no se puede hacer desde una interfaz. Esto es una diferencia con los mocks, ya que los mocks pueden ser de interfaces ya que siempre van a ser simulados.

### Verificación del orden de invocaciones

**inOrder()** es un método que nos permite verificar el orden en el que se llaman los métodos de un mock.

```java
    @Test
    void testOrdenInvocaciones() {
        when(repository.findAll()).thenReturn(Data.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(preguntaRespository);
        inOrder.verify(preguntaRespository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntaRespository).findPreguntasPorExamenId(6L);
    }
```
En este caso estamos verificando que primero se llame al método **findPreguntasPorExamenId()** con el id 5L y luego con el id 6L. Si se llama en otro orden el test fallará.

También podemos verificar multiples mockw.

```java
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
```

### Verificación del numero de invocaciones

```java
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
```

Tenemos varias formas de verificar las veces que se llama a un método, **times()** verifica que se llame una cantidad de veces, **never()** verifica que nunca se llame, **atLeastOnce()** verifica que se llame al menos una vez, **atLeast()** verifica que se llame al menos una cantidad de veces, **atMost()** verifica que se llame como máximo una cantidad de veces. Hay mas formas, pero estas son las más comunes.