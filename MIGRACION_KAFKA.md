# 🔄 Migración de RabbitMQ a Apache Kafka

## 📋 Resumen de Cambios

Este documento describe la migración completa del sistema de mensajería asíncrona de **RabbitMQ** a **Apache Kafka** en los microservicios `libro-service` y `prestamo-service`.

---

## 🎯 Objetivo

Reemplazar la comunicación asíncrona basada en RabbitMQ por Apache Kafka para:
- ✅ Mayor escalabilidad y throughput
- ✅ Persistencia de mensajes duradera
- ✅ Soporte nativo para streaming de eventos
- ✅ Mejor integración con ecosistemas modernos de big data

---

## 📦 Cambios Implementados

### 1️⃣ **Dependencias Maven**

#### Antes (RabbitMQ)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

#### Después (Kafka)
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>3.0.16</version>
</dependency>
```

**Archivos modificados:**
- `libro-service/pom.xml`
- `prestamo-service/pom.xml`

---

### 2️⃣ **Configuración (application.properties)**

#### Antes (RabbitMQ)
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

#### Después (Kafka)
```properties
spring.kafka.bootstrap-servers=localhost:9092
```

**Archivos modificados:**
- `libro-service/src/main/resources/application.properties`
- `libro-service/src/test/resources/application.properties`
- `prestamo-service/src/main/resources/application.properties`
- `prestamo-service/src/test/resources/application.properties`

---

### 3️⃣ **Configuración de Beans (Java)**

#### libro-service: `RabbitMQConfig.java` → `KafkaConfig`

**Nota:** Mantuve el nombre `RabbitMQConfig` por compatibilidad, pero ahora configura Kafka.

**Cambios principales:**
```java
@Configuration
@EnableKafka
public class RabbitMQConfig {
    
    public static final String PRESTAMO_QUEUE = "prestamo.topic"; // Ahora es un topic de Kafka
    
    @Bean
    public NewTopic prestamoTopic() {
        return new NewTopic(PRESTAMO_QUEUE, 1, (short) 1);
    }
    
    @Bean
    public KafkaTemplate<String, PrestamoEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public ConsumerFactory<String, PrestamoEvent> consumerFactory() {
        // Configuración de deserialización JSON
        JsonDeserializer<PrestamoEvent> deserializer = new JsonDeserializer<>(PrestamoEvent.class);
        deserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(props, 
            new StringDeserializer(), 
            deserializer);
    }
}
```

**Archivo:** `libro-service/src/main/java/com/biblioteca/libro/config/RabbitMQConfig.java`

---

#### prestamo-service: `RabbitMQConfig.java` → Producer Config

**Cambios principales:**
```java
@Configuration
@EnableKafka
public class RabbitMQConfig {
    
    public static final String PRESTAMO_QUEUE = "prestamo.topic";
    
    @Bean
    public NewTopic prestamoTopic() {
        return new NewTopic(PRESTAMO_QUEUE, 1, (short) 1);
    }
    
