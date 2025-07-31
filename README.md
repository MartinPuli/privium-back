# Marketplace Backend

Este proyecto contiene el backend de Privium (Marketplace), implementado con Spring Boot y Java 17.

## Requisitos
- Java 17
- Maven 3.9+

## Compilación
Ejecute:

```bash
mvn clean package
```

## Ejecución en desarrollo
Puede iniciar la aplicación con:

```bash
mvn spring-boot:run
```

Utilice el perfil correspondiente (por ejemplo `local` o `dev`) configurado en los archivos de `src/main/resources`.

## Configuración

Los archivos en `src/main/resources` contienen propiedades para distintos entornos (`application-local.properties`, `application-dev.properties`, etc.). Ajuste estas propiedades según sus credenciales y entorno.

## Estructura
- `src/main/java` – código Java de la aplicación.
- `src/main/resources` – configuraciones y recursos.

## Despliegue
El artefacto generado se ubica en `target/marketplace-backend-0.0.1-SNAPSHOT.jar`. Puede ejecutarlo con `java -jar`.
