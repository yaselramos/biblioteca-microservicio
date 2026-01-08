# 🔧 Solución: Error "No Autorizado" en POST /prestamos

## ❌ Problema
Al ejecutar `POST http://localhost:8082/prestamos/1` recibes error **401 Unauthorized**

## ✅ Solución Implementada

He configurado Spring Security con JWT en el **prestamo-service** para que pueda validar los tokens generados por el **auth-service**.

---

## 📝 Cambios Realizados

### 1. **Archivos Nuevos Creados:**

- ✅ `SecurityConfig.java` - Configuración de seguridad
- ✅ `JwtService.java` - Servicio para validar tokens JWT
- ✅ `JwtFilter.java` - Filtro para interceptar y validar el token en cada request

### 2. **Dependencias Agregadas:**

```xml
<!-- En pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

### 3. **Configuración JWT:**

```properties
# En application.properties
jwt.secret=mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong12345678
```

⚠️ **IMPORTANTE:** El `jwt.secret` debe ser **exactamente el mismo** que el usado en `auth-service`.

---

## 🚀 Cómo Usar

### Paso 1: Recompilar el Servicio

```bash
cd prestamo-service
mvn clean package -DskipTests
```

### Paso 2: Reiniciar el Servicio

Si el servicio está corriendo, deténlo y vuelve a iniciarlo:

```bash
mvn spring-boot:run
```

### Paso 3: Obtener Token JWT

```
POST http://localhost:8080/auth/login

Body:
{
  "username": "user",
  "password": "user123"
}

Respuesta:
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIi..."
}
```

**👉 COPIA EL TOKEN**

### Paso 4: Crear Préstamo con el Token

```
POST http://localhost:8082/prestamos/1

Headers:
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIi...
```

**🎯 Ahora debería funcionar correctamente**

---

## 📋 Ejemplo Completo en Postman

### Request 1: Login
```
Method: POST
URL: http://localhost:8080/auth/login
Headers:
  Content-Type: application/json
Body (raw JSON):
{
  "username": "user",
  "password": "user123"
}
```

### Request 2: Crear Préstamo
```
Method: POST
URL: http://localhost:8082/prestamos/1
Headers:
  Authorization: Bearer {PEGA_AQUI_EL_TOKEN_DEL_PASO_1}
```

**✅ Respuesta Esperada (201 Created):**
```json
{
  "id": 1,
  "libroId": 1,
  "username": "user",
  "fechaPrestamo": "2026-01-07",
  "fechaDevolucion": null,
  "devuelto": false
}
```

---

## 🔍 Verificar que Funcionó

### 1. Verifica los logs del prestamo-service:
```
📤 Publicando evento de préstamo: PrestamoEvent{...}
✅ Evento publicado exitosamente
```

### 2. Verifica los logs del libro-service:
```
📥 Recibido evento de préstamo: PrestamoEvent{...}
✅ Stock decrementado exitosamente
```

### 3. Verifica que el stock del libro se decrementó:
```
GET http://localhost:8081/libros/1
```

El stock debe haberse reducido en 1.

---

## ⚠️ Troubleshooting

### Error: "Invalid JWT token"
**Causa:** El `jwt.secret` en `prestamo-service` es diferente al de `auth-service`

**Solución:**
1. Verifica que ambos archivos `application.properties` tengan el **mismo** `jwt.secret`
2. Reinicia ambos servicios

---

### Error: "Token expired"
**Causa:** El token JWT expiró (duración: 24 horas)

**Solución:**
1. Haz login nuevamente para obtener un token nuevo
2. Usa el nuevo token en tus requests

---

### Error: "No message available"
**Causa:** No enviaste el header `Authorization`

**Solución:**
Asegúrate de incluir:
```
Authorization: Bearer {tu_token}
```

---

### Error: Connection refused (RabbitMQ)
**Causa:** RabbitMQ no está corriendo

**Solución:**
```bash
# Iniciar RabbitMQ con Docker
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

---

## ✅ Checklist Final

- [ ] Prestamo-service recompilado (`mvn clean package -DskipTests`)
- [ ] Prestamo-service reiniciado
- [ ] Token JWT obtenido desde auth-service
- [ ] Header `Authorization: Bearer {token}` incluido en la request
- [ ] Request enviada a `POST http://localhost:8082/prestamos/{libroId}`
- [ ] Respuesta 201 Created recibida
- [ ] Evento publicado a RabbitMQ (verificar logs)
- [ ] Stock del libro decrementado (verificar con GET /libros/{id})

---

## 🎯 Flujo Completo de Prueba

```bash
# 1. Iniciar RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# 2. Iniciar servicios (en terminales separadas)
cd auth-service && mvn spring-boot:run
cd libro-service && mvn spring-boot:run
cd prestamo-service && mvn spring-boot:run
```

**En Postman:**

1. **Login:** `POST http://localhost:8080/auth/login` con user/user123
2. **Copiar token** de la respuesta
3. **Crear libro:** `POST http://localhost:8081/libros` con admin token
   ```json
   {"titulo": "El Quijote", "autor": "Cervantes", "stock": 5}
   ```
4. **Verificar stock:** `GET http://localhost:8081/libros/1` → stock = 5
5. **Crear préstamo:** `POST http://localhost:8082/prestamos/1` con user token
6. **Verificar stock:** `GET http://localhost:8081/libros/1` → stock = 4 ✅

---

## 📚 Archivos Modificados

```
prestamo-service/
├── pom.xml (agregadas dependencias JJWT)
├── src/main/resources/application.properties (agregado jwt.secret)
└── src/main/java/com/biblioteca/prestamo/
    ├── config/
    │   ├── SecurityConfig.java (NUEVO)
    │   └── JwtFilter.java (NUEVO)
    └── service/
        └── JwtService.java (NUEVO)
```

---

¡Ahora el prestamo-service está completamente configurado con autenticación JWT! 🎉