    @Bean
    public KafkaTemplate<String, PrestamoEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

**Archivo:** `prestamo-service/src/main/java/com/biblioteca/prestamo/config/RabbitMQConfig.java`

---

### 4️⃣ **Consumer (Listener)**

#### Antes (RabbitMQ)
```java
@RabbitListener(queues = RabbitMQConfig.PRESTAMO_QUEUE)
public void handlePrestamoEvent(PrestamoEvent event) {
    // Lógica de procesamiento
}
```

#### Después (Kafka)
```java
@KafkaListener(topics = RabbitMQConfig.PRESTAMO_QUEUE, groupId = "libro-group")
public void handlePrestamoEvent(PrestamoEvent event) {
    // Lógica de procesamiento (sin cambios)
}
```

**Archivo:** `libro-service/src/main/java/com/biblioteca/libro/messaging/PrestamoEventListener.java`

---

### 5️⃣ **Producer (Publisher)**

#### Antes (RabbitMQ)
```java
@Service
public class PrestamoEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    
    public void publishPrestamoEvent(PrestamoEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.PRESTAMO_QUEUE, event);
    }
}
```

#### Después (Kafka)
```java
@Service
public class PrestamoEventPublisher {
    private final KafkaTemplate<String, PrestamoEvent> kafkaTemplate;
    
    public void publishPrestamoEvent(PrestamoEvent event) {
        kafkaTemplate.send(RabbitMQConfig.PRESTAMO_QUEUE, event);
    }
}
```

**Archivo:** `prestamo-service/src/main/java/com/biblioteca/prestamo/messaging/PrestamoEventPublisher.java`

---

### 6️⃣ **Correcciones Adicionales**

#### Fix en `PrestamoService.prestarLibro()`

**Problema:** El test esperaba `ResourceNotFoundException` al recibir 404, pero el código lanzaba `HttpClientErrorException`.

**Solución:**
```java
try {
    ResponseEntity<LibroDTO> response = restTemplate.getForEntity(
        LIBRO_SERVICE_URL + libroId,
        LibroDTO.class
    );
    // ... resto del código
} catch (HttpClientErrorException e) {
    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ResourceNotFoundException("Libro no encontrado: " + libroId);
    }
    throw e;
}
```

**Archivo:** `prestamo-service/src/main/java/com/biblioteca/prestamo/service/PrestamoService.java`

---

### 7️⃣ **Configuración de Tests (Maven Surefire)**

Para soportar Java 24 con Mockito/Byte Buddy, agregué al POM padre:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <argLine>-Dnet.bytebuddy.experimental=true</argLine>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**Archivo:** `pom.xml` (raíz)

---

## 🚀 Instrucciones de Ejecución

### Paso 1: Levantar Infraestructura (Docker Compose)

```bash
# Levantar PostgreSQL + Kafka + Zookeeper + Kafka UI
docker-compose up -d

# Verificar que todos los contenedores están corriendo
docker-compose ps

# Ver logs de Kafka
docker-compose logs -f kafka
```

**Servicios disponibles:**
- **PostgreSQL auth-service:** `localhost:5433`
- **PostgreSQL libro-service:** `localhost:5432`
- **PostgreSQL prestamo-service:** `localhost:5434`
- **Kafka Broker:** `localhost:9092`
- **Zookeeper:** `localhost:2181`
- **Kafka UI:** `http://localhost:8090`

---

### Paso 2: Compilar el Proyecto

```bash
# Compilar todos los módulos
mvn clean package -DskipTests

# O compilar con tests
mvn clean package
```

---

### Paso 3: Ejecutar los Microservicios

#### Opción A: Desde JARs compilados

```bash
# Terminal 1: auth-service
java -jar auth-service/target/auth-service-1.0.0.jar

# Terminal 2: libro-service
java -jar libro-service/target/libro-service-1.0.0.jar

# Terminal 3: prestamo-service
java -jar prestamo-service/target/prestamo-service-1.0.0.jar
```

#### Opción B: Desde IDE (IntelliJ/Eclipse)

1. Importar el proyecto como Maven multi-módulo
2. Ejecutar cada `*Application.java` como Spring Boot App

---

### Paso 4: Verificar Funcionamiento

#### 4.1 Crear un libro (libro-service)

```bash
curl -X POST http://localhost:8081/libros \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <tu-token-jwt>" \
  -d '{
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "isbn": "978-0307474728",
    "stock": 5
  }'
```

#### 4.2 Crear un préstamo (prestamo-service)

```bash
curl -X POST http://localhost:8082/prestamos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <tu-token-jwt>" \
  -d '{
    "username": "testuser",
    "libroId": 1
  }'
```

**Flujo esperado:**
1. `prestamo-service` crea el préstamo en la BD
2. Publica evento `PRESTAMO_CREADO` al topic `prestamo.topic` de Kafka
3. `libro-service` consume el evento
4. Decrementa el stock del libro en su BD

#### 4.3 Verificar en Kafka UI

1. Abrir `http://localhost:8090`
2. Ir a Topics → `prestamo.topic`
3. Ver los mensajes publicados

---

## 🧪 Ejecutar Tests

### Tests unitarios completos

```bash
# Todos los módulos
mvn test

# Solo prestamo-service
mvn -pl prestamo-service test

# Solo libro-service
mvn -pl libro-service test
```

### Tests específicos

```bash
# Test de PrestamoService
mvn -pl prestamo-service -Dtest=PrestamoServiceTest test

# Con verbose output
mvn -pl prestamo-service -Dtest=PrestamoServiceTest test -e
```

---

## 📊 Comparación RabbitMQ vs Kafka

| Característica | RabbitMQ | Kafka |
|----------------|----------|-------|
| **Modelo** | Message Queue | Event Log/Stream |
| **Persistencia** | Opcional | Siempre persistido |
| **Throughput** | ~20K msg/sec | ~100K msg/sec |
| **Latencia** | <1ms | 2-5ms |
| **Consumidores** | Elimina mensaje | Lee desde offset |
| **Replay** | No soportado | Nativo |
| **Orden** | Queue-level | Partition-level |
| **Escalabilidad** | Vertical | Horizontal |

---

## 🔍 Monitoreo y Debugging

### Ver logs de Kafka

```bash
# Logs en tiempo real
docker-compose logs -f kafka

# Ver todos los topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Describir topic prestamo.topic
docker exec -it kafka kafka-topics --describe --topic prestamo.topic --bootstrap-server localhost:9092

# Consumir mensajes desde el inicio
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic prestamo.topic \
  --from-beginning
```

### Ver mensajes en Kafka UI

1. Navegar a `http://localhost:8090`
2. Topics → prestamo.topic
3. Messages → Ver todos los eventos publicados

---

## ⚠️ Troubleshooting

### Problema: Kafka no arranca

**Síntoma:** Error "Connection refused" en logs

**Solución:**
```bash
# Reiniciar servicios
docker-compose restart zookeeper kafka

# Verificar healthchecks
docker-compose ps
```

---

### Problema: Topic no se crea automáticamente

**Solución manual:**
```bash
docker exec -it kafka kafka-topics --create \
  --topic prestamo.topic \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1
```

---

### Problema: Mensaje no llega al consumer

**Debugging:**

1. **Verificar que el mensaje se publicó:**
```bash
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic prestamo.topic \
  --from-beginning
```

2. **Revisar logs del libro-service:**
```bash
# Buscar "📥 Recibido evento de préstamo"
grep "Recibido evento" libro-service.log
```

3. **Verificar consumer group:**
```bash
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group libro-group
```

---

### Problema: Tests fallan con Java 24

**Solución:** Ya aplicada en `pom.xml` padre:
```xml
<argLine>-Dnet.bytebuddy.experimental=true</argLine>
```

Alternativa: Usar JDK 17 o 21 para ejecutar tests.

---

## 🎯 Próximos Pasos / Mejoras Sugeridas

### 1. Renombrar clases para mayor claridad
```bash
# RabbitMQConfig → KafkaConfig
mv libro-service/src/.../RabbitMQConfig.java libro-service/src/.../KafkaConfig.java
mv prestamo-service/src/.../RabbitMQConfig.java prestamo-service/src/.../KafkaConfig.java
```

### 2. Externalizar configuración
```properties
# application.properties
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
```

### 3. Implementar retry y error handling
```java
@KafkaListener(topics = "prestamo.topic", groupId = "libro-group")
public void handlePrestamoEvent(PrestamoEvent event) {
    try {
        // Procesar evento
    } catch (Exception e) {
        // Publicar a DLQ (Dead Letter Queue)
        kafkaTemplate.send("prestamo.topic.dlq", event);
    }
}
```

### 4. Tests de integración con Embedded Kafka
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 5. Implementar Schema Registry (Avro)
Para producción, usar Avro + Confluent Schema Registry para versionado de schemas.

---

## 📚 Referencias

- [Spring for Apache Kafka](https://spring.io/projects/spring-kafka)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Kafka vs RabbitMQ](https://www.confluent.io/kafka-vs-rabbitmq/)
- [Spring Boot Kafka Tutorial](https://www.baeldung.com/spring-kafka)

---

## ✅ Checklist de Migración

- [x] Actualizar dependencias Maven (RabbitMQ → Kafka)
- [x] Modificar application.properties (todos los servicios)
- [x] Crear configuración Kafka (beans en ambos servicios)
- [x] Migrar consumer (@RabbitListener → @KafkaListener)
- [x] Migrar producer (RabbitTemplate → KafkaTemplate)
- [x] Corregir tests unitarios (mapeo 404 → ResourceNotFoundException)
- [x] Configurar Surefire para Java 24 (Byte Buddy experimental)
- [x] Crear docker-compose.yml con Kafka + Zookeeper + Kafka UI
- [x] Documentar cambios y procedimientos
- [ ] Ejecutar tests completos (en progreso)
- [ ] Pruebas end-to-end con servicios levantados
- [ ] Renombrar clases (RabbitMQConfig → KafkaConfig) - opcional

---

## 🎉 Conclusión

La migración de RabbitMQ a Apache Kafka está **completada** y lista para pruebas. El sistema mantiene la misma funcionalidad con las ventajas de escalabilidad y persistencia de Kafka.

**Próximo paso:** Levantar la infraestructura y ejecutar pruebas end-to-end.

```bash
# ¡Listo para ejecutar!
docker-compose up -d
mvn clean package
java -jar libro-service/target/libro-service-1.0.0.jar &
java -jar prestamo-service/target/prestamo-service-1.0.0.jar &
```

---

**Fecha de migración:** 9 de enero de 2026  
**Autor:** GitHub Copilot  
**Versión:** 1.0.0

