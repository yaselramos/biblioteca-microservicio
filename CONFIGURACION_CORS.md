# Configuración CORS para Frontend

## Cambios Implementados

Se ha configurado CORS (Cross-Origin Resource Sharing) en los tres microservicios para permitir conexiones desde el frontend en **http://localhost:4200/**

### Archivos Creados

#### 1. auth-service
- **CorsConfig.java**: `/auth-service/src/main/java/com/biblioteca/auth/config/CorsConfig.java`
- **Modificado**: `SecurityConfig.java` - Se agregó `.cors(cors -> cors.configure(http))`

#### 2. libro-service
- **CorsConfig.java**: `/libro-service/src/main/java/com/biblioteca/libro/config/CorsConfig.java`
- **Modificado**: `SecurityConfig.java` - Se agregó `.cors(cors -> cors.configure(http))`

#### 3. prestamo-service
- **CorsConfig.java**: `/prestamo-service/src/main/java/com/biblioteca/prestamo/config/CorsConfig.java`
- **Modificado**: `SecurityConfig.java` - Se agregó `.cors(cors -> cors.configure(http))`

## Configuración CORS

Cada archivo `CorsConfig.java` incluye:

- ✅ **Origen permitido**: `http://localhost:4200`
- ✅ **Métodos HTTP**: GET, POST, PUT, DELETE, OPTIONS, PATCH
- ✅ **Headers permitidos**: Todos (*)
- ✅ **Credenciales**: Habilitadas (allowCredentials = true)
- ✅ **Headers expuestos**: Authorization, Content-Type
- ✅ **Cache preflight**: 3600 segundos (1 hora)

## Cómo Usar

1. **Reiniciar los microservicios** para que los cambios tomen efecto
2. El frontend en `http://localhost:4200` ahora puede hacer peticiones a:
   - Auth Service: `http://localhost:8081/auth/**`
   - Libro Service: `http://localhost:8082/libros/**`
   - Prestamo Service: `http://localhost:8083/prestamos/**`

## Ejemplo de Petición desde Angular

```typescript
// Ejemplo de login
this.http.post('http://localhost:8081/auth/login', {
  username: 'admin',
  password: 'admin123'
}, {
  withCredentials: true
}).subscribe(response => {
  console.log(response);
});

// Ejemplo de GET con token
const headers = new HttpHeaders({
  'Authorization': `Bearer ${token}`
});

this.http.get('http://localhost:8082/libros', {
  headers: headers,
  withCredentials: true
}).subscribe(response => {
  console.log(response);
});
```

## Notas Importantes

- 🔒 Las peticiones que requieren autenticación deben incluir el header `Authorization: Bearer <token>`
- 🌐 Solo se permite el origen `http://localhost:4200` por seguridad
- 📝 Si necesitas agregar más orígenes, modifica el array en `setAllowedOrigins()`
- ⚠️ En producción, cambia `http://localhost:4200` por la URL de tu frontend en producción

## Verificación

Para verificar que CORS está funcionando correctamente:

1. Abre la consola del navegador (F12)
2. Realiza una petición desde el frontend
3. Verifica que NO aparezcan errores de CORS en la consola
4. Deberías ver los headers CORS en la respuesta:
   - `Access-Control-Allow-Origin: http://localhost:4200`
   - `Access-Control-Allow-Credentials: true`

