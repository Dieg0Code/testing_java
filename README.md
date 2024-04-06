# Notas Unit Testing con Java (JUnit y Mockito)

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