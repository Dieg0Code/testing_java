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