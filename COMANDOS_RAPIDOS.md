# ⚡ Comandos Rápidos - Biblioteca Microservicios con Kafka

## 🚀 Inicio Rápido (Opción Automática)

```bash
chmod +x run-all.sh
./run-all.sh
```

---

## 📋 Comandos Paso a Paso

### 1️⃣ Levantar Infraestructura
```bash
docker-compose up -d
docker-compose ps  # Verificar estado
```

### 2️⃣ Compilar
```bash
mvn clean package -DskipTests
```

### 3️⃣ Ejecutar Servicios (3 terminales)
```bash
# Terminal 1: libro-service
java -jar libro-service/target/libro-service-1.0.0.jar

# Terminal 2: prestamo-service
java -jar prestamo-service/target/prestamo-service-1.0.0.jar

# Terminal 3: auth-service (opcional)
java -jar auth-service/target/auth-service-1.0.0.jar
```

### 4️⃣ Probar Flujo Kafka
```bash
chmod +x test-kafka.sh
./test-kafka.sh
```

---

## 🧪 Tests

```bash
# Todos los tests
mvn test

# Solo prestamo-service
mvn -pl prestamo-service test

# Solo libro-service
mvn -pl libro-service test

# Test específico
mvn -pl prestamo-service -Dtest=PrestamoServiceTest test
```

---

## 🔍 Monitoreo Kafka

### Ver Topics
```bash
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Consumir Mensajes del Topic
```bash
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic prestamo.topic \
  --from-beginning
```

### Describir Topic
```bash
docker exec -it kafka kafka-topics \
  --describe \
  --topic prestamo.topic \
  --bootstrap-server localhost:9092
```

### Ver Consumer Groups
```bash
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --list

docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group libro-group
```

### Crear Topic Manualmente (si es necesario)
```bash
docker exec -it kafka kafka-topics --create \
  --topic prestamo.topic \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1
```

---

## 🌐 URLs Útiles

```bash
# Kafka UI
open http://localhost:8090

# Swagger libro-service
open http://localhost:8081/swagger-ui.html

# Swagger prestamo-service
open http://localhost:8082/swagger-ui.html

# Actuator libro-service
curl http://localhost:8081/actuator/health | jq

# Actuator prestamo-service
curl http://localhost:8082/actuator/health | jq
```

---

## 📦 Docker Commands

### Ver Logs
```bash
# Todos los servicios
docker-compose logs -f

# Solo Kafka
docker-compose logs -f kafka

# Solo PostgreSQL
docker-compose logs -f postgres-libro
```

### Reiniciar Servicios
```bash
docker-compose restart

# Solo Kafka
docker-compose restart kafka

# Solo Zookeeper + Kafka
docker-compose restart zookeeper kafka
```

### Detener Todo
```bash
docker-compose down

# Con volúmenes (limpieza completa)
docker-compose down -v
```

### Ver Estado
```bash
docker-compose ps
docker-compose top
```

---

## 🧹 Limpieza

```bash
# Limpiar compilaciones Maven
mvn clean

# Detener y limpiar Docker
docker-compose down -v

# Limpiar todo (Maven + Docker)
mvn clean && docker-compose down -v
```

---

## 📡 API Testing con cURL

### 1. Login (obtener JWT)
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.token')

echo "Token: $TOKEN"
```

### 2. Crear Libro
```bash
curl -X POST http://localhost:8081/libros \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "El Quijote",
    "autor": "Cervantes",
    "isbn": "978-1234567890",
    "stock": 5
  }' | jq
```

### 3. Listar Libros
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/libros | jq
```

### 4. Crear Préstamo
```bash
curl -X POST http://localhost:8082/prestamos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "libroId": 1
  }' | jq
```

### 5. Verificar Stock (debe decrementarse)
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/libros/1 | jq '.stock'
```

---

## 🐛 Troubleshooting

### Puerto ocupado
```bash
lsof -i :8081
kill -9 <PID>
```

### Kafka no responde
```bash
docker-compose restart zookeeper kafka
docker-compose logs -f kafka
```

### Verificar conectividad Kafka
```bash
docker exec -it kafka kafka-broker-api-versions \
  --bootstrap-server localhost:9092
```

### Limpiar topic (resetear)
```bash
docker exec -it kafka kafka-topics --delete \
  --topic prestamo.topic \
  --bootstrap-server localhost:9092
```

---

## 📊 Monitoreo en Tiempo Real

### Tail logs de Kafka
```bash
docker-compose logs -f kafka | grep prestamo.topic
```

### Tail logs de libro-service
```bash
tail -f libro-service.log | grep "Recibido evento"
```

### Watch consumer group lag
```bash
watch -n 2 'docker exec kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe --group libro-group'
```

---

## 🎯 One-Liners Útiles

```bash
# Full restart
docker-compose down && docker-compose up -d && sleep 15 && mvn clean package -DskipTests

# Rebuild + restart
mvn clean package -DskipTests && docker-compose restart

# Ver último evento en Kafka
docker exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic prestamo.topic \
  --max-messages 1 \
  --from-beginning

# Count mensajes en topic
docker exec kafka kafka-run-class kafka.tools.GetOffsetShell \
  --broker-list localhost:9092 \
  --topic prestamo.topic \
  --time -1

# Ver recursos Docker
docker stats --no-stream
```

---

## 📖 Documentación

```bash
# Ver documentación completa
cat MIGRACION_KAFKA.md | less

# Ver README
cat README_KAFKA.md | less

# Ver resumen
cat RESUMEN_COMPLETADO.md | less
```

---

## ✅ Checklist de Verificación

```bash
# 1. Infraestructura OK?
docker-compose ps | grep "Up"

# 2. Kafka OK?
docker exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092

# 3. Topic creado?
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092 | grep prestamo

# 4. Servicios compilados?
ls -lh */target/*.jar

# 5. Servicios corriendo?
curl -s http://localhost:8081/actuator/health
curl -s http://localhost:8082/actuator/health
```

---

**💡 Tip:** Guarda este archivo en favoritos para acceso rápido a comandos comunes.

*Última actualización: 9 de enero de 2026*

