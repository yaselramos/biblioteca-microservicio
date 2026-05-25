# ✅ SOLUCIÓN FINAL - Problema de Autenticación en Préstamos

## 📌 Configuración de Puertos (CORRECTA)

| Servicio | Puerto | Frontend | Estado |
|----------|--------|----------|--------|
| auth-service | 8080 | `localhost:8080` | ✅ Correcto |
| libro-service | 8081 | `localhost:8081` | ✅ Correcto |
| prestamo-service | 8082 | `localhost:8082` | ✅ Correcto |

**Los puertos están bien configurados. NO hay que cambiar nada en el frontend.**

## 🔍 Problema Real Identificado

El problema es que **el token JWT no incluía el rol del usuario**. Cuando intentas crear un préstamo:

1. El frontend envía el token JWT al prestamo-service
2. El JwtFilter extrae el username pero NO el rol
3. El sistema asigna un rol hardcodeado en lugar del rol real del usuario
4. La autenticación falla o no se reconoce correctamente

## ✅ Soluciones Implementadas en el Backend

He modificado los siguientes archivos:

### 1. auth-service - Generar token con rol

**Archivo**: `auth-service/src/main/java/.../auth/service/JwtService.java`

```java
public String generarToken(String username, String rol) {
    return Jwts.builder()
            .setSubject(username)
            .claim("rol", rol)  // ← ROL INCLUIDO EN EL TOKEN
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
}

public String extraerRol(String token) {
    return extraerClaims(token).get("rol", String.class);
}
```

**Archivo**: `auth-service/src/main/java/.../auth/controller/AuthController.java`

```java
// En login y register, ahora se pasa el rol:
String token = jwtService.generarToken(user.getUsername(), user.getRol().name());
```

### 2. prestamo-service - Extraer rol del token

**Archivo**: `prestamo-service/src/main/java/.../prestamo/service/JwtService.java`

```java
public String extraerRol(String token) {
    return extraerClaims(token).get("rol", String.class);
}
```

**Archivo**: `prestamo-service/src/main/java/.../prestamo/config/JwtFilter.java`

```java
String username = jwtService.extraerUsuario(token);
String rol = jwtService.extraerRol(token);

if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    // Usar el rol del token, o USER por defecto
    String authority = rol != null ? "ROLE_" + rol : "ROLE_USER";
    
    UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(authority))
            );
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```

### 3. libro-service - Mismo cambio

Se aplicaron los mismos cambios en `JwtService.java` y `JwtFilter.java`.

### 4. CORS Configurado

Todos los servicios tienen configuración CORS para aceptar peticiones desde `http://localhost:4200`.

## 🚀 Pasos para Solucionar el Problema

### Paso 1: Compilar el Backend

```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios

# Compilar todos los servicios
mvn clean install -DskipTests
```

### Paso 2: Reiniciar los Microservicios

**Detén todos los servicios que estén corriendo** (Ctrl+C en cada terminal).

Luego inicia cada servicio:

```bash
# Terminal 1 - Auth Service (puerto 8080)
cd /Users/familia/Desktop/spring/biblioteca-microservicios/auth-service
mvn spring-boot:run

# Terminal 2 - Libro Service (puerto 8081)
cd /Users/familia/Desktop/spring/biblioteca-microservicios/libro-service
mvn spring-boot:run

# Terminal 3 - Prestamo Service (puerto 8082)
cd /Users/familia/Desktop/spring/biblioteca-microservicios/prestamo-service
mvn spring-boot:run
```

**Espera** a que cada servicio muestre el mensaje: `Started ...Application in X seconds`

### Paso 3: Verificar que los Servicios Estén Activos

```bash
# Verificar cada servicio
curl http://localhost:8080/actuator/health  # auth-service
curl http://localhost:8081/actuator/health  # libro-service
curl http://localhost:8082/actuator/health  # prestamo-service

# Respuesta esperada de cada uno: {"status":"UP"}
```

### Paso 4: Reiniciar el Frontend

```bash
cd /Users/familia/Desktop/spring/biblioteca-frontend/biblioteca-app

# Si está corriendo, detenlo (Ctrl+C)

# Reiniciar
npm start
# o
ng serve
```

### Paso 5: ⚠️ CRÍTICO - Hacer Logout y Login Nuevamente

**MUY IMPORTANTE:** Los tokens antiguos (generados antes de estos cambios) NO incluyen el rol del usuario.

1. Abre el navegador en `http://localhost:4200`
2. **Cierra sesión** (Logout) si ya estás logueado
3. **Inicia sesión nuevamente** con tus credenciales
4. El nuevo token incluirá tu rol

### Paso 6: Probar Crear un Préstamo

1. Ve a la lista de libros
2. Selecciona un libro disponible
3. Haz clic en "Solicitar Préstamo" o el botón correspondiente
4. ✅ **Debería funcionar correctamente ahora**

