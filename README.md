# Notas Unit Testing con Java (JUnit y Mockito)

## Que son las pruebas unitarias

Las pruebas unitarias son un proceso de examen para verificar que una pieza de código cumple con ciertas reglas de negocio y afirmar un resultado esperado

## Que es JUnit

JUnit test es una librería java para escribir y ejecutar repetibles pruebas unitarias

Utiliza programación funcional, expresiones lambda e incluye varios estilos diferentes de pruebas, configuraciones, anotaciones, ciclo de vida etc.

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


## JUnit 5

## Assertions

Las aserciones son una forma de verificar que el resultado de una prueba sea el esperado. Por ejemplo si tenemos un clase con dos atributos, como nombre y saldo, podemos verificar si el objeto instanciado de esta clase tiene algún valor que esperamos.

```java
public class Cuenta {
    private String persona;

    private BigDecimal saldo;

    public Cuenta(String persona, BigDecimal saldo) {
        this.persona = persona;
        this.saldo = saldo;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

}
```

```java

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertEquals(expectedValue, actualValue);
    }
}
```

En este caso, estamos verificando mediante **Assertions.assertEquals** que el valor guardado en el atributo persona de la clase cuenta sea igual al valor esperado.

También podemos usar **Assertions.assertTrue** para verificar si una condición es verdadera.

```java
class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertTrue(actualValue.equals("Diego"));
    }
}
```
```java
    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }
```

En este caso testeamos el saldo de la cuenta, verificando si el valor es igual al esperado y si es mayor a 0, mediante **Assertions.assertFalse**, con esto estamos esperando que la comprobación de que el saldo es mayor a 0 sea falsa, si devolviera verdadero, la prueba fallaría.

## TDD (Test Driven Development) con JUnit

El Test Driven Development es una técnica de desarrollo de software que se basa en escribir primero las pruebas unitarias antes de escribir el código.

Por ejemplo, siguiendo el ejemplo, si queremos validar si dos instancias de la clase Cuenta son iguales, primero escribimos la prueba y luego el código.

```java
    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.999"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.999"));

        // assertNotEquals(cuenta, cuenta2);
        assertEquals(cuenta, cuenta2);
    }
```

Para que esta prueba pase de manera exitosa debemos sobreescribir el método equals en la clase Cuenta.

```java
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cuenta)) {
            return false;
        }
        Cuenta c = (Cuenta) obj;
        if (this.persona == null || this.saldo == null) {
            return false;
        }

        return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
    }
```

De esta manera, estamos evaluando primero si el objeto no es una instancia de la clase Cuenta, en cuyo caso devolvemos false, luego si ambos atributos, nombre y saldo no son nulos, y por último comprobamos si tanto el nombre de la persona guardado en el atributo persona y el saldo guardado en el atributo saldo son iguales a los valores de la instancia de la clase Cuenta que estamos comparando. De esta manera estamos evaluando mediante el test si ambas instancias de la clase Cuenta son iguales en base a los valores de sus atributos, si no sobrescribimos el método equals, el test evaluara ambas instancias por referencia, por lo que siempre serian diferentes.


### assertAll

Podemos usar **assertAll** para evaluar varias aserciones en un solo test.

```java
    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.999"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.999"));

        assertAll(() -> {
            assertEquals(cuenta, cuenta2);
        }, () -> {
            assertEquals(cuenta.getPersona(), cuenta2.getPersona());
        }, () -> {
            assertEquals(cuenta.getSaldo(), cuenta2.getSaldo());
        });
    }
```

Mediante lambdas definimos las aserciones que queremos evaluar, en este caso, si ambas instancias de la clase Cuenta son iguales, si el nombre de la persona es igual y si el saldo es igual. Por solo dar un ejemplo.

Usar **assertAll** es útil ya que podemos evaluar multiples aserciones al mismo tiempo y si falla alguna nos dice cual fue. A diferencia de usar **assertEquals** que si falla una aserción, no evalúa las demás.

