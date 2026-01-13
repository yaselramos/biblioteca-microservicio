# 🚀 GUÍA COMPLETA DE EJECUCIÓN

**Sistema de Biblioteca - Full Stack**  
**Backend:** Spring Boot Microservicios + Kafka  
**Frontend:** Angular 17 + Material Design

---

## ✅ PREREQUISITOS

- [x] Java 17+
- [x] Maven 3.8+
- [x] Docker & Docker Compose
- [x] Node.js 18+
- [x] Angular CLI 17+

---

## 📦 PASO 1: LEVANTAR INFRAESTRUCTURA

```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios
docker-compose up -d
```

**Esto levanta:**
- 3 PostgreSQL (puertos 5433, 5432, 5434)
- Kafka (puerto 9092)
- Zookeeper (puerto 2181)
- Kafka UI (puerto 8090)

**Verificar:**
```bash
docker-compose ps
# Todos deben estar "Up"
```

**Kafka UI:** http://localhost:8090

---

## 🔨 PASO 2: COMPILAR BACKEND

```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios
mvn clean package -DskipTests
```

**Espera:** 2-3 minutos

**Resultado esperado:**
```
[INFO] BUILD SUCCESS
```

---

## 🚀 PASO 3: EJECUTAR MICROSERVICIOS

Abre **3 terminales diferentes:**

### Terminal 1 - Auth Service
```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios
java -jar auth-service/target/auth-service-1.0.0.jar
```

**Espera:** "Started AuthServiceApplication"  
**Puerto:** 8080

### Terminal 2 - Libro Service
```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios
java -jar libro-service/target/libro-service-1.0.0.jar
```

**Espera:** "Started LibroServiceApplication"  
**Puerto:** 8081

### Terminal 3 - Prestamo Service
```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios
java -jar prestamo-service/target/prestamo-service-1.0.0.jar
```

**Espera:** "Started PrestamoServiceApplication"  
**Puerto:** 8082

---

## ✅ PASO 4: VERIFICAR BACKEND

```bash
# Auth service
curl http://localhost:8080/actuator/health
# Respuesta: {"status":"UP"}

# Libro service
curl http://localhost:8081/actuator/health
# Respuesta: {"status":"UP"}

# Prestamo service
curl http://localhost:8082/actuator/health
# Respuesta: {"status":"UP"}
```

**Swagger UIs:**
- Auth: http://localhost:8080/swagger-ui.html
- Libros: http://localhost:8081/swagger-ui.html
- Préstamos: http://localhost:8082/swagger-ui.html

---

## 🎨 PASO 5: EJECUTAR FRONTEND

### Nueva Terminal - Frontend
```bash
cd /Users/familia/Desktop/spring/biblioteca-frontend/biblioteca-app
npm install  # Solo la primera vez
ng serve
```

**Espera:** "Compiled successfully"

**Abre navegador:** http://localhost:4200

---

## 🧪 PASO 6: PROBAR EL SISTEMA

### 1. Registro
1. Ve a http://localhost:4200/register
2. Completa el formulario:
   - Username: `testuser`
   - Email: `test@mail.com`
   - Password: `123456`
   - Confirmar Password: `123456`
   - Rol: `USER`
3. Click "Registrarse"
4. Deberías ver mensaje de éxito

### 2. Login
1. Ve a http://localhost:4200/login
2. Ingresa credenciales:
   - Username: `testuser`
   - Password: `123456`
3. Click "Iniciar Sesión"
4. Deberías ser redirigido a la página de inicio

### 3. Ver Libros (como ADMIN)
1. Primero crea un usuario ADMIN:
   - Registro con rol ADMIN o usa:
     - Username: `admin`
     - Password: `admin123`
2. Login como ADMIN
3. Ve a `/libros`
4. Click "Nuevo Libro"
5. Completa:
   - Título: `Don Quijote`
   - Autor: `Cervantes`
   - ISBN: `978-1234567890`
   - Stock: `5`
6. Guarda

### 4. Solicitar Préstamo (como USER)
1. Login como USER
2. Ve a `/libros`
3. Busca el libro creado
4. Click "Solicitar"
5. Ve a `/prestamos`
6. Deberías ver tu préstamo activo

### 5. Verificar Kafka
1. Abre http://localhost:8090
2. Ve a "Topics"
3. Busca `prestamo.topic`
4. Deberías ver el evento `PRESTAMO_CREADO`

### 6. Verificar Stock
1. Vuelve a `/libros`
2. El stock del libro debería haberse decrementado de 5 a 4

