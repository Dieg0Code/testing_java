# Notas Testing con Spring Boot

## Injection de dependencias

La inyección de dependencias es suministrar a un objeto una referencia de otro objeto que necesite según la relación. Resuelve el problema de reutilización y modularized entre componentes.

A fines prácticos la inyección de dependencias consiste en pasar una referencia de un objeto a otro objeto para que este pueda ser manipulado por el objeto que lo recibe.

En Spring esto funciona de la siguiente manera. Un contenedor se encarga de gestionar las infancias y dependencias de componentes mediante la relación e inyección de objetos.

La inyección de dependencias considera como mala practica crear objetos con el operador **new** ya que esto acopla las clases y no permite la reutilización de código.

La ventaja de usar inyección de dependencias es que nos brinda un bajo acoplamiento entre los objetos.

En spring esto se logra usando la anotación **@Autowired**. Esta anotación inyecta un objeto de spring (bean o componente) en un atributo de otro objeto. Es decir, inyectar un bean de spring en el componente actual (en alguna clase anotada como @Component, @Service, @Controller o con @Repository). Por defecto, la inyección falla si no encuentra candidatos disponibles.

Podemos usar @Autowired en atributos, métodos setter y constructores.

En el atributo.

```java
@Component
public class UsuarioComponent {

    @Autowired
    private IUsuarioService usuarioService;

    //...
}
```

En el método setter.

```java
@Component
public class UsuarioComponent {

    private IUsuarioService usuarioService;

    @Autowired
    public void setUsuarioService(IUsuarioservice usuarioService) {
        this.usuarioService = usuarioService;
    }
}
```
En el constructor.

```java
@Component
public class UsuarioComponent {

    private IUsuarioService usuarioService;

    @Autowired
    public UsuarioComponent(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
}
```

## Anotación @Component y sus derivados

**@Component** es un estereotipo genérico para cualquier componente manejado por Spring.

**@Repository**, **@Service** y **@Controller** son especializaciones de **@Component** para casos específicos.

