# 📚 Guía de Uso del Módulo Common-Library

## Descripción General

El módulo `common-library` contiene código compartido reutilizable entre todos los microservicios de la arquitectura.

```
comum-library/
├── config/
│   ├── CorsConfig.java           → Configuración CORS centralizada
│   └── SwaggerConfig.java        → Configuración Swagger/OpenAPI base
├── dto/
│   └── ErrorResponse.java        → Respuesta de error estándar
├── exception/
│   ├── GlobalExceptionHandler.java → Manejador de excepciones global
│   └── ResourceNotFoundException.java → Excepción personalizada
└── security/
    ├── JwtFilter.java            → Filtro JWT para autenticación
    └── JwtService.java           → Servicio de generación y validación JWT
```

---

## 🔧 Componentes por Funcionalidad

### 1. Configuración CORS (`CorsConfig`)

**Ubicación:** `com.biblioteca.common.config.CorsConfig`

**Qué hace:**
- Permite solicitudes desde `http://localhost:4200` (frontend)
- Autoriza métodos HTTP: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Permite todos los headers en solicitudes
- Expone headers: Authorization, Content-Type
- Caché preflight: 3600 segundos

**Cómo usarlo en cada servicio:**

```java
// En cada microservicio, extender la clase base:
@Configuration
public class CorsConfig extends com.biblioteca.common.config.CorsConfig {
    // Personalización adicional si es necesaria
}
```

---

### 2. Configuración Swagger (`SwaggerConfig`)

**Ubicación:** `com.biblioteca.common.config.SwaggerConfig`

**Qué hace:**
- Configura la documentación OpenAPI
- Define esquema de seguridad Bearer JWT
- Proporciona métodos parametrizables para título y descripción

**Cómo usarlo en cada servicio:**

```java
@Configuration
public class SwaggerConfig extends com.biblioteca.common.config.SwaggerConfig {

    @Override
    protected String getServiceTitle() {
        return "Mi Service API";  // Personalizar
    }

    @Override
    protected String getServiceDescription() {
        return "Descripción de mi servicio";  // Personalizar
    }
}
```

**Ejemplo en Auth Service:**
```java
@Configuration
public class SwaggerConfig extends com.biblioteca.common.config.SwaggerConfig {

    @Override
    protected String getServiceTitle() {
        return "Auth Service API";
    }

    @Override
    protected String getServiceDescription() {
        return "API de autenticación y gestión de usuarios";
    }
}
```

---

### 3. Servicio JWT (`JwtService`)

**Ubicación:** `com.biblioteca.common.security.JwtService`

**Métodos disponibles:**

```java
// Generar un nuevo token
String token = jwtService.generarToken("username");

// Extraer nombre de usuario del token
String username = jwtService.extraerUsuario(token);

// Extraer rol del token
String rol = jwtService.extraerRol(token);

// Validar token
boolean isValid = jwtService.validarToken(token, "username");
```

**Configuración requerida:**

En `application.properties` de cada servicio:
```properties
jwt.secret=tu_secreto_muy_largo_y_seguro_aqui_minimo_256_bits
```

**Cómo usarlo en cada servicio:**

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public UserDetailsService userDetailsService() {
        // Tu implementación
    }
}
```

El `JwtService` se inyecta automáticamente:
```java
@Service
public class MiService {
    
    @Autowired
    private JwtService jwtService;
    
    public void procesarToken(String token) {
        String username = jwtService.extraerUsuario(token);
        // ... lógica
    }
}
```

---

### 4. Filtro JWT (`JwtFilter`)

**Ubicación:** `com.biblioteca.common.security.JwtFilter`

**Qué hace:**
- Intercepta todas las solicitudes HTTP
- Extrae el token del header `Authorization: Bearer {token}`
- Valida y carga el usuario autenticado en el contexto de seguridad
- Asigna roles automáticamente

**Cómo usarlo en cada servicio:**

```java
@Configuration
public class JwtFilter extends com.biblioteca.common.security.JwtFilter {
    public JwtFilter(com.biblioteca.common.security.JwtService jwtService) {
        super(jwtService);
    }
}
```

O simplemente extender:
```java
public class JwtFilter extends com.biblioteca.common.security.JwtFilter {
    public JwtFilter(JwtService jwtService) {
        super(jwtService);
    }
}
```

---

### 5. Manejador de Excepciones (`GlobalExceptionHandler`)

**Ubicación:** `com.biblioteca.common.exception.GlobalExceptionHandler`

**Excepciones manejadas:**
- `MethodArgumentNotValidException` → Errores de validación
- `ResourceNotFoundException` → Recurso no encontrado
- `IllegalStateException` → Estados inválidos
- `Exception` → Cualquier otra excepción

**Respuesta estándar:**

```json
{
    "timestamp": "2026-05-22T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Recurso no encontrado",
    "path": "/api/usuarios/999"
}
```

**Cómo usarlo en cada servicio:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler extends com.biblioteca.common.exception.GlobalExceptionHandler {
    // Delega automáticamente a la implementación común
    // Opcionalmente, agregar handlers adicionales específicos del servicio
}
```