### 7. Devolver Libro
1. Ve a `/prestamos`
2. Click "Devolver Libro"
3. Ve a `/libros`
4. El stock debería volver a 5

---

## 🎯 FLUJO COMPLETO VERIFICADO

```
✅ Frontend carga correctamente
✅ Login funciona
✅ JWT se guarda en localStorage
✅ Navbar muestra usuario
✅ CRUD de libros funciona (ADMIN)
✅ Solicitar préstamo funciona (USER)
✅ Kafka publica evento
✅ Stock se actualiza automáticamente
✅ Devolución funciona
✅ Stock se incrementa
```

---

## 🔍 MONITOREO

### URLs Útiles

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Frontend | http://localhost:4200 | Aplicación Angular |
| Auth Service | http://localhost:8080 | Autenticación |
| Libro Service | http://localhost:8081 | Gestión libros |
| Prestamo Service | http://localhost:8082 | Gestión préstamos |
| Kafka UI | http://localhost:8090 | Monitoreo Kafka |
| Swagger Auth | http://localhost:8080/swagger-ui.html | API Docs |
| Swagger Libros | http://localhost:8081/swagger-ui.html | API Docs |
| Swagger Préstamos | http://localhost:8082/swagger-ui.html | API Docs |

### Comandos Útiles

```bash
# Ver logs de Kafka
docker-compose logs -f kafka

# Ver mensajes en topic
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic prestamo.topic \
  --from-beginning

# Ver bases de datos
docker-compose ps

# Ver logs de un microservicio
# (en la terminal donde está corriendo)
```

---

## 🛑 DETENER TODO

### Detener Frontend
```bash
Ctrl + C  # En la terminal de ng serve
```

### Detener Microservicios
```bash
Ctrl + C  # En cada una de las 3 terminales
```

### Detener Infraestructura
```bash
cd /Users/familia/Desktop/spring/biblioteca-microservicios
docker-compose down
```

### Detener y Limpiar Todo (incluye volúmenes)
```bash
docker-compose down -v
```

---

## 🐛 TROUBLESHOOTING

### Puerto ocupado
```bash
# Ver qué proceso usa el puerto
lsof -i :8080
lsof -i :8081
lsof -i :8082
lsof -i :4200

# Matar proceso
kill -9 <PID>
```

### Error CORS
- Verifica que el backend tenga CORS configurado
- Verifica que el frontend use las URLs correctas

### Error localStorage
- El fix ya está aplicado (isPlatformBrowser)
- Limpia caché: `rm -rf .angular/cache`

### Error de conexión BD
- Verifica que docker-compose esté corriendo
- `docker-compose ps` debe mostrar "Up"

### Kafka no procesa eventos
- Verifica logs de libro-service
- Revisa Kafka UI para ver consumer groups
- Verifica que el topic existe

---

## 📚 DOCUMENTACIÓN

### Backend
- `BACKEND_PREPARADO_FRONTEND.md` - Cambios para integración
- `RESUMEN_CAMBIOS_BACKEND.md` - Resumen rápido
- `MIGRACION_KAFKA.md` - Migración a Kafka

### Frontend
- `README_FRONTEND.md` - Guía completa
- `RESUMEN_COMPLETO.md` - Estado del proyecto
- `FIX_LOCALSTORAGE_SSR.md` - Fix SSR

---

## ✅ CHECKLIST DE EJECUCIÓN

- [ ] Docker Desktop corriendo
- [ ] `docker-compose up -d` ejecutado
- [ ] Backend compilado (`mvn clean package`)
- [ ] auth-service corriendo en 8080
- [ ] libro-service corriendo en 8081
- [ ] prestamo-service corriendo en 8082
- [ ] Frontend corriendo en 4200
- [ ] Registro funciona
- [ ] Login funciona
- [ ] CRUD libros funciona
- [ ] Préstamos funcionan
- [ ] Kafka procesa eventos
- [ ] Stock se actualiza

---

## 🎉 ¡ÉXITO!

Si todos los pasos anteriores funcionan:

**¡FELICITACIONES! 🎊**

Tienes un sistema full-stack completamente funcional con:
- ✅ Arquitectura de microservicios
- ✅ Mensajería asíncrona con Kafka
- ✅ Frontend moderno con Angular
- ✅ Autenticación JWT
- ✅ CRUD completo
- ✅ Eventos en tiempo real

**¡A DESARROLLAR! 🚀**

---

*Guía creada el 12 de enero de 2026*

