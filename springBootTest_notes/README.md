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