### Mensajes de falla en los métodos de aserción

Podemos agregar mensajes personalizados a las aserciones para que cuando falle una prueba, nos diga que fue lo que falló.

```java
    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.999"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.999"));

        assertAll(() -> {
            assertEquals(cuenta, cuenta2, () -> "Las cuentas no son iguales");
        }, () -> {
            assertEquals(cuenta.getPersona(), cuenta2.getPersona(), () -> "Los nombres de las cuentas no son iguales");
        }, () -> {
            assertEquals(cuenta.getSaldo(), cuenta2.getSaldo(), () -> "Los saldos de las cuentas no son iguales");
        });
    }
```

Estos mensajes solo serán mostrados en caso de que se produzca el error en la prueba, son muy útiles para dar mas contexto de que fue lo que falló.

Anterior a JUnit 5, los mensajes se agregaban en duro en el código, ahora podemos agregarlos de manera dinámica mediante lambdas, de esta forma se es mas eficiente y solo se construye el mensaje en caso de que falle la prueba.

### Anotaciones @DisplayName y @Disabled

Por defecto los test muestran el nombre del método como nombre de la prueba, pero podemos personalizar esto con algo mas descriptivo que ayude a tener mas información a quien ejecute el test.

```java
    @Test
    @DisplayName("Probando el nombre de la cuenta corriente")
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertTrue(actualValue.equals("Diego"));
    }
```

Con esto estamos personalizando el nombre de la prueba, de esta manera si falla, sabremos que es lo que se estaba probando.

También podemos deshabilitar una prueba con la anotación **@Disabled**.

```java
    @Test
    @DisplayName("Probando el nombre de la cuenta corriente")
    @Disabled
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertTrue(actualValue.equals("Diego"));
    }
```

Esta anotación nos puede servir para deshabilitar pruebas que no queremos que se ejecuten por el momento, pero que no queremos borrar. Al ejecutar los test aquel que tenga esta anotación será ignorado.

Un método útil que tenemos también es **fail()**, con el cual podemos forzar el error en un test, esto se puede usar en caso de que queramos ver como se comportaría un test en caso de fallar.

### Ciclo de vida, anotaciones @BeforeAll, @BeforeEach, @AfterAll, @AfterEach

**@BeforeAll** y **@AfterAll** son anotaciones que se ejecutan antes y después de todos los test de la clase. Mientras que **@BeforeEach** y **@AfterEach** se ejecutan antes y después de cada test.

```java
class CuentaTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("Iniciando el test");
    }

    @BeforeEach
    void initMethod() {
        System.out.println("Iniciando el método de prueba");
    }

    @Test
    @DisplayName("Probando el nombre de la cuenta corriente")
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertTrue(actualValue.equals("Diego"));
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el método de prueba");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }
}
```

Se puede usar el ciclo de vida para inicializar recursos antes de los test y liberarlos después de los test, por ejemplo, si estamos trabajando con una base de datos, podemos inicializar la conexión en **@BeforeAll** y cerrarla en **@AfterAll** o instanciar un objeto en **@BeforeEach** y liberar la memoria en **@AfterEach**.

Como nota, no es bueno instanciar objetos en **@BeforeAll** ya que estos objetos serán compartidos entre todos los test, lo cual puede llevar a problemas si se modifican en algún test, lo recomendable es instanciar objetos en **@BeforeEach** para que cada test tenga su propia instancia. Las clases test tienen que ser stateless, es decir, no deben tener estado, cada test debe ser independiente de los demás.