---

### 6. Excepción Personalizada (`ResourceNotFoundException`)

**Ubicación:** `com.biblioteca.common.exception.ResourceNotFoundException`

**Cómo usar:**

```java
@Service
public class MiService {
    
    public Recurso obtenerRecurso(Long id) {
        return recursoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado: " + id));
    }
}
```

**En cada servicio:**

```java
public class ResourceNotFoundException extends com.biblioteca.common.exception.ResourceNotFoundException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### 7. DTO ErrorResponse

**Ubicación:** `com.biblioteca.common.dto.ErrorResponse`

**Estructura:**

```java
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
}
```

**En cada servicio:**

```java
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
}
```

---

## 🔗 Dependencias entre Componentes

```
┌─────────────────────┐
│   JwtService        │  ← Genera y valida JWT
└──────────┬──────────┘
           │
           ├─→ JwtFilter (usa JwtService para validar)
           │
           └─→ Controladores (inyectan JwtService)

┌──────────────────────────────────┐
│ GlobalExceptionHandler            │
├──────────────────────────────────┤
│ • ErrorResponse                  │
│ • ResourceNotFoundException        │
│ • Otros handlers                 │
└──────────────────────────────────┘

┌──────────────┐
│ CorsConfig   │  ← Permite comunicación frontend-backend
└──────────────┘

┌──────────────────┐
│ SwaggerConfig    │  ← Documenta la API
└──────────────────┘
```

---

## 🚀 Patrón de Extensión

### Para agregar un nuevo microservicio:

#### 1. Agregar dependencia en `pom.xml`:
```xml
<dependency>
    <groupId>com.biblioteca</groupId>
    <artifactId>common-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 2. Crear clases locales que extiendan las comunes:

```java
// Config
@Configuration
public class CorsConfig extends com.biblioteca.common.config.CorsConfig {}

@Configuration
public class SwaggerConfig extends com.biblioteca.common.config.SwaggerConfig {
    @Override
    protected String getServiceTitle() { return "Mi Nuevo Service"; }
    
    @Override
    protected String getServiceDescription() { return "Descripción"; }
}

// Exception
@RestControllerAdvice
public class GlobalExceptionHandler extends com.biblioteca.common.exception.GlobalExceptionHandler {}

public class ResourceNotFoundException extends com.biblioteca.common.exception.ResourceNotFoundException {
    public ResourceNotFoundException(String message) { super(message); }
}

// DTO
public record ErrorResponse(
    LocalDateTime timestamp, int status, String error, 
    String message, String path) {}

// Security (si usa JWT)
public class JwtFilter extends com.biblioteca.common.security.JwtFilter {
    public JwtFilter(com.biblioteca.common.security.JwtService jwtService) {
        super(jwtService);
    }
}
```

#### 3. Configurar `application.properties`:
```properties
jwt.secret=tu_secreto_seguro
spring.datasource.url=jdbc:postgresql://localhost:5432/basedatos
spring.datasource.username=usuario
spring.datasource.password=contraseña
```

---

## 📋 Checklist de Implementación

- [ ] Agregar `common-library` al módulo raíz `pom.xml`
- [ ] Agregar dependencia en `pom.xml` del nuevo servicio
- [ ] Extender `CorsConfig`
- [ ] Extender `SwaggerConfig` con títulos personalizados
- [ ] Extender `GlobalExceptionHandler`
- [ ] Extender `ResourceNotFoundException`
- [ ] Crear record `ErrorResponse` local
- [ ] Configurar `jwt.secret` en `application.properties`
- [ ] Extender `JwtFilter` si es necesario autenticación
- [ ] Compilar y verificar

---

## 🔍 Verificar la Integración

```bash
# Compilar el proyecto
mvn clean install -DskipTests

# Ejecutar tests
mvn test

# Verificar con SonarQube
mvn clean verify sonar:sonar
```

---

## 📖 Referencias

- **JWT**: [java-jwt.github.io/](https://java-jwt.github.io/)
- **OpenAPI/Swagger**: [springdoc.org/](https://springdoc.org/)
- **Spring Security**: [spring.io/projects/spring-security](https://spring.io/projects/spring-security)
- **Maven**: [maven.apache.org/](https://maven.apache.org/)

---

**✅ Módulo Common-Library Implementado y Listo**

