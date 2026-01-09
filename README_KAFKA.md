# 📚 Sistema de Biblioteca - Microservicios con Apache Kafka

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.5.0-orange.svg)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Java](https://img.shields.io/badge/Java-17+-red.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> 🚀 Sistema de gestión de biblioteca implementado con arquitectura de microservicios, comunicación asíncrona mediante **Apache Kafka**, autenticación JWT y mejores prácticas de Spring Boot.

---

## 🎯 Características Principales

- ✅ **Arquitectura de Microservicios** independientes y escalables
- ✅ **Autenticación y Autorización** con JWT y roles (ADMIN/USER)
- ✅ **Comunicación Asíncrona** con Apache Kafka para eventos entre servicios
- ✅ **API REST** documentada con Swagger/OpenAPI
- ✅ **Persistencia** con PostgreSQL para cada microservicio
- ✅ **Tests Unitarios** con JUnit 5 y Mockito
- ✅ **Monitoreo** con Spring Boot Actuator
- ✅ **Contenerización** con Docker Compose

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│  AUTH-SERVICE   │         │  LIBRO-SERVICE  │         │ PRESTAMO-SERVICE│
│  (Puerto 8080)  │         │  (Puerto 8081)  │         │  (Puerto 8082)  │
│                 │         │                 │         │                 │
│ • Login/Register│         │ • CRUD Libros   │         │ • CRUD Préstamos│
│ • JWT Generator │         │ • Stock Manager │         │ • Validaciones  │
│ • Roles (ADMIN/ │         │ • Kafka Consumer│         │ • Kafka Producer│
│   USER)         │         │                 │         │                 │
└────────┬────────┘         └────────┬────────┘         └────────┬────────┘
         │                           │                           │
         │                           │                           │
         ├───────────────────────────┴───────────────────────────┤
         │               PostgreSQL (3 bases de datos)           │
         └───────────────────────────────────────────────────────┘
                                     │
                    ┌────────────────┴────────────────┐
                    │     Apache Kafka (Topic)        │
                    │     prestamo.topic              │
                    │  • PRESTAMO_CREADO              │
                    │  • PRESTAMO_DEVUELTO            │
                    └─────────────────────────────────┘
```

### Flujo de Eventos con Kafka

1. **Usuario crea un préstamo** → `POST /prestamos`
2. **prestamo-service** guarda en BD y publica evento → `PRESTAMO_CREADO` al topic `prestamo.topic`
3. **libro-service** consume el evento → Decrementa stock del libro
4. **Usuario devuelve libro** → `PUT /prestamos/{id}/devolver`
5. **prestamo-service** publica evento → `PRESTAMO_DEVUELTO` al topic
6. **libro-service** consume el evento → Incrementa stock del libro

---

## 📦 Microservicios

### 1️⃣ auth-service (Puerto 8080)
- Registro de usuarios
- Login con generación de JWT
- Gestión de roles (ADMIN, USER)

### 2️⃣ libro-service (Puerto 8081)
- CRUD completo de libros
- Control de stock
- **Kafka Consumer**: escucha eventos de préstamos

### 3️⃣ prestamo-service (Puerto 8082)
- CRUD de préstamos
- Validación de disponibilidad de libros
- **Kafka Producer**: publica eventos de préstamos

---

## 🚀 Inicio Rápido

### Prerequisitos

- **Java 17+** ([Descargar](https://adoptium.net/))
- **Maven 3.8+** ([Descargar](https://maven.apache.org/download.cgi))
- **Docker & Docker Compose** ([Descargar](https://www.docker.com/products/docker-desktop))

### Opción 1: Script Automático (Recomendado) ⚡

```bash
# Clonar el repositorio
git clone <tu-repo>
cd biblioteca-microservicios

# Dar permisos de ejecución
chmod +x run-all.sh

# Ejecutar todo (infraestructura + compilación + tests)
./run-all.sh
```

Este script automáticamente:
- ✅ Verifica Docker, Maven y Java
- ✅ Compila el proyecto
- ✅ Levanta PostgreSQL + Kafka + Zookeeper
- ✅ Ejecuta tests unitarios
- ✅ Muestra resumen y comandos útiles

### Opción 2: Paso a Paso 🔧

#### 1. Levantar Infraestructura

```bash
# Levantar PostgreSQL, Kafka, Zookeeper y Kafka UI
docker-compose up -d

# Verificar que todo está corriendo
docker-compose ps

# Ver logs (opcional)
docker-compose logs -f kafka
```

**Servicios disponibles:**
- PostgreSQL auth-service: `localhost:5433`
- PostgreSQL libro-service: `localhost:5432`
- PostgreSQL prestamo-service: `localhost:5434`
- Kafka Broker: `localhost:9092`
- Kafka UI: http://localhost:8090

#### 2. Compilar Proyecto

```bash
# Compilar todos los módulos
mvn clean package -DskipTests

# O con tests
mvn clean package
```

#### 3. Ejecutar Microservicios

**Terminal 1 - auth-service:**
```bash
java -jar auth-service/target/auth-service-1.0.0.jar
```

**Terminal 2 - libro-service:**
```bash
java -jar libro-service/target/libro-service-1.0.0.jar
```

**Terminal 3 - prestamo-service:**
```bash
java -jar prestamo-service/target/prestamo-service-1.0.0.jar
```

---

## 🧪 Pruebas

### Ejecutar Tests Unitarios

```bash
# Todos los tests
mvn test

# Solo un módulo
mvn -pl prestamo-service test

# Test específico
mvn -pl prestamo-service -Dtest=PrestamoServiceTest test
```

### Prueba de Flujo Completo con Kafka

```bash
# Dar permisos de ejecución
chmod +x test-kafka.sh

# Ejecutar (asegúrate de que los servicios estén corriendo)
./test-kafka.sh
```

Este script:
1. Obtiene un token JWT
2. Crea un libro con stock inicial
3. Crea un préstamo (publica evento a Kafka)
4. Verifica que el stock se decrementó automáticamente

---

## 📖 API Endpoints

### auth-service (Puerto 8080)

#### Registro de Usuario
```bash
POST /auth/register
Content-Type: application/json

{
  "username": "usuario1",
  "password": "password123",
  "email": "usuario1@example.com",
  "rol": "USER"
}
```

#### Login
```bash
POST /auth/login
Content-Type: application/json

{
  "username": "usuario1",
  "password": "password123"
}

# Respuesta
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "usuario1",
  "rol": "USER"
}
```

### libro-service (Puerto 8081)

#### Crear Libro (ADMIN)
```bash
POST /libros
Authorization: Bearer <token>
Content-Type: application/json