Aún así, podemos modificar el comportamiento de la clase mediante la anotación **@TestInstance**, por defecto el valor es **Lifecycle.PER_METHOD**, lo que significa que se creará una nueva instancia de la clase para cada test, si cambiamos el valor a **Lifecycle.PER_CLASS**, se creará una sola instancia de la clase para todos los test.

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("Iniciando el test");
    }

    @BeforeEach
    void initMethod() {
        System.out.println("Iniciando el método de prueba");
    }

    @Test
    @DisplayName("Probando el nombre de la cuenta corriente")
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertTrue(actualValue.equals("Diego"));
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el método de prueba");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }
}
```

Con esto forzamos a que la clase maneje una instancia compartida para cada método de test, esto puede ser útil si queremos compartir recursos entre los test, pero hay que tener cuidado con el estado de los objetos. En algunos casos esto puede ser útil, pero por lo general para que esto funciona bien debemos ordenar los test para que estos no interfieran entre si.

### Test condicionales con @EnabledOnOs, @EnabledOnJre, @EnabledIfSystemProperty

Los test condicionales son aquellos que se van a ejecta en ciertos escenarios, dependiendo del contexto, por ejemplo, ejecutar un test solo si estamos en un sistema operativo en particular, o si estamos en una versión de Java en particular.

```java
@Test
@EnabledOnOs(OS.WINDOWS)
void testSoloWindows() {
    System.out.println("Ejecutando en Windows");
}

@Test
@EnabledOnOs({OS.LINUX, OS.MAC})
void testSoloLinuxYMac() {
    System.out.println("Ejecutando en Linux o Mac");
}

@Test
@EnabledOnJre(JRE.JAVA_8)
void testJava8() {
    System.out.println("Ejecutando en Java 8");
}

@Test
@EnabledOnJre(JRE.JAVA_11)
void testJava11() {
    System.out.println("Ejecutando en Java 11");
}

@Test
@EnabledIfSystemProperty(named = "user.name", matches = "diego")
void testSiUsuarioDiego() {
    System.out.println("Ejecutando si el usuario es Diego");
}
```
Cuando ejecutamos estos test, solo se ejecutarán aquellos que cumplan con las condiciones que hemos definido los demás serán ignorados.

### Test condicionales con @EnabledIfEnvironmentVariable

Podemos usar la anotación **@EnabledIfEnvironmentVariable** para ejecutar un test si una variable de entorno cumple con cierta condición.

```java
@Test
@EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11.*")
void testJavaHome() {
    System.out.println("Ejecutando en Java 11");
}

@Test
@EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
void testNumeroProcesadores() {
    System.out.println("Ejecutando si el número de procesadores es 8");
}
```

### Ejecución de test condicionales con Assumptions

Las asunciones son una forma de ejecutar un test si se cumple una condición, si no se cumple, el test será ignorado.

```java
@Test
void testSaldoCuenta() {
    Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
    boolean esDev = "dev".equals(System.getProperty("ENV"));
    Assumptions.assumeTrue(esDev);
    assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
    assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
}
```

En este caso, estamos asumiendo que el entorno es dev, si esto es verdadero, entonces comprobamos si el saldo de la cuenta es igual al esperado y si es mayor a 0, si no es dev, el test será ignorado.

A fines prácticos, este tipo de test no devolverá error en caso de que no se cumplan las comprobaciones que declaremos en el test, simplemente en caso de fallar, el test será ignorado.

También podemos probar multiples condiciones con *assumingThat*.

```java
@Test
void testSaldoCuenta() {
    Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
    boolean esDev = "dev".equals(System.getProperty("ENV"));
    Assumptions.assumingThat(esDev, () -> {
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    });
}
```

En este caso, si la asunción es verdadera, entonces se ejecutaran las comprobaciones que definamos en el lambda, si no es verdadera, el test será ignorado.

### Clases de test anidadas con @Nested

La anotación **@Nested** nos permite anidar test dentro de otros, para así poder organizarlos de forma jerárquica. De esta forma podemos organizar según lo que hacen , según el contexto, etc. Por ejemplo, todos los Assumptions o todos los Enables pueden ser organizados en una clase anidada.

```java
class CuentaTest {
    
        @BeforeAll
        static void beforeAll() {
            System.out.println("Iniciando el test");
        }
    
        @BeforeEach
        void initMethod() {
            System.out.println("Iniciando el método de prueba");
        }
    
