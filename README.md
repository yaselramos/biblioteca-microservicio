# 📚 Sistema de Biblioteca - Microservicios con Spring Boot

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3-orange.svg)](https://www.rabbitmq.com/)
[![Tests](https://img.shields.io/badge/Tests-45%20passing-success.svg)](./TESTS_IMPLEMENTADOS.md)

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Arquitectura](#️-arquitectura-del-sistema)
- [Características](#-características-principales)
- [Tecnologías](#️-stack-tecnológico)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación-paso-a-paso)
- [Ejecución](#-ejecutar-los-microservicios)
- [Guía de Uso](#-guía-de-uso-completa)
- [API Reference](#-api-endpoints)
- [Testing](#-testing)
- [Documentación](#-documentación-adicional)
- [Troubleshooting](#-solución-de-problemas)

---

## 🎯 Descripción

Sistema completo de gestión de biblioteca implementado con **arquitectura de microservicios**, que permite:

- 🔐 **Autenticación segura** con JWT
- 📚 **Gestión de libros** con control de stock
- 📖 **Sistema de préstamos** con validaciones
- 🔄 **Comunicación asíncrona** entre servicios con RabbitMQ
- 🧪 **45 tests automatizados** (unitarios e integración)
- 📊 **Documentación interactiva** con Swagger UI
- 💚 **Health checks** y monitoreo con Actuator

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────────┐
│                    CLIENTE (Postman/Browser/Mobile)                  │
└─────────────────────────────────────────────────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
        ▼                           ▼                           ▼
┌──────────────────┐      ┌──────────────────┐      ┌──────────────────┐
│  AUTH-SERVICE    │      │  LIBRO-SERVICE   │      │ PRESTAMO-SERVICE │
│   Puerto: 8080   │      │   Puerto: 8081   │      │   Puerto: 8082   │
├──────────────────┤      ├──────────────────┤      ├──────────────────┤
│ • JWT Auth       │      │ • CRUD Libros    │      │ • Préstamos      │
│ • BCrypt         │      │ • Stock Management│     │ • Validaciones   │
│ • Roles (ADMIN/  │      │ • RabbitMQ       │      │ • RestTemplate   │
│   USER)          │      │   Listener       │      │ • RabbitMQ       │
│ • Usuarios CRUD  │      │ • JWT Protected  │      │   Publisher      │
│ • Swagger UI     │      │ • Swagger UI     │      │ • Swagger UI     │
│ • 22 Tests ✅    │      │ • 11 Tests ✅    │      │ • 12 Tests ✅    │
└────────┬─────────┘      └────────┬─────────┘      └────────┬─────────┘
         │                         │                         │
         │                         └──────RabbitMQ───────────┘
         │                         Cola: prestamo.queue
         │                                 │
         ▼                                 ▼
┌──────────────────┐              ┌──────────────────┐
│   PostgreSQL     │              │    RabbitMQ      │
├──────────────────┤              ├──────────────────┤
│ • auth_service   │              │ • Port: 5672     │
│ • libro_service  │              │ • UI: 15672      │
│ • prestamo_srv   │              │ • Durable Queue  │
└──────────────────┘              └──────────────────┘
```

### 🔄 Flujo de Comunicación

```
1. Usuario → POST /prestamos/1 (con JWT token)
   ↓
2. prestamo-service valida JWT y stock del libro
   ↓
3. Crea préstamo en BD prestamo_service
   ↓
4. Publica evento "PRESTAMO_CREADO" → RabbitMQ
   ↓
5. libro-service escucha cola (automático)
   ↓
6. Decrementa stock del libro (5 → 4)
   ↓
7. ✅ Respuesta 201 al cliente
```

---

## ✨ Características Principales

### 🔐 Seguridad
- ✅ JWT (JSON Web Tokens) con expiración de 24h
- ✅ BCrypt para encriptación de contraseñas
- ✅ Spring Security con roles ADMIN/USER
- ✅ Endpoints protegidos por autenticación y autorización

### 📚 Gestión de Libros
- ✅ CRUD completo de libros (solo ADMIN)
- ✅ Control de stock en tiempo real
- ✅ Actualización automática vía RabbitMQ
- ✅ Consultas públicas (GET)

### 📖 Sistema de Préstamos
- ✅ Validación de stock antes de prestar
- ✅ Control de préstamos duplicados
- ✅ Historial de préstamos por usuario
- ✅ Devoluciones con actualización automática de stock

### 🔄 Comunicación Asíncrona
- ✅ RabbitMQ para eventos entre servicios
- ✅ Cola durable (persiste en disco)
- ✅ Mensajes en formato JSON
- ✅ Resiliencia ante caídas de servicios

### ✅ Validaciones
- ✅ Bean Validation en DTOs y entidades
- ✅ Validaciones de negocio (stock, duplicados)
- ✅ Manejo global de excepciones
- ✅ Respuestas de error estructuradas

### 📊 Documentación y Monitoreo
- ✅ Swagger UI en los 3 servicios
- ✅ Health checks con Spring Boot Actuator
- ✅ Métricas y estadísticas
- ✅ Logs estructurados

### 🧪 Testing
- ✅ 45 tests automatizados (22 + 11 + 12)
- ✅ Tests unitarios con Mockito
- ✅ Tests de integración con MockMvc
- ✅ Cobertura ~85%

---

## 🛠️ Stack Tecnológico

### Backend Framework
```
Java 17
Spring Boot 3.2.1
├── Spring Web (REST APIs)
├── Spring Data JPA (Persistencia)
├── Spring Security (Autenticación/Autorización)
├── Spring AMQP (RabbitMQ)
├── Spring Boot Actuator (Monitoreo)
└── Spring Boot Validation (Validaciones)
```

### Seguridad
```
JJWT 0.11.5 (JWT Tokens)
BCrypt (Password Hashing)
```

### Base de Datos
```
PostgreSQL 15 (Producción)
H2 Database (Tests)
Hibernate (ORM)
```

### Mensajería
```
RabbitMQ 3
Spring AMQP
Jackson (JSON Serialization)
```

### Testing
```
JUnit 5 (Framework de tests)
Mockito (Mocking)
MockMvc (API Testing)
AssertJ (Assertions)
```

### Documentación
```
SpringDoc OpenAPI 2.3.0 (Swagger UI)
```

### Build Tool
```
Maven 3.9+ (Multi-módulo)
```

---

## 📋 Requisitos Previos

### Instalaciones Necesarias

| Software | Versión Mínima | Comando Verificación |
|----------|----------------|----------------------|
| **Java** | 17+ | `java -version` |
| **Maven** | 3.6+ | `mvn -version` |
| **PostgreSQL** | 15+ | `psql --version` |
| **RabbitMQ** | 3+ | `rabbitmqctl status` |

### Instalación en macOS

```bash
# Instalar Homebrew (si no está instalado)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Instalar Java 17
brew install openjdk@17

# Instalar Maven
brew install maven

# Instalar PostgreSQL
brew install postgresql@15
brew services start postgresql@15

# Instalar RabbitMQ
brew install rabbitmq
brew services start rabbitmq
```

### Instalación en Linux (Ubuntu/Debian)

```bash
# Java 17
sudo apt update
sudo apt install openjdk-17-jdk

# Maven
sudo apt install maven

# PostgreSQL
sudo apt install postgresql-15
sudo systemctl start postgresql

# RabbitMQ
sudo apt install rabbitmq-server
sudo systemctl start rabbitmq-server
```

---

## 🚀 Instalación Paso a Paso

### Paso 1️⃣: Clonar o Ubicar el Proyecto

```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios
```

---

### Paso 2️⃣: Configurar PostgreSQL

#### A) Crear las Bases de Datos

```bash
# Conectar a PostgreSQL
psql -U postgres

# Ejecutar en psql:
```

```sql
-- Crear las 3 bases de datos
CREATE DATABASE auth_service;
CREATE DATABASE libro_service;
CREATE DATABASE prestamo_service;

-- Verificar que se crearon
\l

-- Salir
\q
```

#### B) Verificar Conexión

```bash
# auth_service
psql -U postgres -d auth_service -c "SELECT version();"

# libro_service
psql -U postgres -d libro_service -c "SELECT version();"

# prestamo_service
psql -U postgres -d prestamo_service -c "SELECT version();"
```

#### C) Configurar Credenciales (si es necesario)

Si tu usuario/password de PostgreSQL es diferente de `postgres/a`, edita:

```bash
# auth-service
nano auth-service/src/main/resources/application.properties

# libro-service
nano libro-service/src/main/resources/application.properties

# prestamo-service
nano prestamo-service/src/main/resources/application.properties
```

---

### Paso 3️⃣: Configurar RabbitMQ

#### A) Verificar que RabbitMQ está corriendo

```bash
# Verificar status
rabbitmqctl status

# O acceder al panel web
open http://localhost:15672
# Usuario: guest
# Password: guest
```

#### B) La cola se crea automáticamente

La cola `prestamo.queue` se crea automáticamente al iniciar `libro-service` o `prestamo-service`.

---

### Paso 4️⃣: Compilar el Proyecto

```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios

# Compilar sin tests (más rápido)
mvn clean install -DskipTests

# O compilar con tests (recomendado)
mvn clean install
```

**Salida Esperada:**

```
[INFO] Reactor Summary:
[INFO] 
[INFO] biblioteca-parent .............................. SUCCESS [  1.234 s]
[INFO] auth-service ................................... SUCCESS [ 15.678 s]
[INFO] libro-service .................................. SUCCESS [ 12.345 s]
[INFO] prestamo-service ............................... SUCCESS [ 13.456 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  42.713 s
[INFO] Finished at: 2026-01-08T15:30:00-03:00
[INFO] ------------------------------------------------------------------------
```

---

### Paso 5️⃣: Ejecutar Tests (Opcional)

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests de un servicio específico
mvn test -pl auth-service    # 22 tests
mvn test -pl libro-service   # 11 tests
mvn test -pl prestamo-service # 12 tests
```

**Resultados Esperados:**

```
[INFO] Results:
[INFO] 
[INFO] Tests run: 45, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 🎮 Ejecutar los Microservicios

### Opción 1: Ejecución Manual (Recomendada para Desarrollo)

Abre **3 terminales** separadas:

#### 🔐 Terminal 1 - auth-service (Puerto 8080)

```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios/auth-service
mvn spring-boot:run
```

**Logs esperados:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.1)

2026-01-08T15:30:15.123-03:00  INFO 12345 --- [main] c.b.AuthServiceApplication     : Starting AuthServiceApplication
2026-01-08T15:30:18.456-03:00  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8080 (http)
2026-01-08T15:30:18.789-03:00  INFO 12345 --- [main] c.b.AuthServiceApplication     : Started AuthServiceApplication in 3.666 seconds
```

#### 📚 Terminal 2 - libro-service (Puerto 8081)

```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios/libro-service
mvn spring-boot:run
```

**Logs esperados:**
```
2026-01-08T15:31:00.123-03:00  INFO 12346 --- [main] c.b.LibroServiceApplication    : Starting LibroServiceApplication
2026-01-08T15:31:02.456-03:00  INFO 12346 --- [main] o.s.a.r.c.CachingConnectionFactory    : Created new connection: SimpleConnection@1a2b3c4d
2026-01-08T15:31:03.789-03:00  INFO 12346 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8081 (http)
2026-01-08T15:31:04.123-03:00  INFO 12346 --- [main] c.b.LibroServiceApplication    : Started LibroServiceApplication in 4.000 seconds
```

#### 📖 Terminal 3 - prestamo-service (Puerto 8082)

```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios/prestamo-service
mvn spring-boot:run
```

**Logs esperados:**
```
2026-01-08T15:32:00.123-03:00  INFO 12347 --- [main] c.b.PrestamoServiceApplication : Starting PrestamoServiceApplication
2026-01-08T15:32:02.456-03:00  INFO 12347 --- [main] o.s.a.r.c.CachingConnectionFactory    : Created new connection: SimpleConnection@5e6f7g8h
2026-01-08T15:32:03.789-03:00  INFO 12347 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8082 (http)
2026-01-08T15:32:04.123-03:00  INFO 12347 --- [main] c.b.PrestamoServiceApplication : Started PrestamoServiceApplication in 4.000 seconds
```

---

### Opción 2: Ejecución con Scripts (Modo Background)

```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios

# Dar permisos (solo primera vez)
chmod +x start-services.sh stop-services.sh
mkdir -p logs .pids

# Iniciar todos los servicios
./start-services.sh

# Ver logs
tail -f logs/auth-service.log
tail -f logs/libro-service.log
tail -f logs/prestamo-service.log

# Detener todos los servicios
./stop-services.sh
```

---

### Opción 3: Ejecutar con JAR (Producción)

```bash
# auth-service
java -jar auth-service/target/auth-service-1.0.0.jar &

# libro-service
java -jar libro-service/target/libro-service-1.0.0.jar &

# prestamo-service
java -jar prestamo-service/target/prestamo-service-1.0.0.jar &
```

---

## ✅ Verificar que Todo Funciona

### 1️⃣ Health Checks

```bash
# auth-service
curl http://localhost:8080/actuator/health | jq
# Respuesta esperada: {"status":"UP"}

# libro-service
curl http://localhost:8081/actuator/health | jq
# Respuesta esperada: {"status":"UP"}

# prestamo-service
curl http://localhost:8082/actuator/health | jq
# Respuesta esperada: {"status":"UP"}
```

### 2️⃣ Swagger UI

Abre en tu navegador:

- 🔐 **Auth Service:** http://localhost:8080/swagger-ui.html
- 📚 **Libro Service:** http://localhost:8081/swagger-ui.html
- 📖 **Prestamo Service:** http://localhost:8082/swagger-ui.html

### 3️⃣ RabbitMQ Management

- **URL:** http://localhost:15672
- **Usuario:** `guest`
- **Password:** `guest`
- **Verificar:** Que exista la cola `prestamo.queue`

---

## 📖 Guía de Uso Completa

### 🎬 Escenario: De Registro a Préstamo (9 Pasos)

#### Paso 1: Registrar Usuario ADMIN 👤

**Request:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "rol": "ADMIN"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3MDUwMDAwMCwiZXhwIjoxNjcwNTg2NDAwfQ.xyz..."
}
```

💾 **Guarda el token en una variable:**
```bash
export ADMIN_TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

---

#### Paso 2: Registrar Usuario Normal 👤

**Request:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan",
    "password": "juan123",
    "rol": "USER"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuIiwiaWF0IjoxNjcwNTAwMTAwLCJleHAiOjE2NzA1ODY1MDB9.abc..."
}
```

💾 **Guarda el token:**
```bash
export USER_TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

---

#### Paso 3: Crear un Libro 📚 (requiere ADMIN)

**Request:**
```bash
curl -X POST http://localhost:8081/libros \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "stock": 5
  }'
```

**Response:**
```json
{
  "id": 1,
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "stock": 5
}
```

---

#### Paso 4: Listar Libros 📚 (público)

**Request:**
```bash
curl http://localhost:8081/libros | jq
```

**Response:**
```json
[
  {
    "id": 1,
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "stock": 5
  }
]
```

---

#### Paso 5: Prestar un Libro 📖

**Request:**
```bash
curl -X POST http://localhost:8082/prestamos/1 \
  -H "Authorization: Bearer $USER_TOKEN"
```

**Response:**
```json
{
  "id": 1,
  "libroId": 1,
  "username": "juan",
  "fechaPrestamo": "2026-01-08",
  "fechaDevolucion": null,
  "devuelto": false
}
```

**🔄 Lo que sucede automáticamente:**

1. ✅ **prestamo-service** valida que el libro exista y tenga stock
2. ✅ Crea el préstamo en la BD `prestamo_service`
3. ✅ Publica evento `PRESTAMO_CREADO` a RabbitMQ
4. ✅ **libro-service** escucha el evento automáticamente
5. ✅ Decrementa el stock del libro: **5 → 4**

**Ver en logs:**

```
# prestamo-service log:
📤 Publicando evento: PrestamoEvent{libroId=1, eventType=PRESTAMO_CREADO}
✅ Evento publicado exitosamente

# libro-service log:
📥 Recibido evento: PrestamoEvent{libroId=1, eventType=PRESTAMO_CREADO}
📖 Decrementando stock del libro 1
✅ Stock actualizado: 5 → 4
```

---

#### Paso 6: Verificar Stock Actualizado ✅

**Request:**
```bash
curl http://localhost:8081/libros/1 | jq
```

**Response:**
```json
{
  "id": 1,
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "stock": 4
}
```

✨ **¡Stock decrementado automáticamente!**

---

#### Paso 7: Ver Mis Préstamos 📋

**Request:**
```bash
curl http://localhost:8082/prestamos \
  -H "Authorization: Bearer $USER_TOKEN" | jq
```

**Response:**
```json
[
  {
    "id": 1,
    "libroId": 1,
    "username": "juan",
    "fechaPrestamo": "2026-01-08",
    "fechaDevolucion": null,
    "devuelto": false
  }
]
```

---

#### Paso 8: Devolver el Libro 🔄

**Request:**
```bash
curl -X PUT http://localhost:8082/prestamos/1/devolver \
  -H "Authorization: Bearer $USER_TOKEN" | jq
```

**Response:**
```json
{
  "id": 1,
  "libroId": 1,
  "username": "juan",
  "fechaPrestamo": "2026-01-08",
  "fechaDevolucion": "2026-01-08",
  "devuelto": true
}
```

**🔄 Lo que sucede automáticamente:**

1. ✅ Marca el préstamo como devuelto
2. ✅ Publica evento `PRESTAMO_DEVUELTO` a RabbitMQ
3. ✅ **libro-service** incrementa el stock: **4 → 5**

---

#### Paso 9: Verificar Stock Restaurado ✅

**Request:**
```bash
curl http://localhost:8081/libros/1 | jq
```

**Response:**
```json
{
  "id": 1,
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "stock": 5
}
```

✨ **¡Stock restaurado automáticamente!**

---

## 📡 API Endpoints

### 🔐 auth-service (http://localhost:8080)

#### Autenticación (Público)

| Método | Endpoint | Descripción | Body |
|--------|----------|-------------|------|
| POST | `/auth/register` | Registrar usuario | `{username, password, rol}` |
| POST | `/auth/login` | Iniciar sesión | `{username, password}` |

#### Gestión de Usuarios (ADMIN)

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/usuarios` | ✅ ADMIN | Crear usuario |
| GET | `/usuarios` | ✅ ADMIN | Listar usuarios |
| GET | `/usuarios/{id}` | ✅ ADMIN | Buscar usuario |
| PUT | `/usuarios/{id}` | ✅ ADMIN | Actualizar usuario |
| DELETE | `/usuarios/{id}` | ✅ ADMIN | Eliminar usuario |

---

### 📚 libro-service (http://localhost:8081)

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| GET | `/libros` | ❌ | Listar todos los libros |
| GET | `/libros/{id}` | ❌ | Buscar libro por ID |
| POST | `/libros` | ✅ ADMIN | Crear libro |
| PUT | `/libros/{id}` | ✅ ADMIN | Actualizar libro |
| DELETE | `/libros/{id}` | ✅ ADMIN | Eliminar libro |

---

### 📖 prestamo-service (http://localhost:8082)

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/prestamos/{libroId}` | ✅ USER | Prestar libro |
| PUT | `/prestamos/{id}/devolver` | ✅ USER | Devolver libro |
| GET | `/prestamos` | ✅ USER | Mis préstamos |
| GET | `/prestamos/activos` | ✅ USER | Mis préstamos activos |
| GET | `/prestamos/todos` | ✅ USER | Todos los préstamos |
| GET | `/prestamos/{id}` | ✅ USER | Buscar préstamo |

---

### 💚 Actuator (Todos los servicios)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/actuator/health` | Estado del servicio |
| GET | `/actuator/info` | Información del servicio |
| GET | `/actuator/metrics` | Métricas del servicio |

---

### 📄 Swagger UI (Todos los servicios)

| Servicio | URL |
|----------|-----|
| auth-service | http://localhost:8080/swagger-ui.html |
| libro-service | http://localhost:8081/swagger-ui.html |
| prestamo-service | http://localhost:8082/swagger-ui.html |

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests (45 tests)
mvn test

# Tests de un servicio específico
mvn test -pl auth-service      # 22 tests
mvn test -pl libro-service     # 11 tests
mvn test -pl prestamo-service  # 12 tests

# Tests con reporte detallado
mvn test -Dsurefire.printSummary=true

# Test específico
mvn test -Dtest=UsuarioServiceTest -pl auth-service
```

### Resultados de Tests

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.biblioteca.auth.service.UsuarioServiceTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.532 s
[INFO] Running com.biblioteca.auth.service.JwtServiceTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.245 s
[INFO] Running com.biblioteca.auth.controller.AuthControllerIntegrationTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.134 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Cobertura de Tests

| Servicio | Suite de Tests | Tests | Tipo |
|----------|----------------|-------|------|
| **auth-service** | `UsuarioServiceTest` | 8 | Unitarios |
| | `JwtServiceTest` | 6 | Unitarios |
| | `AuthControllerIntegrationTest` | 8 | Integración |
| **libro-service** | `LibroServiceTest` | 11 | Unitarios |
| **prestamo-service** | `PrestamoServiceTest` | 12 | Unitarios |
| **TOTAL** | | **45** | |

**Cobertura Estimada:** ~85%

Ver reportes:
```bash
# Ver reportes de tests
ls -la auth-service/target/surefire-reports/
ls -la libro-service/target/surefire-reports/
ls -la prestamo-service/target/surefire-reports/
```

---

## 📚 Documentación Adicional

El proyecto incluye documentación detallada en archivos markdown:

| Archivo | Descripción |
|---------|-------------|
| **[MEJORAS_IMPLEMENTADAS.md](MEJORAS_IMPLEMENTADAS.md)** | Detalle de las 5 mejoras prioritarias implementadas |
| **[TESTS_IMPLEMENTADOS.md](TESTS_IMPLEMENTADOS.md)** | Documentación completa de los 45 tests |
| **[FLUJO_MICROSERVICIOS_RABBITMQ.md](FLUJO_MICROSERVICIOS_RABBITMQ.md)** | Explicación paso a paso del flujo entre microservicios |
| **[RABBITMQ_GUIA_COMPLETA.md](RABBITMQ_GUIA_COMPLETA.md)** | Guía detallada de RabbitMQ y eventos |
| **[PROPUESTAS_MEJORA.md](PROPUESTAS_MEJORA.md)** | Roadmap de 15 mejoras futuras |
| **[RESUMEN_EJECUTIVO_MEJORAS.md](RESUMEN_EJECUTIVO_MEJORAS.md)** | Quick reference de mejoras |
| **[auth-service/EXPLICACION_AUTH_SERVICE.md](auth-service/EXPLICACION_AUTH_SERVICE.md)** | Explicación detallada del servicio de autenticación |

---

## 🐛 Solución de Problemas

### ❌ Puerto en uso (8080/8081/8082)

**Error:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solución:**
```bash
# Encontrar el proceso
lsof -i :8080

# Matar el proceso
kill -9 <PID>

# O cambiar el puerto en application.properties
echo "server.port=8083" >> auth-service/src/main/resources/application.properties
```

---

### ❌ PostgreSQL no conecta

**Error:**
```
Connection to localhost:5432 refused
```

**Solución:**
```bash
# Verificar que PostgreSQL está corriendo
brew services list | grep postgresql

# Iniciar PostgreSQL
brew services start postgresql@15

# Verificar conexión
psql -U postgres -c "SELECT version();"

# Crear las bases de datos si no existen
psql -U postgres <<EOF
CREATE DATABASE auth_service;
CREATE DATABASE libro_service;
CREATE DATABASE prestamo_service;
EOF
```

---

### ❌ RabbitMQ no conecta

**Error:**
```
Connection refused: Connection refused
```

**Solución:**
```bash
# Verificar que RabbitMQ está corriendo
brew services list | grep rabbitmq

# Iniciar RabbitMQ
brew services start rabbitmq

# Verificar en el navegador
open http://localhost:15672

# Ver logs
tail -f /usr/local/var/log/rabbitmq/rabbit@*.log
```

---

### ❌ Stock no se actualiza

**Diagnóstico:**

1. **Verificar logs de prestamo-service:**
   ```bash
   tail -f logs/prestamo-service.log | grep "Publicando evento"
   ```
   ¿Ves `📤 Publicando evento...`?

2. **Verificar logs de libro-service:**
   ```bash
   tail -f logs/libro-service.log | grep "Recibido evento"
   ```
   ¿Ves `📥 Recibido evento...`?

3. **Verificar RabbitMQ:**
   - Accede a http://localhost:15672
   - Ve a **Queues** → `prestamo.queue`
   - ¿Hay mensajes en "Ready"?

**Soluciones:**

```bash
# Reiniciar libro-service
cd libro-service && mvn spring-boot:run

# Verificar configuración RabbitMQ
grep "spring.rabbitmq" */src/main/resources/application.properties

# Purgar la cola si hay mensajes atascados
rabbitmqadmin purge queue name=prestamo.queue
```

---

### ❌ JWT Token inválido

**Error:**
```json
{
  "timestamp": "2026-01-08T15:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT strings must contain exactly 2 period characters"
}
```

**Solución:**

1. **Verifica el formato del header:**
   ```bash
   # Correcto ✅
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWI...
   
   # Incorrecto ❌
   Authorization: eyJhbGciOiJIUzI1NiJ9...  # Falta "Bearer "
   Authorization: Bearer                    # Falta el token
   ```

2. **Verifica que el secreto JWT sea el mismo:**
   ```bash
   grep "jwt.secret" auth-service/src/main/resources/application.properties
   grep "jwt.secret" prestamo-service/src/main/resources/application.properties
   # Deben ser idénticos
   ```

3. **Genera un nuevo token:**
   ```bash
   curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"juan","password":"juan123"}'
   ```

---

### ❌ Tests fallan

**Error:**
```
Tests run: 22, Failures: 2, Errors: 1
```

**Solución:**

```bash
# Limpiar y recompilar
mvn clean install -DskipTests

# Ejecutar tests con más detalles
mvn test -X

# Verificar H2 en tests
cat auth-service/src/test/resources/application.properties

# Ejecutar test individual para ver el error
mvn test -Dtest=UsuarioServiceTest -pl auth-service
```

---

### ❌ Validaciones no funcionan

**Error:** Los campos vacíos no generan error 400

**Solución:**

1. **Verificar dependencia:**
   ```bash
   grep "spring-boot-starter-validation" auth-service/pom.xml
   ```

2. **Verificar anotación @Valid:**
   ```bash
   grep "@Valid" auth-service/src/main/java/com/biblioteca/auth/controller/AuthController.java
   ```

3. **Recompilar:**
   ```bash
   mvn clean install -DskipTests
   ```

---

## 📊 Monitoreo y Métricas

### Health Checks

```bash
# Script para verificar todos los servicios
cat << 'EOF' > check-health.sh
#!/bin/bash
echo "🔍 Verificando servicios..."
echo ""

echo "🔐 auth-service:"
curl -s http://localhost:8080/actuator/health | jq -r '.status'

echo "📚 libro-service:"
curl -s http://localhost:8081/actuator/health | jq -r '.status'

echo "📖 prestamo-service:"
curl -s http://localhost:8082/actuator/health | jq -r '.status'

echo ""
echo "✅ Todos los servicios verificados"
EOF

chmod +x check-health.sh
./check-health.sh
```

### Métricas

```bash
# Ver métricas de memoria
curl http://localhost:8080/actuator/metrics/jvm.memory.used | jq

# Ver métricas de HTTP requests
curl http://localhost:8080/actuator/metrics/http.server.requests | jq

# Ver todas las métricas disponibles
curl http://localhost:8080/actuator/metrics | jq '.names'
```

---

## 🔐 Seguridad

### JWT (JSON Web Tokens)

- **Algoritmo:** HMAC-SHA256
- **Expiración:** 24 horas
- **Secreto:** `mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong12345678`
- **Formato:** `Bearer <token>`

### BCrypt

- **Factor de costo:** 10
- **Irreversible:** No se puede desencriptar

### Roles y Permisos

| Rol | Permisos |
|-----|----------|
| **ADMIN** | • CRUD completo de usuarios<br>• CRUD completo de libros<br>• Ver todos los préstamos |
| **USER** | • Ver libros<br>• Crear préstamos propios<br>• Ver préstamos propios<br>• Devolver préstamos propios |

---

## 📂 Estructura del Proyecto

```
biblioteca-microservicios/
│
├── README.md                           # ← Guía principal
├── pom.xml                             # POM padre (multi-módulo)
├── docker-compose.yml                  # Infraestructura Docker
├── start-services.sh                   # Script para iniciar servicios
├── stop-services.sh                    # Script para detener servicios
├── MEJORAS_IMPLEMENTADAS.md            # Documentación de mejoras
├── TESTS_IMPLEMENTADOS.md              # Documentación de tests
├── FLUJO_MICROSERVICIOS_RABBITMQ.md    # Explicación del flujo
├── RABBITMQ_GUIA_COMPLETA.md           # Guía de RabbitMQ
├── PROPUESTAS_MEJORA.md                # Roadmap futuro
└── RESUMEN_EJECUTIVO_MEJORAS.md        # Quick reference
│
├── auth-service/                       # 🔐 Microservicio de Autenticación
│   ├── pom.xml
│   ├── EXPLICACION_AUTH_SERVICE.md
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/biblioteca/auth/
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   └── UsuarioController.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── AuthRequest.java
│   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   └── ErrorResponse.java
│   │   │   │   ├── entity/
│   │   │   │   │   └── Usuario.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── UsuarioRepository.java
│   │   │   │   ├── security/
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   └── JwtFilter.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── JwtService.java
│   │   │   │   │   ├── UsuarioService.java
│   │   │   │   │   └── Rol.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   └── ResourceNotFoundException.java
│   │   │   │   ├── config/
│   │   │   │   │   └── SwaggerConfig.java
│   │   │   │   └── AuthServiceApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       ├── java/com/biblioteca/auth/
│   │       │   ├── service/
│   │       │   │   ├── UsuarioServiceTest.java (8 tests)
│   │       │   │   └── JwtServiceTest.java (6 tests)
│   │       │   └── controller/
│   │       │       └── AuthControllerIntegrationTest.java (8 tests)
│   │       └── resources/
│   │           └── application.properties
│   └── target/
│       ├── auth-service-1.0.0.jar
│       └── surefire-reports/          # Reportes de tests
│
├── libro-service/                     # 📚 Microservicio de Libros
│   ├── pom.xml
│   ├── QUICKSTART_POSTMAN.md
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/biblioteca/libro/
│   │   │   │   ├── controller/
│   │   │   │   │   └── LibroController.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── PrestamoEvent.java
│   │   │   │   │   └── ErrorResponse.java
│   │   │   │   ├── entity/
│   │   │   │   │   └── Libro.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── LibroRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── LibroService.java
│   │   │   │   │   └── JwtService.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── RabbitMQConfig.java
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── JwtFilter.java
│   │   │   │   │   └── SwaggerConfig.java
│   │   │   │   ├── messaging/
│   │   │   │   │   └── PrestamoEventListener.java (Consumer)
│   │   │   │   ├── exception/
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   └── ResourceNotFoundException.java
│   │   │   │   └── LibroServiceApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       ├── java/com/biblioteca/libro/
│   │       │   └── service/
│   │       │       └── LibroServiceTest.java (11 tests)
│   │       └── resources/
│   │           └── application.properties
│   └── target/
│       └── surefire-reports/
│
└── prestamo-service/                  # 📖 Microservicio de Préstamos
    ├── pom.xml
    ├── SOLUCION_AUTORIZACION.md
    ├── src/
    │   ├── main/
    │   │   ├── java/com/biblioteca/prestamo/
    │   │   │   ├── controller/
    │   │   │   │   └── PrestamoController.java
    │   │   │   ├── dto/
    │   │   │   │   ├── PrestamoEvent.java
    │   │   │   │   ├── LibroDTO.java
    │   │   │   │   └── ErrorResponse.java
    │   │   │   ├── entity/
    │   │   │   │   └── Prestamo.java
    │   │   │   ├── repository/
    │   │   │   │   └── PrestamoRepository.java
    │   │   │   ├── service/
    │   │   │   │   ├── PrestamoService.java
    │   │   │   │   └── JwtService.java
    │   │   │   ├── config/
    │   │   │   │   ├── RabbitMQConfig.java
    │   │   │   │   ├── SecurityConfig.java
    │   │   │   │   ├── JwtFilter.java
    │   │   │   │   ├── RestTemplateConfig.java
    │   │   │   │   └── SwaggerConfig.java
    │   │   │   ├── messaging/
    │   │   │   │   └── PrestamoEventPublisher.java (Producer)
    │   │   │   ├── exception/
    │   │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   │   └── ResourceNotFoundException.java
    │   │   │   └── PrestamoServiceApplication.java
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/
    │       ├── java/com/biblioteca/prestamo/
    │       │   └── service/
    │       │       └── PrestamoServiceTest.java (12 tests)
    │       └── resources/
    │           └── application.properties
    └── target/
        ├── prestamo-service-1.0.0.jar
        └── surefire-reports/
```

---

## 🚧 Roadmap y Próximos Pasos

### ✅ Completado (v1.0.0)

- [x] Arquitectura de microservicios
- [x] Autenticación con JWT
- [x] Control de acceso por roles
- [x] CRUD completo de usuarios, libros y préstamos
- [x] Comunicación asíncrona con RabbitMQ
- [x] Validaciones de datos con Bean Validation
- [x] Manejo global de excepciones
- [x] Documentación con Swagger UI
- [x] Health checks con Actuator
- [x] 45 tests automatizados (85% coverage)

### 🔜 Próximas Versiones

#### v1.1.0 - Configuración
- [ ] Profiles de Spring (dev/prod/test)
- [ ] Configuración externalizada
- [ ] Variables de entorno
- [ ] Dead Letter Queue en RabbitMQ
- [ ] Retry logic

#### v1.2.0 - Infraestructura
- [ ] API Gateway (Spring Cloud Gateway)
- [ ] Service Discovery (Eureka)
- [ ] Config Server
- [ ] Circuit Breaker (Resilience4j)

#### v1.3.0 - Observabilidad
- [ ] Logs centralizados (ELK Stack)
- [ ] Distributed Tracing (Zipkin/Jaeger)
- [ ] Métricas con Prometheus
- [ ] Dashboards con Grafana

#### v2.0.0 - Producción
- [ ] Docker Compose completo
- [ ] Kubernetes deployment
- [ ] CI/CD Pipeline
- [ ] Rate Limiting
- [ ] HTTPS/TLS
- [ ] Caché con Redis

---

## 👥 Contribuir

### Cómo Contribuir

1. **Fork** el repositorio
2. **Crea una rama** para tu feature
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. **Commit** tus cambios
   ```bash
   git commit -am 'Add: nueva funcionalidad'
   ```
4. **Push** a la rama
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
5. **Abre un Pull Request**

### Estándares de Código

- **Java:** Seguir convenciones de Java (camelCase, PascalCase)
- **Tests:** Mantener cobertura > 80%
- **Commits:** Usar conventional commits (`feat:`, `fix:`, `docs:`)
- **Documentación:** Actualizar README y docs relacionados

---

## ✅ Checklist de Inicio Rápido

- [ ] Java 17 instalado (`java -version`)
- [ ] Maven 3.6+ instalado (`mvn -version`)
- [ ] PostgreSQL corriendo (`psql --version`)
- [ ] RabbitMQ corriendo (`rabbitmqctl status`)
- [ ] Bases de datos creadas (auth_service, libro_service, prestamo_service)
- [ ] Proyecto compilado (`mvn clean install`)
- [ ] Tests pasando (`mvn test` → 45 tests)
- [ ] auth-service corriendo (http://localhost:8080/actuator/health)
- [ ] libro-service corriendo (http://localhost:8081/actuator/health)
- [ ] prestamo-service corriendo (http://localhost:8082/actuator/health)
- [ ] Swagger UI accesible en los 3 servicios
- [ ] RabbitMQ UI accesible (http://localhost:15672)
- [ ] Cola `prestamo.queue` creada en RabbitMQ

---

## 📞 Soporte y Contacto

### Problemas Conocidos

Consulta la sección [Solución de Problemas](#-solución-de-problemas) para errores comunes.

### Reportar Bugs

1. Verifica que el bug no esté ya reportado
2. Incluye:
   - Versión de Java, Maven, PostgreSQL, RabbitMQ
   - Sistema operativo
   - Logs relevantes
   - Pasos para reproducir

### Documentación

- **Documentación Técnica:** Ver archivos `.md` en el proyecto
- **Swagger UI:** Disponible en cada servicio
- **Javadocs:** Generados con `mvn javadoc:javadoc`

---

## 📄 Licencia

Este proyecto es de uso **educativo** y de demostración.

---

## 🎓 Conceptos Aprendidos

Al completar este proyecto, habrás aprendido:

✅ **Arquitectura de Microservicios**
- Separación de responsabilidades
- Comunicación entre servicios
- Base de datos por servicio

✅ **Seguridad en Spring**
- JWT Authentication
- Spring Security
- BCrypt password hashing
- Role-based access control

✅ **Mensajería Asíncrona**
- RabbitMQ message broker
- Producer/Consumer pattern
- Event-driven architecture

✅ **Testing Profesional**
- Tests unitarios con JUnit 5 y Mockito
- Tests de integración con MockMvc
- Test coverage y reportes

✅ **Best Practices**
- Bean Validation
- Global Exception Handling
- API Documentation con Swagger
- Health Checks y Monitoring

---

## 🎉 ¡Proyecto Completado!

Este sistema de microservicios está **production-ready** (nivel básico) con:

- ✅ **45 tests** automatizados
- ✅ **Arquitectura escalable**
- ✅ **Seguridad robusta**
- ✅ **Documentación completa**
- ✅ **Comunicación asíncrona**
- ✅ **Validaciones completas**

**Próximos pasos sugeridos:**
1. Probar el flujo completo con Postman
2. Explorar Swagger UI
3. Ver el flujo de eventos en RabbitMQ
4. Revisar los logs de los servicios
5. Experimentar con los tests

---

**Desarrollado con ❤️ usando Spring Boot**  
**Versión:** 1.0.0  
**Fecha:** Enero 2026  
**Tests:** 45 passing ✅  
**Cobertura:** ~85%

---

**¿Preguntas?** Consulta la [documentación adicional](#-documentación-adicional) o los archivos `.md` del proyecto.

**¡Gracias por usar este sistema!** 🚀