- **@Repository** para persistencia, componentes que acceden a los datos (DAO's).
- **@Service** para servicios de lógica de negocio.
- **@Controller** para controladores MVC.

## Componentes o beans

El termino **"bean"** se utiliza para referirse a cualquier componente  manejado por Spring.

Los beans deben ser anotado con la anotación **@Component** o alguna de sus especializaciones.

Debe tener un constructor vacío por defecto, sin argumentos.

Cualquier beans anotado con **@Component** (o derivados) bajo el package base serán instanciados y manejados por el contenedor DI de Spring (Auto-scanning).

### Anotaciones @Configuration y  @Bean

Una clase anotada con **@Configuration** indica que la clase puede ser utilizada por el contenedor Spring como una fuente de definiciones beans.

```java
@Configuration
public class AppConfig {

    @Bean
    public IUsuarioService registrarUsuarioService() {
        return new UsuarioServiceImpl();
    }
}
```

Básicamente, **@Bean** permite crear objetos y registrarlo en el contenedor pero mediante métodos.

La anotación **@Bean** juega el mismo papel que anotar la clase con **@Component**.

Lo definido anteriormente en la clase AppConfig, es equivalente a:

```java
@Component
public class UsuarioService implements IUsuarioService {
    //...
}
```

## Repositories en Spring Data JPA

Spring Data JPA es un proyecto de Spring que simplifica el acceso a datos en aplicaciones basadas en JPA. Nos ofrece una abstracción de alto nivel para trabajar con JPA.

Spring Data JPA nos permite definir repositorios de datos de una manera muy sencilla y sin necesidad de escribir una sola línea de código.

### Query Methods

Los **Query Methods** son métodos que Spring Data JPA genera automáticamente a partir del nombre del método. Estos métodos permiten realizar consultas a la base de datos sin necesidad de escribir una consulta JPQL (Java Persistence Query Language).

```java
public interface UserRepository extends Repository<User, Long> {

    List<User> findByEmailAddressAndLastname(String emailAddress, String lastname);

    // use @Query to provide a JPQL query
    @Query("select u from User u where u.emailAddress = ?1")
    User findByEmailAddress(String emailAddress);
}
```

Los **query methods** tienen una nomenclatura específica que Spring Data JPA interpreta para generar la consulta. Por ejemplo, el método findByEmailAddressAndLastname genera la siguiente consulta:

```sql
select u from User u where u.emailAddress = ?1 and u.lastname = ?2
```

**find** es la palabra clave que indica que se trata de una consulta, es equivalente a **SELECT** en SQL, **By** es equivalente a **WHERE**, luego **EmailAddress** y **Lastname** son los atributos de la entidad User que se utilizan para filtrar los resultados, con **And** se indica que se deben cumplir ambas condiciones.

Mediante **keywords** podemos generar consultas sin la necesidad de escribir una consulta JPQL.

Por ejemplo:

| Keyword | Sample | JPQL |
| --- | --- | --- |
| And | findByLastnameAndFirstname | where x.lastname = ?1 and x.firstname = ?2 |
| Or | findByLastnameOrFirstname | where x.lastname = ?1 or x.firstname = ?2 |
| Is, Equals | findByFirstname, findByFirstnameIs, findByFirstnameEquals | where x.firstname = ?1 |
| Between | findByStartDateBetween | where x.startDate between ?1 and ?2 |
| LessThan | findByAgeLessThan | where x.age < ?1 |
| LessThanEqual | findByAgeLessThanEqual | where x.age <= ?1 |
| GreaterThan | findByAgeGreaterThan | where x.age > ?1 |

Y así, para mas ejemplos de **keywords** podemos consultar la (documentación oficial de Spring Data JPA.)[https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html]

En caso de que este tipo de consultas no nos parezcan claras o no se ajusten a nuestras necesidades, podemos usar la anotación **@Query** para escribir una consulta JPQL, la cual es mas declarativa y por ende mas fácil de entender.

```java
@Query("select u from User u where u.emailAddress = ?1")
User findByEmailAddress(String emailAddress);
```

En este tipo de consultas no importa como se llame el método, ya que la consulta JPQL se define en la anotación **@Query**.

```java
@Query("select u from User u where u.emailAddress = ?1 and u.lastname = ?2")
List<User> obtenerUsuariosPorEmailYApellido(String email, String apellido);
```

### Test de integración con JpaTest

Para realizar test de integración con Spring Data JPA, podemos usar la anotación **@DataJpaTest**.

```java
@DataJpaTest
public class IntegrationJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void findByIdTest() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Diego", cuenta.orElseThrow().getNombre());
    }
}
```

La anotación **@DataJpaTest** se encarga de configurar el contexto de Spring para realizar test de integración con JPA.

En este caso estamos probando un método de un repositorio de Spring Data JPA.

```java
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    @Query("SELECT c FROM Cuenta c WHERE c.nombre = ?1")
    Optional<Cuenta> findByNombre(String nombre);
}
```

### Hibernate

Hibernate es un framework de mapeo objeto-relacional (ORM) para la plataforma Java. Facilita el mapeo de atributos de una base de datos relacional a objetos Java.

Hibernate es una implementación de JPA (Java Persistence API), que es una especificación de Java para el mapeo objeto-relacional.

Hibernate nos permite trabajar con objetos Java en lugar de escribir consultas SQL.


```java
@Entity
@Table(name = "cuentas")
public class Cuenta {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String nombre;
        private BigDecimal saldo;
}
```

La anotación **@Entity** indica que la clase es una entidad JPA, es decir, que se mapea a una tabla en la base de datos, mientras que la anotación **@Table** se utiliza para especificar el nombre de la tabla en la base de datos.

Tenemos ademas la anotación **@Id** que indica que el atributo es la clave primaria de la tabla, y **@GeneratedValue** que indica que el valor de la clave primaria se generará automáticamente.

En el contexto de nuestro test, cuando ejecutamos, Hibernate primero elimina las tablas en caso de que existan.

```sql
drop table if exists cuentas CASCADE
```

Luego la crea en base a la definición de la entidad en nuestra clase Cuenta.

```sql
create table cuentas (
    id bigint generated by default as identity,
    nombre varchar(255),
    saldo decimal(19,2),
    primary key (id)
)
```

Luego se ejecuta nuestro **import.sql** que definimos previamente.

```sql
INSERT INTO cuentas (nombre, saldo) VALUES ('Diego', 1000);
INSERT INTO cuentas (nombre, saldo) VALUES ('Pedro', 2000);

INSERT INTO bancos (nombre, total_transferencias) VALUES ('Banco 1', 0);
```

Luego se ejecuta nuestro test.

```java
@DataJpaTest
public class IntegrationJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void findByIdTest() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Diego", cuenta.orElseThrow().getNombre());
    }
}
```

Y cuando finaliza, Hibernate elimina las tablas.

```sql
drop table if exists cuentas CASCADE
```

Cabe mencionar que cada método de Test es independiente de otro, por tanto, si un método test modifica información de alguna tabla, elimina, modifica o crea un registro, no afectará a otro método test ya que como se mencionó, Hibernate hace un rollback al finalizar cada método test dejando la base de datos en su estado original.

## Test de Controladores con MockMvc

Para los test de controladores debemos anotar la clase con **@WebMvcTest(NombreController.class)**.

También demos inyectar las dependencias con **@Autowired** y usar **MockMvc** para realizar las peticiones.

Considerando que nuestro controlador es el siguiente:

```java
@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;


    // ***************************************************************************************
    // ***************************************************************************************
    // ***************************************************************************************


    @Operation(
            /* -------------------------------------------------------------------------- */
            summary = "Retorna el detalle de una cuenta",
            description = "Retorna el detalle de una cuenta mediante un identificador único"
            /* -------------------------------------------------------------------------- */
    )
    @ApiResponse(
            /* ---------------------------------------------------- */
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Cuenta.class)
            )
            /* ---------------------------------------------------- */
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cuenta details(@PathVariable(name = "id") Long id) {
        return cuentaService.findById(id);
    }


    // ***************************************************************************************
    // ***************************************************************************************
    // ***************************************************************************************


    @Operation(
            /* -------------------------------------------------------------------------- */
            summary = "Realiza una transferencia entre cuentas",
            description = "Realiza una transferencia entre cuentas de un mismo banco"
            /* -------------------------------------------------------------------------- */
    )
    @ApiResponse(
            /* -------------------------------------------------------------------------- */
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TransactionDTO.class)
            )
            /* -------------------------------------------------------------------------- */
    )
    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransactionDTO dto) {
        cuentaService.transferir(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia realizada con éxito");
        response.put("transaction", dto);

        return ResponseEntity.ok(response);
    }
}
```	

Los Test de Controladores se realizan de la siguiente manera:


```java
@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CuentaService cuentaService;

    @Test
    void detailsTest() throws Exception {
        //Given
        when(cuentaService.findById(1L)).thenReturn(Data.crearCuenta001().orElseThrow());

        // When
        mockMvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Diego"))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(cuentaService, times(1)).findById(1L);
    }
}
```

Con **@MockBean** podemos simular el comportamiento de un servicio, en este caso **cuentaService**. **mockMvc.perform** nos permite realizar una petición a la url especificada. luego dentro de esta función empezamos definiendo el método HTTP que realiza la petición, en este caso **get**, el cual se importa desde **org.springframework.test.web.servlet.request.MockMvcRequestBuilders**.

Luego definimos la url a la que se realiza la petición, en este caso **"/api/cuentas/1"**.

Finalmente definimos las validaciones que esperamos en la respuesta, en este caso que el status sea **200**, que el contenido sea de tipo **application/json**, que el atributo **nombre** tenga el valor **Diego** y que el atributo **saldo** tenga el valor **1000**. Todo esto se realiza con **andExpect**.

**jsonPath** nos permite acceder a los atributos de la respuesta, la sintaxis es **$.atributo**, se importa desde **org.springframework.test.web.servlet.result.MockMvcResultMatchers**.

Para el test de transferir, el código es el siguiente:

```java
    @Test
    void transferirTest() throws Exception {
        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia realizada con éxito");
        response.put("transaction", dto);

        // When
        mockMvc.perform(post("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.message").value("Transferencia realizada con éxito"))
                .andExpect(jsonPath("$.transaction.cuentaOrigenId").value(dto.getCuentaOrigenId()))
                .andExpect(jsonPath("$.transaction.cuentaDestinoId").value(dto.getCuentaDestinoId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(cuentaService, times(1)).transferir(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());
    }
```

En este caso, se realiza una petición **post** a la url **"/api/cuentas/transferir"**. Se define el contenido de la petición con **.content(objectMapper.writeValueAsString(dto))**, ya que la petición es de tipo **application/json** y se espera un objeto **TransactionDTO**.

Finalmente se definen las validaciones que se esperan en la respuesta, en este caso que el atributo **date** tenga el valor de la fecha actual, que el atributo **message** tenga el valor **"Transferencia realizada con éxito"** y que los atributos **cuentaOrigenId** y **cuentaDestinoId** tengan los valores definidos en el objeto **dto**.

También se valida que el contenido de la respuesta sea igual al objeto **response** definido previamente.

Finalmente verificamos que el método **transferir** del servicio **cuentaService** se haya llamado una vez con los parámetros definidos en el objeto **dto**.

### Implementando Services y Controllers con TDD

Para implementar nuevos controladores y servicios bajo la filosofía TDD el ciclo seria el siguiente:

1. Agregamos a la interfaz del servicio los métodos que queremos implementar.

```java	
public interface CuentaService {
    List<Cuenta> findAll();

    Cuenta save(Cuenta cuenta);
}
```

2. Agregamos estos métodos a la implementación del servicio.

```java
public class CuentaServiceImpl implements CuentaService {
    @Override
    public List<Cuenta> findAll() {
        return null;
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        return null;
    }
}
```

Por ahora los dejamos retornando **null**.

3. Agregamos estos servicios al controlador.

```java
@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {
    @Autowired
    private CuentaService cuentaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> findAll() {
        return null;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cuenta save(@RequestBody Cuenta cuenta) {
        return null;
    }
}
```

Por ahora los dejamos retornando **null**.

4. Comenzamos creando los test para los controladores.

```java
    @Test
    void listarTest() throws Exception {
        // Given
        List<Cuenta> cuentas = Arrays.asList(Data.crearCuenta001().orElseThrow(), Data.crearCuenta002().orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);

        // When
        mockMvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Diego"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].nombre").value("John"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));
    }
```

5. Ejecutamos el test y fallará porque estamos retornando **null** en el controlador.

5. Implementamos el método en el controlador.

```java
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> findAll() {
        return cuentaService.findAll();
    }
```

Y entonces pasa el test. Este es el ciclo que debemos seguir para implementar nuevos servicios y controladores bajo la filosofía TDD.

## Test de Integración a servicios REST con WebTestClient

Una prueba de integración de un servicio REST consiste en levantar el contexto de Spring y realizar peticiones HTTP a los endpoints de nuestra aplicación para validar que el servicio funcione correctamente en un ambiente similar al de producción.

Para comenzar debemos agregar una dependencia en nuestro archivo **pom.xml**.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <scope>test</scope>
</dependency>
```

Luego creamos una clase de test.

```java
package com.dieg0code.sprinboot_test.controllers;

import com.dieg0code.sprinboot_test.models.TransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    @Autowired
    private WebTestClient webTestClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void transferirTest() throws JsonProcessingException {
        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("100"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia realizada con éxito");
        response.put("transaction", dto);

        // When
        webTestClient.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").isEqualTo("Transferencia realizada con éxito")
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));

    }
}
```

Comenzamos anotando la clase con **@SpringBootTest(webEnvironment = RANDOM_PORT)** en donde **RANDOM_PORT** indica que se levantará un puerto aleatorio para realizar las pruebas.

Inyectamos la dependencia **WebTestClient** con **@Autowired**.

Luego definimos el método de test **transferirTest** en donde comenzamos definiendo el objeto **dto** con los datos necesarios para realizar la transferencia.

Luego usando el **webTestClient** realizamos una petición **post** a la url **http://localhost:8080/api/cuentas/transferir** con el objeto **dto** como contenido de la petición, esta petición se hace directamente al endpoint real, no a un mock, es por eso que para que funcione correctamente debemos tener nuestra aplicación corriendo.

Definimos el tipo de contenido de la petición con **.contentType(MediaType.APPLICATION_JSON)**, el contenido de la petición con **.bodyValue(dto)** y realizamos la petición con **.exchange()**.

Finalmente definimos las validaciones que esperamos en la respuesta, en este caso que el atributo **message** tenga el valor **"Transferencia realizada con éxito"**, que el atributo **date** tenga el valor de la fecha actual y que el contenido de la respuesta sea igual al objeto **response** definido previamente.

Es importante que cada vez que ejecutamos los test de integración, los datos de la BD no hayan sido modificados por otro proceso o test de integración, es importante partir de cero y reiniciar el servidor antes de ejecutar los test de integración.

### @TestOrder

Ya que las pruebas de integración trabajan con los endpoint reales, es importante definir el orden en el que se ejecutan los test ya que, por ejemplo, si se ejecuta primero un test que hace una consulta sobre una cuenta y luego otro que hace una transferencia y luego se ejecuta uno que hace una consulta a una segunda cuenta, la cual recibió o emitió una transferencia, el resultado de esta segunda consulta no será el esperado. Para esto es el **@TestOrder**.

```java
package com.dieg0code.sprinboot_test.controllers;

