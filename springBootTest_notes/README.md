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