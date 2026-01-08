# 📚 Explicación Paso a Paso: Microservicio de Autenticación

## 🎯 Descripción General

Este microservicio maneja la **autenticación y autorización** de usuarios usando **JWT (JSON Web Tokens)** y **Spring Security**. Permite registrar usuarios, iniciar sesión y proteger endpoints según roles (ADMIN/USER).

---

## 📁 Estructura del Proyecto

```
auth-service/
├── src/main/java/com/biblioteca/
│   ├── AuthServiceApplication.java      # Punto de entrada
│   └── auth/
│       ├── controller/
│       │   ├── AuthController.java      # Login y registro
│       │   └── UsuarioController.java   # CRUD de usuarios (solo ADMIN)
│       ├── dto/
│       │   ├── AuthRequest.java         # Request para login
│       │   ├── AuthResponse.java        # Response con token JWT
│       │   └── RegisterRequest.java     # Request para registro
│       ├── entity/
│       │   └── Usuario.java             # Entidad JPA
│       ├── repository/
│       │   └── UsuarioRepository.java   # Acceso a BD
│       ├── security/
│       │   ├── SecurityConfig.java      # Configuración de seguridad
│       │   └── JwtFilter.java           # Filtro para validar JWT
│       └── service/
│           ├── JwtService.java          # Generar y validar JWT
│           ├── UsuarioService.java      # Lógica de negocio
│           └── Rol.java                 # Enum de roles
└── src/main/resources/
    └── application.properties           # Configuración
```

---

## 🔧 Configuración (`application.properties`)

```properties
# Conexión a PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_service
spring.datasource.username=postgres
spring.datasource.password=a

# Hibernate
spring.jpa.hibernate.ddl-auto=update    # Crea/actualiza tablas automáticamente
spring.jpa.show-sql=true                # Muestra SQL en consola
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Clave secreta para firmar JWT (debe ser de al menos 256 bits)
jwt.secret=mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong12345678
```

**⚠️ Importante**: La clave secreta NO debe estar en texto plano en producción. Usa variables de entorno.

---

## 📊 Modelo de Datos

### 1. **Entidad `Usuario`** (`Usuario.java`)

```java
@Entity
public class Usuario {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(unique = true)
    private String username;       // Debe ser único
    
    private String password;       // Se guarda encriptado con BCrypt
    
    @Enumerated(EnumType.STRING)
    private Rol rol;              // ADMIN o USER
    
    // Getters y Setters...
}
```

**Tabla en PostgreSQL:**
```sql
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(10) NOT NULL
);
```

### 2. **Enum `Rol`** (`Rol.java`)

```java
public enum Rol {
    ADMIN,  // Acceso total (gestionar usuarios, libros, etc.)
    USER    // Acceso limitado (crear préstamos)
}
```

---

## 🔐 Seguridad y JWT

### 1. **Servicio JWT** (`JwtService.java`)

Este servicio maneja la **creación y validación de tokens JWT**.

#### 🔑 **Generar Token**
```java
public String generarToken(String username) {
    return Jwts.builder()
        .setSubject(username)                          // Usuario
        .setIssuedAt(new Date())                       // Fecha de emisión
        .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
        .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma HMAC-SHA256
        .compact();
}
```

**Estructura del Token JWT:**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNTE1...(firma)
    [Header]         [Payload con username]           [Signature]
```

#### ✅ **Validar Token**
```java
public String extraerUsuario(String token) {
    return extraerClaims(token).getSubject();  // Extrae el username del token
}

public boolean validarToken(String token, String username) {
    final String extractedUsername = extraerUsuario(token);
    return (extractedUsername.equals(username) && !isTokenExpired(token));
}
```

### 2. **Filtro JWT** (`JwtFilter.java`)

Este filtro intercepta **todas las peticiones HTTP** y valida el token JWT.

#### 📍 **Flujo de Validación:**

1. **Extraer el header `Authorization`**
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
   ```

2. **Obtener el token** (elimina "Bearer ")
   ```java
   String token = header.substring(7);
   ```

3. **Extraer username del token**
   ```java
   String username = jwtService.extraerUsuario(token);
   ```

4. **Buscar usuario en la BD**
   ```java
   Usuario user = usuarioRepository.findByUsername(username).orElse(null);
   ```