## 🔍 Verificar que el Token Incluye el Rol

Para confirmar que el nuevo token tiene el rol:

1. Abre **DevTools** en el navegador (F12)
2. Ve a **Application** → **Local Storage** → `http://localhost:4200`
3. Busca el item que contiene el token JWT (puede llamarse `token`, `authToken`, etc.)
4. Copia el valor del token
5. Ve a [https://jwt.io](https://jwt.io)
6. Pega el token en la sección "Encoded"
7. En la sección "Decoded" deberías ver:

```json
{
  "sub": "tu_usuario",
  "rol": "USER",        ← DEBE ESTAR PRESENTE
  "iat": 1736778000,
  "exp": 1736864400
}
```

Si NO ves el campo `"rol"`, significa que estás usando un token antiguo. **Haz logout y login nuevamente.**

## ❌ Troubleshooting

### Error: "Failed to fetch" o "Connection refused"

**Causa:** El servicio no está corriendo o está en el puerto incorrecto.

**Solución:**
1. Verifica que los 3 servicios estén corriendo
2. Verifica los puertos con `curl http://localhost:XXXX/actuator/health`
3. Revisa la consola de cada servicio para errores

### Error: 401 Unauthorized al crear préstamo

**Causa:** Token antiguo sin el rol, o token expirado.

**Solución:**
1. ⚠️ **Haz logout en el frontend**
2. ⚠️ **Inicia sesión nuevamente**
3. Intenta crear el préstamo de nuevo
4. Si persiste, verifica el token en jwt.io

### Error: "Cannot read property 'name' of null" en el backend

**Causa:** El objeto `Authentication` es null en el controller.

**Solución:**
1. Verifica que el header `Authorization: Bearer <token>` se esté enviando
2. Abre DevTools (F12) → Network
3. Busca la petición POST a `/prestamos/{id}`
4. Verifica que tenga el header `Authorization`
5. Si no está, revisa el interceptor de autenticación del frontend

### Error: CORS policy

**Causa:** El navegador bloquea la petición por CORS.

**Solución:**
- La configuración CORS ya está implementada
- Reinicia los servicios del backend
- Limpia la caché del navegador (Ctrl+Shift+Delete)
- Verifica que el frontend esté en `http://localhost:4200`

### El préstamo se crea pero no aparece en la lista

**Causa:** Problema de sincronización o mensajería.

**Solución:**
1. Verifica que RabbitMQ esté corriendo: `rabbitmqctl status`
2. Revisa los logs del prestamo-service y libro-service
3. Busca errores relacionados con RabbitMQ

## 📝 Verificación Completa del Flujo

### 1. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario1",
    "password": "password123"
  }'
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3VhcmlvMSIsInJvbCI6IlVTRVIiLCJpYXQiOjE3MzY3NzgwMDAsImV4cCI6MTczNjg2NDQwMH0...."
}
```

### 2. Crear Préstamo
```bash
curl -X POST http://localhost:8082/prestamos/1 \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json"
```

**Respuesta esperada (201 Created):**
```json
{
  "id": 1,
  "libroId": 1,
  "username": "usuario1",
  "fechaPrestamo": "2026-01-13T...",
  "fechaDevolucion": null,
  "activo": true
}
```

### 3. Ver Mis Préstamos
```bash
curl http://localhost:8082/prestamos \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

## 📚 Archivos Modificados

### auth-service
- ✅ `src/main/java/.../auth/service/JwtService.java` - Genera token con rol
- ✅ `src/main/java/.../auth/controller/AuthController.java` - Pasa rol al generar token
- ✅ `src/main/java/.../auth/config/CorsConfig.java` - Configuración CORS

### libro-service
- ✅ `src/main/java/.../libro/service/JwtService.java` - Extrae rol del token
- ✅ `src/main/java/.../libro/config/JwtFilter.java` - Asigna rol dinámicamente
- ✅ `src/main/java/.../libro/config/CorsConfig.java` - Configuración CORS

### prestamo-service
- ✅ `src/main/java/.../prestamo/service/JwtService.java` - Extrae rol del token
- ✅ `src/main/java/.../prestamo/config/JwtFilter.java` - Asigna rol dinámicamente
- ✅ `src/main/java/.../prestamo/config/CorsConfig.java` - Configuración CORS

## 🎯 Resumen

✅ **Backend**: Completamente solucionado y listo  
✅ **Puertos**: Correctamente configurados (8080, 8081, 8082)  
✅ **CORS**: Configurado para aceptar localhost:4200  
✅ **JWT**: Ahora incluye el rol del usuario  
⚠️ **Acción requerida**: Reiniciar servicios + Logout/Login en el frontend  

---

**Fecha:** 13 de enero de 2026  
**Estado:** ✅ Backend corregido | ⚠️ Requiere reinicio + nuevo login