import com.dieg0code.sprinboot_test.models.Cuenta;
import com.dieg0code.sprinboot_test.models.TransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    @Autowired
    private WebTestClient webTestClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void transferirTest() throws JsonProcessingException {
        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("100"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia realizada con éxito");
        response.put("transaction", dto);

        // When
        webTestClient.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").isEqualTo("Transferencia realizada con éxito")
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @Order(2)
    void detailsTest() {
        webTestClient.get().uri("/api/cuentas/1")
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Diego")
                .jsonPath("$.saldo").isEqualTo(900);
    }

    @Test
    @Order(3)
    void detailsTest2() {
        webTestClient.get().uri("/api/cuentas/2")
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta cuenta = response.getResponseBody();
                    assertNotNull(cuenta);
                    assertEquals("Pedro", cuenta.getNombre());
                    assertEquals(2100, cuenta.getSaldo().intValue());
                });
    }
}
```

Así nos aseguramos que los test se ejecuten en el orden que definimos con **@Order**, en este caso, transferimos, consultamos la cuenta 1 y luego consultamos la cuenta 2.

Test para listar cuentas:

```java
@Test
    @Order(4)
    void findAllTest() {
        webTestClient.get().uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].nombre").isEqualTo("Diego")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900)
                .jsonPath("$[1].nombre").isEqualTo("Pedro")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void findAllTest2() {
        webTestClient.get().uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response -> {
                    List<Cuenta> cuentas = response.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2, cuentas.size());
                    assertEquals(1L, cuentas.get(0).getId());
                    assertEquals("Diego", cuentas.get(0).getNombre());
                    assertEquals(900, cuentas.get(0).getSaldo().intValue());
                    assertEquals(2L, cuentas.get(1).getId());
                    assertEquals("Pedro", cuentas.get(1).getNombre());
                    assertEquals(2100, cuentas.get(1).getSaldo().intValue());
                });
    }