5. **Crear autenticación en el contexto de Spring Security**
   ```java
   UsernamePasswordAuthenticationToken auth = 
       new UsernamePasswordAuthenticationToken(
           username,
           null,
           List.of(new SimpleGrantedAuthority("ROLE_" + user.getRol()))
       );
   SecurityContextHolder.getContext().setAuthentication(auth);
   ```

**⚠️ Nota**: Spring Security requiere que los roles tengan el prefijo `ROLE_`. Por eso usamos `"ROLE_" + user.getRol()`.

### 3. **Configuración de Seguridad** (`SecurityConfig.java`)

Define **qué endpoints están protegidos** y **qué roles pueden acceder**.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())              // Desactiva CSRF (API REST)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()              // Público
            .requestMatchers(HttpMethod.POST, "/libros/**").hasRole("ADMIN")
            .requestMatchers("/usuarios/**").hasRole("ADMIN")     // Solo ADMIN
            .requestMatchers(HttpMethod.POST, "/prestamos/**").hasRole("USER")
            .anyRequest().authenticated()                        // Resto requiere login
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

#### 🔓 **Endpoints Públicos (sin autenticación):**
- `POST /auth/login`
- `POST /auth/register`

#### 🔒 **Endpoints Protegidos:**
- `/usuarios/**` → Solo `ROLE_ADMIN`
- `POST /prestamos/**` → Solo `ROLE_USER`

### 4. **Encriptación de Contraseñas**

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**BCrypt** es un algoritmo de hashing seguro que:
- Genera un hash diferente cada vez (usa "salt" aleatorio)
- Es resistente a ataques de fuerza bruta
- Ejemplo: `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`

---

## 🎮 Controladores (API REST)

### 1. **AuthController** (`/auth/**`)

#### 📍 **POST `/auth/login`** - Iniciar Sesión

**Request:**
```json
{
  "username": "admin",
  "password": "1234"
}
```

