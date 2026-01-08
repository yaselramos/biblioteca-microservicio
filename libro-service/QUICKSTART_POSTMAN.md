# 🚀 Quick Start - Libro Service

## Iniciar el servicio

```bash
cd libro-service
mvn spring-boot:run
```

El servicio estará disponible en: `http://localhost:8081`

---

## Probar en Postman - 3 pasos rápidos

### 1. Obtener token ADMIN (para crear libros)
```
POST http://localhost:8080/auth/login
Body: {"username": "admin", "password": "admin123"}
```
👉 Copia el token de la respuesta

### 2. Crear un libro
```
POST http://localhost:8081/libros
Header: Authorization: Bearer {tu_token}
Header: Content-Type: application/json
Body: 
{
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "stock": 10
}
```

### 3. Listar todos los libros
```
GET http://localhost:8081/libros
```

---

## Archivos de ayuda incluidos

- **POSTMAN_LIBRO_SERVICE.md** - Guía completa con todos los detalles
- **Biblioteca_Libro_Service.postman_collection.json** - Colección importable en Postman
- **Ejemplos_Postman_Libro_Service.md** - Ejemplos visuales paso a paso

---

## Endpoints disponibles

| Método | Endpoint | Requiere Auth | Rol |
|--------|----------|---------------|-----|
| POST | /libros | ✅ Sí | ADMIN |
| GET | /libros | ❌ No | - |
| GET | /libros/{id} | ❌ No | - |
| PUT | /libros/{id} | ❌ No | - |
| DELETE | /libros/{id} | ❌ No | - |

---

## Importar colección en Postman

1. Abrir Postman
2. Click en "Import"
3. Seleccionar archivo: `Biblioteca_Libro_Service.postman_collection.json`
4. ¡Listo! Ya tienes todas las requests configuradas

---

## Requisitos

- ✅ Java 17+
- ✅ PostgreSQL corriendo
- ✅ Base de datos `libro_service` creada
- ✅ Auth-service corriendo en puerto 8080 (para obtener token)

---

¡Feliz testing! 🎉

