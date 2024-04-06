# Notas Unit Testing con Java (JUnit y Mockito)

## Temas

### JUnit 5

- TDD (Test Driven Development)
- Assertions
- assertThrows
- assertAll
- Anotaciones
    - @DisplayName
    - @Disabled
- Ciclo de vida de una prueba
    - @BeforeAll
    - @BeforeEach
    - @AfterEach
    - @AfterAll
- Test condicionales
    - @EnabledOnOs
    - @EnabledOnJre
- Assumptions
- Test anidados
    - @Nested
- @RepeatedTest
- @ParameterizedTest
- @Tag
- Inyección de dependencias

### Mockito

- Dependencia mock
- verify
- Anotaciones
    - @Mock
    - @InjectMock
    - @Captor
    - @ExtendWith
- Excepciones thenThrow
- Argument matcher
- Argument capture
- Familia de métodos do...
- Spy

### Spring Boot Test

- Integraciones con JUnit 5 y Mockito
- Anotaciones @MockBean y @Autowired
- MockMvc
    - @WebMvcTest
    - @DataJpaTest


## Que son las pruebas unitarias

Las pruebas unitarias son un proceso de examen para verificar que una pieza de código cumple con ciertas reglas de negocio y afirmar un resultado esperado

## Que es JUnit

JUnit test es una librería java para escribir y ejecutar repetibles pruebas unitarias

Utiliza programación funcional, expresiones lambda e incluye varios estilos diferentes de pruebas, configuraciones anotaciones, ciclo de vida etc.

### Arquitectura de JUnit

#### JUnit Platform

Es el core, esta enfocada al contexto de ejecución del test, lanza nuestra pruebas unitarias.

#### JUnit Jupiter

Es el API de programación y extensión para escribir pruebas en JUnit 5, incluye anotaciones, aserciones, extensiones y otros elementos, es lo que usamos para escribir nuestras pruebas.

##### Anotaciones

- @Test
- @DisplayName
- @Nested
- @Tag
- @ExtendWith
- @BeforeEach
- @AfterEach
- @BeforeAll
- @AfterAll
- @Disable

#### JUnit Vintage

Es un motor de ejecución que permite correr pruebas escritas en JUnit 3 y JUnit 4 en JUnit 5.