{
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "isbn": "978-0307474728",
  "stock": 5
}
```

#### Listar Libros
```bash
GET /libros
Authorization: Bearer <token>
```

#### Buscar Libro por ID
```bash
GET /libros/{id}
Authorization: Bearer <token>
```

#### Actualizar Libro (ADMIN)
```bash
PUT /libros/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "titulo": "Cien años de soledad - Edición Especial",
  "autor": "Gabriel García Márquez",
  "isbn": "978-0307474728",
  "stock": 10
}
```

#### Eliminar Libro (ADMIN)
```bash
DELETE /libros/{id}
Authorization: Bearer <token>
```

### prestamo-service (Puerto 8082)

#### Crear Préstamo
```bash
POST /prestamos
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "usuario1",
  "libroId": 1
}
```

#### Listar Préstamos del Usuario
```bash
GET /prestamos/usuario/{username}
Authorization: Bearer <token>
```

#### Devolver Libro
```bash
PUT /prestamos/{id}/devolver
Authorization: Bearer <token>
```

#### Listar Todos los Préstamos (ADMIN)
```bash
GET /prestamos
Authorization: Bearer <token>
```

---

## 🔍 Monitoreo y Debugging

### Kafka UI
Interfaz web para visualizar topics, mensajes y consumer groups:
```
http://localhost:8090
```

### Swagger UI
Documentación interactiva de APIs:
- **libro-service**: http://localhost:8081/swagger-ui.html
- **prestamo-service**: http://localhost:8082/swagger-ui.html
- **auth-service**: http://localhost:8080/swagger-ui.html

### Actuator Endpoints
Monitoreo de health y métricas:
- http://localhost:8081/actuator/health
- http://localhost:8082/actuator/health

### Comandos Kafka Útiles

```bash
# Listar topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Ver mensajes del topic prestamo.topic
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic prestamo.topic \
  --from-beginning