        @Test
        @DisplayName("Probando el nombre de la cuenta corriente")
        void testNombreCuenta() {
            Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
            String expectedValue = "Diego";
            String actualValue = cuenta.getPersona();
            Assertions.assertTrue(actualValue.equals("Diego"));
        }
    
        @AfterEach
        void tearDown() {
            System.out.println("Finalizando el método de prueba");
        }
    
        @AfterAll
        static void afterAll() {
            System.out.println("Finalizando el test");
        }
    
        @Nested
        class CuentaOperacionesTest {
    
            @Test
            void testDebitoCuenta() {
                System.out.println("Ejecutando test debito cuenta");
                Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.12345"));
                cuenta.debito(new BigDecimal(100));
                assertNotNull(cuenta.getSaldo());
                assertEquals(900, cuenta.getSaldo().intValue());
            }
    
            @Test
            void testCreditoCuenta() {
                System.out.println("Ejecutando test credito cuenta");
                Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.12345"));
                cuenta.credito(new BigDecimal(100));
                assertNotNull(cuenta.getSaldo());
                assertEquals(1100, cuenta.getSaldo().intValue());
            }
        }

        @Nested
        class CuentaReferenciaTest {

            @Test
            void testReferenciaCuenta() {
                Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.999"));
                Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.999"));
        
                assertAll(() -> {
                    assertEquals(cuenta, cuenta2, () -> "Las cuentas no son iguales");
                }, () -> {
                    assertEquals(cuenta.getPersona(), cuenta2.getPersona(), () -> "Los nombres de las cuentas no son iguales");
                }, () -> {
                    assertEquals(cuenta.getSaldo(), cuenta2.getSaldo(), () -> "Los saldos de las cuentas no son iguales");
                });
            }
        }
    }
}
```

En cada una de esta clases anidadas podemos declarar eventos como **@BeforeEach** y **@AfterEach** que se ejecutarán antes y después de cada test de la clase anidada. Los eventos **@BeforeAll** y **@AfterAll** solo se ejecutarán una vez antes y después de todos los test de la clase principal.

### Test repetitivos con @RepeatedTest

Junit nos permite repetir un test un número determinado de veces con la anotación **@RepeatedTest**, esta anotación se usa cuando nuestro algoritmo dentro del método tiene cierta aleatoriedad y queremos probarlo varias veces por si falla en alguna ocasión.

```java
@RepeatedTest(value = 5, name = "Probando debito cuenta {currentRepetition} de {totalRepetitions}")
void testDebitoCuenta(RepetitionInfo repetitionInfo) {
    System.out.println("Ejecutando test debito cuenta " + repetitionInfo.getCurrentRepetition());
    Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.12345"));
    cuenta.debito(new BigDecimal(100));
    assertNotNull(cuenta.getSaldo());
    assertEquals(900, cuenta.getSaldo().intValue());
}
```

En el ejemplo anterior, estamos repitiendo el test 5 veces, y estamos mostrando en el nombre del test la repetición actual y el total de repeticiones.

### Test parametrizados con @ParameterizedTest

Los test parametrizados tiene una similitud con los test repetitivos, pero en este caso, podemos pasar parámetros a los test para que estos se ejecuten con diferentes valores.

```java
@ParameterizedTest(name = "Probando debito cuenta {index} - valor: {0}")
@ValueSource(strings = {"100", "200", "300", "500", "700", "1000"})
void testDebitoCuenta(String monto) {
    Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.12345"));
    cuenta.debito(new BigDecimal(monto));
    assertNotNull(cuenta.getSaldo());
    assertEquals(1000 - Integer.parseInt(monto), cuenta.getSaldo().intValue());
}
```

En este caso el test se ejecutará 6 veces, una por cada valor que le pasamos en el array de strings, en cada ejecución, el valor de monto será diferente, se evaluara cada uno de los valores, en caso de que falle uno de los test, nos dirá cual fue el valor que falló.

Otra forma de inyectar valores a la proba parametrizada es mediante **@CsvSource**.

```java
@ParameterizedTest(name = "Probando debito cuenta {index} - valor: {0}")
@CsvSource({"100, 900", "200, 800", "300, 700", "500, 500", "700, 300", "1000, 0"})
void testDebitoCuenta(String monto, String saldo) {
    Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.12345"));
    cuenta.debito(new BigDecimal(monto));
    assertNotNull(cuenta.getSaldo());
    assertEquals(Integer.parseInt(saldo), cuenta.getSaldo().intValue());
}
```

En este caso, estamos pasando dos valores, el monto a debitar y el saldo esperado, en cada ejecución, se evaluará si el saldo de la cuenta es igual al saldo esperado, por ejemplo, si el debito es 100, entonces el saldo después del debito debería ser 900 ya que el saldo inicial es 1000 y así sucesivamente.

La anotación **@CsvFileSource** nos permite leer los valores de un archivo CSV.

```java
@ParameterizedTest(name = "Probando debito cuenta {index} - valor: {0}")
@CsvFileSource(resources = "/data.csv")
void testDebitoCuenta(String monto, String saldo) {
    Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.12345"));
    cuenta.debito(new BigDecimal(monto));
    assertNotNull(cuenta.getSaldo());
    assertEquals(Integer.parseInt(saldo), cuenta.getSaldo().intValue());
}
```

Tambien podemos usar **@MethodSource** para inyectar valores desde un método.

```java
@ParameterizedTest(name = "Probando debito cuenta {index} - valor: {0}")
@MethodSource("montoSaldo")
void testDebitoCuenta(String monto, String saldo) {
    Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.12345"));
    cuenta.debito(new BigDecimal(monto));
    assertNotNull(cuenta.getSaldo());
    assertEquals(Integer.parseInt(saldo), cuenta.getSaldo().intValue());
}