```

Test para guardar una cuenta:

```java
@Test
    @Order(6)
    void saveTest() {
        // Given
        Cuenta cuenta = new Cuenta(null, "Matias", new BigDecimal("3000"));

        // When
        webTestClient.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.nombre").isEqualTo("Matias")
                .jsonPath("$.saldo").isEqualTo(3000);
    }

    @Test
    @Order(7)
    void saveTest2() {
        // Given
        Cuenta cuenta = new Cuenta(null, "Fabian", new BigDecimal("4000"));

        // When
        webTestClient.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta cuentaResponse = response.getResponseBody();
                    assertNotNull(cuentaResponse);
                    assertEquals(4L, cuentaResponse.getId());
                    assertEquals("Fabian", cuentaResponse.getNombre());
                    assertEquals(4000, cuentaResponse.getSaldo().intValue());
                });
    }
```

Test para eliminar una cuenta:

```java
 @Test
    @Order(8)
    void deleteTest() {
        webTestClient.get().uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        webTestClient.delete().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        webTestClient.get().uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        webTestClient.get().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
```

En este test en el que eliminamos una cuenta, primero validamos que existan 4 cuentas, luego eliminamos una cuenta y validamos que ahora existan 3 cuentas, finalmente validamos que la cuenta eliminada ya no exista.

## Test de Integración de servicios REST con TestRestTemplate

**TestRestTemplate** es una clase que nos permite realizar peticiones HTTP a los endpoints de nuestra aplicación en un ambiente de test, similar a como lo hace **WebTestClient**, es una alternativa a este, por supuesto con sus propias características.

Por ejemplo, para un test del endpoint de transferir, el código sería el siguiente:

```java
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    //@LocalServerPort
    //private int port; // Se puede usar para obtener el puerto en el que se está ejecutando la aplicación

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void transferirTest() {
        /// Given
        TransactionDTO dto = new TransactionDTO();
        dto.setMonto( new BigDecimal("100"));
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);

        // When
        ResponseEntity<String> response = client.postForEntity("/api/cuentas/transferir", dto, String.class);
        String json = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con éxito"));
    }
}
```

La sintaxis es mas simple que con **WebTestClient**, en este caso usamos **client.postForEntity** para realizar una petición **post** a la url **"/api/cuentas/transferir"** con el objeto **dto** como contenido de la petición, el tipo de respuesta esperado es **String**.

Luego extraemos el contenido del body de la respuesta y lo guardamos en la variable **json**. Usamos esta variable para hacer las validaciones que esperamos en la respuesta, en este caso que el status sea **200**, que el tipo de contenido sea **application/json** y que el contenido de la respuesta contenga el mensaje **"Transferencia realizada con éxito"**.

Tests para los demas endpoints:

```java
@Test
    @Order(2)
    void trasferirTest2() throws JsonProcessingException {
        TransactionDTO dto = new TransactionDTO();
        dto.setMonto( new BigDecimal("100"));
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);

        // When
        ResponseEntity<String> response = client.postForEntity("/api/cuentas/transferir", dto, String.class);
        String json = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con éxito"));

        // JsonNode para navegar por el json
        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transferencia realizada con éxito", jsonNode.path("message").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaction").path("monto").asText());
        assertEquals(1L, jsonNode.path("transaction").path("cuentaOrigenId").asLong());

        Map<String, Object> res = new HashMap<>();
        res.put("date", LocalDate.now().toString());
        res.put("status", "OK");
        res.put("message", "Transferencia realizada con éxito");
        res.put("transaction", dto);

        assertEquals(objectMapper.writeValueAsString(res), json);

    }
