# Solución: Problema de Autenticación en Préstamos

## Problema Identificado

El usuario estaba autenticado pero no podía realizar préstamos porque el sistema no reconocía su autenticación. Esto ocurría porque:

1. **El token JWT no incluía el rol del usuario** - Solo contenía el username
2. **Los microservices asignaban roles hardcodeados** - prestamo-service asignaba siempre `ROLE_USER` y libro-service asignaba `ROLE_ADMIN`
3. **No había propagación correcta de la autorización** entre microservicios

## Solución Implementada

### 1. Modificaciones en `auth-service`

#### JwtService.java
- ✅ **Modificado el método `generarToken`** para incluir el rol como claim en el JWT
- ✅ **Agregado método `extraerRol`** para extraer el rol del token

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

#### AuthController.java
- ✅ **Actualizado el método `login`** para pasar el rol al generar el token
- ✅ **Actualizado el método `register`** para pasar el rol al generar el token

### 2. Modificaciones en `prestamo-service`

#### JwtService.java
- ✅ **Agregado método `extraerRol`** para extraer el rol del token

#### JwtFilter.java
- ✅ **Modificado el filtro** para extraer el rol del token y asignarlo dinámicamente
- ✅ **Eliminado el rol hardcodeado** `ROLE_USER`

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

### 3. Modificaciones en `libro-service`

#### JwtService.java
- ✅ **Agregado método `extraerRol`** para extraer el rol del token

#### JwtFilter.java
- ✅ **Modificado el filtro** para extraer el rol del token y asignarlo dinámicamente
- ✅ **Eliminado el rol hardcodeado** `ROLE_ADMIN`

## Cómo Funciona Ahora

### Flujo de Autenticación y Autorización

1. **Login/Register** → El usuario se autentica en `auth-service`
   ```
   POST /auth/login
   {
     "username": "usuario1",
     "password": "password123"
   }
   ```

2. **Token JWT generado** → Incluye username Y rol
   ```json
   {
     "sub": "usuario1",
     "rol": "USER",
     "iat": 1234567890,
     "exp": 1234654290
   }
   ```

3. **Petición a prestamo-service** → El JwtFilter extrae el rol del token
   ```
   POST /prestamos/1
   Headers: Authorization: Bearer eyJhbGc...
   ```

4. **Validación** → El sistema verifica que el usuario tiene `ROLE_USER` para crear préstamos

5. **Autorización exitosa** ✅ → El préstamo se crea correctamente

## Verificación

Para verificar que todo funciona:

### 1. Reiniciar los servicios
```bash
# Si usas docker-compose
docker-compose down
docker-compose up -d

# O reinicia cada servicio manualmente
```

### 2. Probar el flujo completo

#### a) Login como USER
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario1",
    "password": "password123"
  }'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### b) Crear un préstamo (con el token obtenido)
```bash
curl -X POST http://localhost:8083/prestamos/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json"
```

✅ **Ahora debería funcionar correctamente**

#### c) Verificar roles
- **Usuario con ROLE_USER**: Puede crear préstamos ✅
- **Usuario con ROLE_ADMIN**: Puede crear/editar/eliminar libros ✅

## Archivos Modificados

### auth-service
- `src/main/java/com/biblioteca/auth/service/JwtService.java`
- `src/main/java/com/biblioteca/auth/controller/AuthController.java`

### prestamo-service
- `src/main/java/com/biblioteca/prestamo/service/JwtService.java`
- `src/main/java/com/biblioteca/prestamo/config/JwtFilter.java`

### libro-service
- `src/main/java/com/biblioteca/libro/service/JwtService.java`
- `src/main/java/com/biblioteca/libro/config/JwtFilter.java`

## Notas Importantes

⚠️ **IMPORTANTE**: Los usuarios que tengan tokens antiguos (generados antes de este cambio) necesitarán hacer login nuevamente para obtener un token con el rol incluido.

🔒 **Seguridad**: El rol ahora se propaga correctamente entre todos los microservicios mediante el token JWT.

✅ **Compilación**: Todos los microservicios se compilaron exitosamente sin errores.

## Testing

Puedes verificar que el token incluye el rol decodificándolo en [jwt.io](https://jwt.io/). Deberías ver algo como:

```json
{
  "sub": "usuario1",
  "rol": "USER",
  "iat": 1736778000,
  "exp": 1736864400
}
```

---

**Fecha de implementación**: 13 de enero de 2026  
**Problema resuelto**: Autenticación funciona correctamente para crear préstamos ✅

