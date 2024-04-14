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
