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

Por defecto la aplicación se inicia con el perfil `dev`. Puede cambiarlo estableciendo la propiedad `spring.profiles.active` o la variable de entorno `SPRING_PROFILES_ACTIVE`.

## Configuración

Los archivos en `src/main/resources` contienen propiedades para distintos entornos (`application-local.properties`, `application-dev.properties`, `application-prod.properties`, etc.). Ajuste estas propiedades según sus credenciales y entorno. En producción se espera que defina las variables de entorno `JDBC_URL`, `DB_USERNAME` y `DB_PASSWORD` utilizadas en `application-prod.properties`.

La variable de entorno `ORIGIN_URL` indica la URL permitida para solicitudes al backend y se utiliza para generar los enlaces enviados por correo electrónico.

## Estructura
- `src/main/java` – código Java de la aplicación.
- `src/main/resources` – configuraciones y recursos.

## Despliegue
El artefacto generado se ubica en `target/marketplace-backend-0.0.1-SNAPSHOT.jar`. Para ejecutarlo en producción, utilice:

```bash
java -jar target/marketplace-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

Asegúrese de que las variables de entorno mencionadas estén configuradas antes de iniciar la aplicación.
