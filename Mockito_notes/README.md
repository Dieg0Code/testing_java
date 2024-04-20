# Mockito

Mockito es un framework de pruebas que nos permite crear objetos simulados **mock** en un entorno controlado y determinado. Le podemos dar comportamientos a un mock y verificar que se comporta como esperamos.

Cuando trabajamos con Mockito tenemos que preparar nuestro entorno de pruebas, por ejemplo un clase service va a tener dependencias, estas dependencias se comunican con fuentes externas como bases de datos, servicios web, etc. Para aislar estas dependencias y poder probar nuestro código de manera unitaria, vamos a utilizar Mockito para simular estas dependencias. La idea es que los test sean algo ágil por lo que no queremos esperar a que se conecte a una base de datos o a un servicio web, es por esto que se usa Mockito para crear mocks de estas dependencias, es decir, objetos simulados que se comportan como queremos.

En Mockito trabajamos con tres partes, **dado que**, **cuando** y **entonces**. En la parte de **dado que** preparamos el entorno de pruebas, en la parte de **cuando** ejecutamos el método que queremos probar y en la parte de **entonces** verificamos que el método se comporta como esperamos.