```

```java
    @Test
    @Order(3)
    void detailsTest() {
        ResponseEntity<Cuenta> response = client.getForEntity("/api/cuentas/1", Cuenta.class);
        Cuenta cuenta = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(cuenta);
        assertEquals(1L, cuenta.getId());
        assertEquals("Diego", cuenta.getNombre());
        assertEquals(new BigDecimal("800.00"), cuenta.getSaldo());
    }
```

```java
    @Test
    @Order(4)
    void testListar() {
        ResponseEntity<Cuenta[]> response = client.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(cuentas);
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());

        assertEquals(1L, cuentas.get(0).getId());
        assertEquals("Diego", cuentas.get(0).getNombre());
        assertEquals(new BigDecimal("800.00"), cuentas.get(0).getSaldo());

        assertEquals(2L, cuentas.get(1).getId());
        assertEquals("Pedro", cuentas.get(1).getNombre());
        assertEquals(new BigDecimal("2200.00"), cuentas.get(1).getSaldo());

    }
```

```java
    @Test
    @Order(5)
    void testGuardar() {
        Cuenta cuenta = new Cuenta(null, "John", new BigDecimal("3000.00"));

        ResponseEntity<Cuenta> response = client.postForEntity("/api/cuentas", cuenta, Cuenta.class);
        Cuenta cuentaResponse = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(cuentaResponse);
        assertEquals(3L, cuentaResponse.getId());
        assertEquals("John", cuentaResponse.getNombre());
        assertEquals(new BigDecimal("3000.00"), cuentaResponse.getSaldo());
    }