**Flujo:**
1. Buscar usuario por username
2. Comparar contraseña con `encoder.matches()`
3. Si es correcto, generar token JWT
4. Devolver token

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIs..."
}
```

**Response (401 Unauthorized):**
```json
"Usuario no encontrado"
// o
"Credenciales inválidas"
```

#### 📍 **POST `/auth/register`** - Registrar Usuario

**Request:**
```json
{
  "username": "juan",
  "password": "pass123",
  "rol": "USER"   // Opcional, por defecto USER
}
```

**Flujo:**
1. Verificar que el usuario no exista
2. Encriptar contraseña con BCrypt
3. Asignar rol (USER por defecto)
4. Guardar en BD
5. Generar token JWT
6. Devolver token

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response (409 Conflict):**
```json
"El usuario ya existe"
```

### 2. **UsuarioController** (`/usuarios/**`)

**⚠️ Todos estos endpoints requieren `ROLE_ADMIN`**

#### 📍 **POST `/usuarios`** - Crear Usuario

**Request:**
```json
{
  "username": "pedro",
  "password": "pass456",
  "rol": "USER"
}
```

**Response (201 Created):**
```json
{
  "id": 3,
  "username": "pedro",
  "password": "$2a$10$...",  // Encriptado
  "rol": "USER"
}
```

#### 📍 **GET `/usuarios`** - Listar Usuarios

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "admin",
    "password": "$2a$10$...",
    "rol": "ADMIN"
  },
  {
    "id": 2,
    "username": "juan",
    "password": "$2a$10$...",
    "rol": "USER"
  }
]
```

#### 📍 **GET `/usuarios/{id}`** - Buscar por ID

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "password": "$2a$10$...",
  "rol": "ADMIN"
}
```

**Response (404 Not Found):** (Si no existe)

#### 📍 **PUT `/usuarios/{id}`** - Actualizar Usuario

**Request:**
```json
{
  "username": "admin_modificado",
  "password": "newpass",
  "rol": "ADMIN"
}
```

**Nota:** Solo se actualizan los campos que se envíen (no es obligatorio enviar todos).

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin_modificado",
  "password": "$2a$10$...",  // Nueva contraseña encriptada
  "rol": "ADMIN"
}
```

#### 📍 **DELETE `/usuarios/{id}`** - Eliminar Usuario

**Response (204 No Content):** Usuario eliminado exitosamente

**Response (404 Not Found):** Usuario no existe

---

## 🔄 Flujo Completo de Autenticación

### 🚀 **Escenario 1: Registro y Login**

```
1. Cliente                      2. AuthController         3. Base de Datos
   |                                  |                         |
   |--POST /auth/register------------>|                         |
   |  {username, password, rol}       |                         |
   |                                  |--Verificar si existe--->|
   |                                  |<-----No existe----------|
   |                                  |--Encriptar password     |
   |                                  |--Guardar usuario------->|
   |                                  |<-----Usuario creado-----|
   |                                  |--Generar JWT            |
   |<-----{token}---------------------|                         |
   |                                                            |
   |--POST /auth/login--------------->|                         |
   |  {username, password}            |                         |
   |                                  |--Buscar usuario-------->|
   |                                  |<-----Usuario encontrado-|
   |                                  |--Validar password       |
   |                                  |--Generar JWT            |
   |<-----{token}---------------------|                         |
```

### 🛡️ **Escenario 2: Acceso a Endpoint Protegido**

```
Cliente                JwtFilter              SecurityConfig        Controller
  |                        |                        |                  |
  |--GET /usuarios-------->|                        |                  |
  |  Header: Authorization |                        |                  |
  |  Bearer eyJhbG...      |                        |                  |
  |                        |--Extraer token         |                  |
  |                        |--Validar firma         |                  |
  |                        |--Extraer username      |                  |
  |                        |--Buscar en BD          |                  |
  |                        |--Crear Authentication  |                  |
  |                        |--Setear en Context     |                  |
  |                        |----------------------->|                  |
  |                        |                        |--Verificar rol   |
  |                        |                        |  (hasRole ADMIN) |
  |                        |                        |----------------->|
  |                        |                        |                  |--Listar usuarios
  |<-------[Lista de usuarios]----------------------------------------|
```

### ❌ **Escenario 3: Token Inválido o Expirado**

```
Cliente                JwtFilter              SecurityConfig
  |                        |                        |
  |--GET /usuarios-------->|                        |
  |  Bearer token_invalido |                        |
  |                        |--Extraer token         |
  |                        |--Validar firma [ERROR] |
  |                        |--No setea Auth         |
  |                        |----------------------->|
  |                        |                        |--No hay Authentication
  |                        |                        |--Rechazar request
  |<-------401 Unauthorized-----------------------|
```

---

## 🧪 Pruebas con Postman

### 1️⃣ **Registrar un Usuario**

```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "1234",
  "rol": "ADMIN"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTczNjM0ODY1M..."
}
```

### 2️⃣ **Login**

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "1234"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 3️⃣ **Listar Usuarios (requiere ROLE_ADMIN)**

```http
GET http://localhost:8080/usuarios
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "username": "admin",
    "password": "$2a$10$N9qo8uLOickgx2ZMRZoMye...",
    "rol": "ADMIN"
  }
]
```

### 4️⃣ **Crear Usuario (requiere ROLE_ADMIN)**

```http
POST http://localhost:8080/usuarios
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "username": "maria",
  "password": "pass123",
  "rol": "USER"
}
```

---

## 🔍 Conceptos Clave

### ¿Qué es JWT?

**JWT (JSON Web Token)** es un estándar para transmitir información de forma segura entre partes como un objeto JSON.

**Estructura:**
```
[Header].[Payload].[Signature]
```

**Ejemplo decodificado:**
```json
// Header
{
  "alg": "HS256",
  "typ": "JWT"
}

// Payload
{
  "sub": "admin",
  "iat": 1736348653,
  "exp": 1736435053
}

// Signature (firma HMAC-SHA256)
```

**Ventajas:**
- ✅ **Stateless**: No se guarda en el servidor (escalable)
- ✅ **Portable**: Funciona en cualquier plataforma
- ✅ **Seguro**: Firmado criptográficamente

### ¿Por qué Spring Security?

Spring Security es el framework estándar para:
- 🔐 Autenticación (¿quién eres?)
- 🛡️ Autorización (¿qué puedes hacer?)
- 🔒 Protección contra ataques (CSRF, XSS, etc.)

### ¿Qué es BCrypt?

BCrypt es un algoritmo de hashing diseñado para ser **lento** (resistente a ataques de fuerza bruta).

**Características:**
- 🔄 Usa "salt" aleatorio (mismo password = hash diferente cada vez)
- 🐌 Configurable en lentitud (factor de costo)
- ✅ Imposible de revertir (hash → password)

---

## 📊 Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENTE (Postman/Frontend)           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Spring Boot Application                   │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              JwtFilter (Intercepta requests)          │ │
│  │  - Valida token JWT                                   │ │
│  │  - Carga usuario y rol en SecurityContext            │ │
│  └───────────────────────────────────────────────────────┘ │
│                              │                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │         SecurityConfig (Autorización)                 │ │
│  │  - /auth/** → Público                                 │ │
│  │  - /usuarios/** → ROLE_ADMIN                          │ │
│  │  - /prestamos/** → ROLE_USER                          │ │
│  └───────────────────────────────────────────────────────┘ │
│                              │                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │             Controllers (API REST)                    │ │
│  │  - AuthController: /auth/login, /auth/register       │ │
│  │  - UsuarioController: CRUD /usuarios                 │ │
│  └───────────────────────────────────────────────────────┘ │
│                              │                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │               Services (Lógica de negocio)            │ │
│  │  - JwtService: Genera y valida tokens                │ │
│  │  - UsuarioService: CRUD de usuarios                  │ │
│  └───────────────────────────────────────────────────────┘ │
│                              │                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │           Repository (Acceso a datos)                 │ │
│  │  - UsuarioRepository: JPA Repository                 │ │
│  └───────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                PostgreSQL Database                          │
│  - Tabla: usuario (id, username, password, rol)            │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚨 Errores Comunes y Soluciones

### 1. **"JWT strings must contain exactly 2 period characters"**
**Causa:** Token vacío o malformado
**Solución:** Verifica que el header sea: `Authorization: Bearer <token_válido>`

### 2. **"401 Unauthorized" al acceder a /usuarios**
**Causa:** Token inválido o usuario sin rol ADMIN
**Solución:** 
- Verifica que el token sea válido
- Asegúrate de que el usuario tenga `rol: ADMIN`

### 3. **"El usuario ya existe"**
**Causa:** Intentas registrar un username que ya está en la BD
**Solución:** Usa otro username o elimina el usuario existente

### 4. **Contraseña no coincide**
**Causa:** La contraseña en login no es correcta
**Solución:** Verifica que estés usando la contraseña correcta

---

## 🎓 Conceptos Avanzados

### 1. **¿Por qué usar Records en DTOs?**

```java
public record AuthRequest(String username, String password) {}
```

**Ventajas:**
- ✅ Inmutable (no se pueden cambiar valores)
- ✅ Menos código (getters, equals, hashCode automáticos)
- ✅ Ideal para transferir datos

### 2. **¿Por qué `@PreAuthorize` en UsuarioController?**

```java
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController { ... }
```

Agrega una **segunda capa de seguridad** además de `SecurityConfig`. Es redundante pero más explícito.

### 3. **¿Por qué validar token en cada request?**

Porque JWT es **stateless**: el servidor no guarda sesión. Cada request debe incluir el token para demostrar identidad.

---

## ✅ Checklist de Seguridad

- ✅ Contraseñas encriptadas con BCrypt
- ✅ JWT firmado con clave secreta
- ✅ Token expira en 24 horas
- ⚠️ Clave secreta en archivo (mover a variables de entorno en producción)
- ⚠️ HTTPS en producción (evitar intercepción de tokens)
- ⚠️ Validación de entrada (agregar @Valid en requests)

---

## 📚 Recursos Adicionales

- [JWT.io](https://jwt.io/) - Decodificador de JWT
- [Spring Security Docs](https://spring.io/projects/spring-security)
- [BCrypt Calculator](https://bcrypt-generator.com/)

---

## 🎯 Resumen Final

Este microservicio proporciona:
1. ✅ **Registro de usuarios** con roles (ADMIN/USER)
2. ✅ **Login** con generación de JWT
3. ✅ **Protección de endpoints** por rol
4. ✅ **CRUD de usuarios** (solo ADMIN)
5. ✅ **Validación automática de JWT** en cada request

**Flujo típico:**
1. Usuario se registra → Recibe JWT
2. Usuario hace login → Recibe JWT
3. Usuario incluye JWT en cada request → Accede según su rol