# Describir consumer group
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group libro-group
```

---

## 🛠️ Tecnologías Utilizadas

| Categoría | Tecnología |
|-----------|-----------|
| **Framework** | Spring Boot 3.2.1 |
| **Lenguaje** | Java 17+ |
| **Build Tool** | Maven 3.8+ |
| **Bases de Datos** | PostgreSQL 15 |
| **Messaging** | Apache Kafka 3.5.0 |
| **Seguridad** | Spring Security + JWT |
| **API Docs** | Swagger/OpenAPI 3.0 |
| **Testing** | JUnit 5, Mockito |
| **Containerización** | Docker, Docker Compose |
| **Monitoreo** | Spring Boot Actuator |

---

## 📁 Estructura del Proyecto

```
biblioteca-microservicios/
├── auth-service/              # Microservicio de autenticación
│   ├── src/
│   │   ├── main/java/com/biblioteca/auth/
│   │   │   ├── controller/    # REST Controllers
│   │   │   ├── service/       # Business Logic
│   │   │   ├── repository/    # JPA Repositories
│   │   │   ├── entity/        # JPA Entities
│   │   │   ├── dto/           # Data Transfer Objects
│   │   │   ├── security/      # JWT, Security Config
│   │   │   └── exception/     # Exception Handlers
│   │   └── resources/
│   │       └── application.properties
│   └── pom.xml
│
├── libro-service/             # Microservicio de libros
│   ├── src/
│   │   ├── main/java/com/biblioteca/libro/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── config/        # Kafka Config
│   │   │   ├── messaging/     # Kafka Listener
│   │   │   └── exception/
│   │   └── resources/
│   │       └── application.properties
│   └── pom.xml
│
├── prestamo-service/          # Microservicio de préstamos
│   ├── src/
│   │   ├── main/java/com/biblioteca/prestamo/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── config/        # Kafka Config
│   │   │   ├── messaging/     # Kafka Producer
│   │   │   └── exception/
│   │   └── resources/
│   │       └── application.properties
│   └── pom.xml
│
├── docker-compose.yml         # Infraestructura completa
├── pom.xml                    # POM padre
├── run-all.sh                 # Script de ejecución automática
├── test-kafka.sh              # Script de prueba de Kafka
├── MIGRACION_KAFKA.md         # Documentación de migración
└── README.md                  # Este archivo
```

---

## 🔧 Configuración

### Variables de Entorno (Opcional)

Puedes sobreescribir configuraciones usando variables de entorno:

```bash
# Kafka
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# PostgreSQL
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=libro_service
export DB_USER=postgres
export DB_PASSWORD=a

# JWT
export JWT_SECRET=tu-secret-key-aqui
```

---

## 🐛 Troubleshooting

### Problema: Kafka no arranca
```bash
# Reiniciar servicios
docker-compose restart zookeeper kafka

# Ver logs
docker-compose logs -f kafka
```

### Problema: Puerto ya en uso
```bash
# Verificar puertos en uso
lsof -i :8080
lsof -i :8081
lsof -i :8082
lsof -i :9092

# Matar proceso
kill -9 <PID>
```

### Problema: Tests fallan con Java 24
Ya configurado en `pom.xml` padre con:
```xml
<argLine>-Dnet.bytebuddy.experimental=true</argLine>
```

Alternativa: usar JDK 17 o 21.

---

## 📚 Documentación Adicional

- [MIGRACION_KAFKA.md](MIGRACION_KAFKA.md) - Detalles de la migración de RabbitMQ a Kafka
- [TESTS_IMPLEMENTADOS.md](TESTS_IMPLEMENTADOS.md) - Cobertura de tests
- [MEJORAS_IMPLEMENTADAS.md](MEJORAS_IMPLEMENTADAS.md) - Mejoras y optimizaciones

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Añade nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

---

## 👨‍💻 Autor

Desarrollado con ❤️ usando Spring Boot y Apache Kafka

---

## 🎉 ¡Listo para Usar!

```bash
# Inicio rápido en 3 pasos
chmod +x run-all.sh
./run-all.sh

# Luego ejecuta los microservicios y empieza a probar!
```

**Documentación de API en vivo:** http://localhost:8081/swagger-ui.html

**Monitoreo de Kafka:** http://localhost:8090

---

*Última actualización: 9 de enero de 2026*