static Stream<Arguments> montoSaldo() {
    return Stream.of(Arguments.of("100", "900"),
            Arguments.of("200", "800"),
            Arguments.of("300", "700"),
            Arguments.of("500", "500"),
            Arguments.of("700", "300"),
            Arguments.of("1000", "0"));
}
```
### Tagging de test con @Tag

Los tags nos permiten categorizar nuestro test e incluso las clases anidadas. Etiquetar nuestros test nos sirve para poder ejecutar luego pruebas de forma selectiva, por ejemplo, si tenemos test etiquetados como test de integración, podemos ejecutar solo estos test.

```java
@Tag("cuenta")
class CuentaTest {

    @Test
    @Tag("cuenta")
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertTrue(actualValue.equals("Diego"));
    }

    @Test
    @Tag("cuenta")
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }
}
```

En este caso, estamos etiquetando la clase y los test con el tag **cuenta**, de esta forma podemos ejecutar solo los test que tengan este tag.

```java
@Tag("cuenta")
class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertTrue(actualValue.equals("Diego"));
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Nested
    @Tag("operaciones")
    class CuentaOperacionesTest {

        @Test
        void testDebitoCuenta() {
            System.out.println("Ejecutando test debito cuenta");
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
        }

        @Test
        void testCreditoCuenta() {
            System.out.println("Ejecutando test credito cuenta");
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.12345"));
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
        }
    }

    @Nested
    @Tag("referencia")
    class CuentaReferenciaTest {

        @Test
        void testReferenciaCuenta() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.999"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.999"));
    
            assertAll(() -> {
                assertEquals(cuenta, cuenta2, () -> "Las cuentas no son iguales");
            }, () -> {
                assertEquals(cuenta.getPersona(), cuenta2.getPersona(), () -> "Los nombres de las cuentas no son iguales");
            }, () -> {
                assertEquals(cuenta.getSaldo(), cuenta2.getSaldo(), () -> "Los saldos de las cuentas no son iguales");
            });
        }
    }
}
```

Con solo etiquetar la clase se aplica esta etiqueta a todos los test de la clase, pero si queremos también podemos etiquetar solo algunos tests.

Podes ejecutar los test por tag mediante la consola de comandos.

```shell
mvn test -Dgroups=cuenta
```

También, se puede aplicar mas de un tag a un test.

### Inyección de dependencias y componentes TestInfo y TestReporter

Con **TestInfo** podemos obtener información sobre el test que se está ejecutando, como el nombre del test, la clase, el display name, etc. Esto es útil ya que en cualquier método de acuerdo a las etiquetas que este tenga podríamos hacer algo en particular.

```java
@Tag("cuenta")
class CuentaTest {