```

```java
    @Test
    @Order(6)
    void testEliminar() {
        ResponseEntity<Cuenta[]> response = client.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(cuentas);
        assertFalse(cuentas.isEmpty());
        assertEquals(3, cuentas.size());
        assertTrue(cuentas.stream().anyMatch(c -> c.getId() == 3));

        client.delete("/api/cuentas/3");

        response = client.getForEntity("/api/cuentas", Cuenta[].class);
        cuentas = Arrays.asList(response.getBody());

        assertEquals(2, cuentas.size());

        ResponseEntity<Cuenta> response2 = client.getForEntity("/api/cuentas/3", Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        assertFalse(response2.hasBody());
    }
```

### Problemas al ejecutar todos los test

Debido a que estamos usando ambos, **WebTestClient** y **TestRestTemplate**, al ejecutar todos los test, puede que se genere un error debido a que ambos interactúan y modifican datos, por lo que puede que choquen entre sí.

Normalmente solo vamos a usar uno de los dos para hacer tests de integración, en este caso usamos dos para mostrar las diferencias entre ambos.

Podemos solucionar esto usando **@Tag("integracion_rt")** y **@Tag("integracion_wtc")** para marcar los test que usan **TestRestTemplate** y **WebTestClient** respectivamente, para luego ejecutar solo los test que queremos. Entonces podemos configurar la configuración, en este caso de IntelliJ, para que ejecute solo los test que queremos en base a los tags.

También podemos ejecutar los test desde la terminal y excluir los test que no queremos ejecutar.

```bash
.\mvnw test -Dgroups="!integracion_rt"
```

**mvnw** es el wrapper de maven que viene incluido en el proyecto, **test** es el comando para ejecutar los test, **-Dgroups="!integracion_rt"** es para excluir los test que tengan el tag **integracion_rt**.

## Diferencias entre WebTestClient y TestRestTemplate

**WebTestClient** esta hecho para trabajar con programación reactiva, es decir, con **Spring WebFlux**, mientras que **TestRestTemplate** esta hecho para trabajar con programación imperativa, es decir, con **Spring MVC**.

Por lo que lo que en este caso, ya que nuestra api no es reactiva, seria mas común que usemos **TestRestTemplate** que ya viene incluido con Spring Boot, pero podemos usar **WebTestClient** si queremos simplemente agregando la dependencia en nuestro archivo **pom.xml**.