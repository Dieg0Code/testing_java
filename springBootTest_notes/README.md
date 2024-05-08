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