    @Test
    void testNombreCuenta(TestInfo testInfo) {
        System.out.println("testInfo: " + testInfo.getDisplayName());
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertTrue(actualValue.equals("Diego"));
    }

    @Test
    void testSaldoCuenta(TestInfo testInfo) {
        System.out.println("testInfo: " + testInfo.getDisplayName());
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }
}
```

El otro componente que podemos inyectar es **TestReporter**, este nos permite enviar información adicional a la consola de salida, algo mas elaborado que un simple **System.out.println**.

```java	
@Tag("cuenta")
class CuentaTest {

    @Test
    void testNombreCuenta(TestInfo testInfo, TestReporter testReporter) {
        System.out.println("testInfo: " + testInfo.getDisplayName());
        testReporter.publishEntry("testNombreCuenta", "Ejecutando: " + testInfo.getDisplayName());
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        String expectedValue = "Diego";
        String actualValue = cuenta.getPersona();
        Assertions.assertTrue(actualValue.equals("Diego"));
    }

    @Test
    void testSaldoCuenta(TestInfo testInfo, TestReporter testReporter) {
        System.out.println("testInfo: " + testInfo.getDisplayName());
        testReporter.publishEntry("testSaldoCuenta", "Ejecutando: " + testInfo.getDisplayName());
        Cuenta cuenta = new Cuenta("Diego", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }
}
```

Por ejemplo mediante **TestInfo** podemos obtener el nombre de el o los tags que tiene un test y en base a esto ejecutar alguna accionó.

```java
@Tag("cuenta")
@DisplayName("[TEST :: Nombre de la cuenta]")
class CuentaTest {

    @Test
    void testNombreCuenta(TestInfo testInfo) {
        if (testInfo.getTags().contains("cuenta")) {
            System.out.println("DO SOMETHING");
        }
    }
}
```

### TimeOut en JUnit 5

Si estamos ejecutando algún test y este tarda mas de lo esperado, podemos definir un tiempo máximo de ejecución para que si el test tarda mas de este tiempo, falle.

```java
@Test
@Timeout(value = 5, unit = TimeUnit.SECONDS)
void testTimeOut() throws InterruptedException {
    TimeUnit.SECONDS.sleep(6);
}
```

En este caso, estamos definiendo un tiempo máximo de 5 segundos, si el test tarda mas de este tiempo, fallará. En este caso falla ya que estamos haciendo que el test espere 6 segundos.

Podemos usar assertTimeout para definir un tiempo máximo de ejecución para un test.

```java
@Test
void testTimeOut() {
    assertTimeout(Duration.ofSeconds(5), () -> {
        TimeUnit.SECONDS.sleep(6);
    });
}
```

### Maven Surefire Plugin

Maven Surefire Plugin es un plugin de Maven que nos permite ejecutar los test de nuestra aplicación, este plugin se encarga de ejecutar los test y generar un reporte con los resultados de los test.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
             <version>3.2.2</version>
         </plugin>
     </plugins>
</build>
```

SureFire nos genera un reporte de los test en formato XML, este reporte se guarda en la carpeta target/surefire-reports.

Podemos aplicar configuraciones al plugin para que ejecute los test de una forma particular.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.2</version>
            <configuration>
                <groups>cuenta</groups>
                <systemPropertyVariables>
                    <ENV>dev</ENV>
                </systemPropertyVariables>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Con esto estamos indicando que solo ejecute los test que tengan el tag **cuenta** y que la variable de entorno **ENV** sea **dev**.