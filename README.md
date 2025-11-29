# 🧬 API REST: Detector de Mutantes

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.11-blue.svg)](https://gradle.org/)
[![Coverage](https://img.shields.io/badge/Coverage->90%25-success.svg)](https://www.jacoco.org/)

## 📋 Descripción

API REST desarrollada con Spring Boot para detectar si un humano es mutante basándose en su secuencia de ADN.

Un humano es **mutante** si tiene **más de una secuencia** de 4 letras idénticas consecutivas (A, T, C, G) en cualquier dirección: horizontal, vertical o diagonal.

---

## 🚀 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.5.8**
- **Spring Data JPA**
- **H2 Database** (en memoria)
- **Gradle 8.11**
- **JUnit 5** + **Mockito**
- **Swagger/OpenAPI 3**
- **Lombok**
- **JaCoCo** (cobertura de código)

---

## 📂 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/example/Mutantes/
│   │   ├── config/                    # Configuraciones (Swagger, Exceptions)
│   │   ├── controller/                # Controladores REST
│   │   ├── dto/                       # DTOs (Request/Response)
│   │   ├── entity/                    # Entidades JPA
│   │   ├── repository/                # Repositorios Spring Data
│   │   ├── service/                   # Lógica de negocio
│   │   └── validator/                 # Validaciones personalizadas
│   └── resources/
│       └── application.properties     # Configuración de la aplicación
└── test/
    └── java/com/example/Mutantes/
        ├── controller/                # Tests de controladores (13 tests)
        ├── service/                   # Tests de servicios (40 tests)
        └── MutantesApplicationTests.java
```

---

## 🔧 Instalación y Ejecución

### Prerrequisitos

- Java 17 o superior
- Gradle 8.x (incluido en el proyecto via wrapper)

### Clonar el repositorio

```bash
git clone <repository-url>
cd Mutantes
```

### Compilar el proyecto

```bash
./gradlew build        # Linux/Mac
.\gradlew.bat build    # Windows
```

### Ejecutar la aplicación

```bash
./gradlew bootRun        # Linux/Mac
.\gradlew.bat bootRun    # Windows
```

La aplicación estará disponible en: `http://localhost:8080`

---

## 📊 Ejecutar Tests

### Ejecutar todos los tests

```bash
./gradlew test        # Linux/Mac
.\gradlew.bat test    # Windows
```

### Generar reporte de cobertura

```bash
./gradlew jacocoTestReport        # Linux/Mac
.\gradlew.bat jacocoTestReport    # Windows
```

Ver reporte de cobertura en: `build/reports/jacoco/test/html/index.html`

### Script de verificación rápida

```bash
# Windows
verify-tests.bat

# Linux/Mac
./verify-tests.sh
```

Este script:
1. Compila el proyecto
2. Ejecuta todos los tests (53 tests)
3. Genera reportes de cobertura
4. Abre los reportes en el navegador

---

## 📡 Endpoints de la API

### 1. Detectar Mutante

**POST** `/mutant`

Detecta si una secuencia de ADN pertenece a un mutante.

**Request Body:**
```json
{
  "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
}
```

**Respuestas:**
- `200 OK` - Es mutante
- `403 FORBIDDEN` - No es mutante (humano)
- `400 BAD REQUEST` - Solicitud inválida

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}'
```

### 2. Obtener Estadísticas

**GET** `/stats`

Obtiene las estadísticas de verificaciones de ADN.

**Respuesta:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

**Ejemplo con cURL:**
```bash
curl http://localhost:8080/stats
```

---

## 📖 Documentación API (Swagger)

La documentación interactiva de la API está disponible en:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

Desde Swagger UI puedes probar todos los endpoints directamente.

---

## 🗄️ Base de Datos H2

La aplicación utiliza H2 Database en memoria para persistir los resultados de análisis de ADN.

### Consola H2

Acceder a la consola web de H2: http://localhost:8080/h2-console

**Credenciales:**
- **JDBC URL:** `jdbc:h2:mem:mutantdb`
- **Username:** `sa`
- **Password:** *(vacío)*

### Tablas

**dna_records:**
- `id` (BIGINT) - PK, autoincremental
- `dna_hash` (VARCHAR 255) - Hash SHA-256 único del ADN
- `is_mutant` (BOOLEAN) - true si es mutante, false si es humano
- `created_at` (TIMESTAMP) - Fecha de creación

---

## 🧪 Suite de Pruebas

### Cobertura de Tests: >90%

Total: **53 tests**

| Archivo de Test | Tests | Descripción |
|----------------|-------|-------------|
| `MutantDetectorTest` | 25 | Algoritmo de detección (todas direcciones, validaciones, borde) |
| `MutantServiceTest` | 6 | Lógica de negocio y sistema de caché |
| `StatsServiceTest` | 9 | Cálculo de estadísticas y ratio |
| `MutantControllerTest` | 13 | Endpoints REST (códigos HTTP, validaciones) |

### Casos Críticos Cubiertos

#### ✅ Detección de Mutantes
- Secuencias horizontales, verticales, diagonales (↘ ↙)
- Múltiples secuencias
- Matrices 4x4 a 100x100
- Validación de caracteres (solo A, T, C, G)
- Matrices cuadradas (NxN)

#### ✅ Sistema de Caché
- Cache hit evita análisis duplicados (optimización)
- Hash único por secuencia de ADN
- Persistencia de resultados

#### ✅ Estadísticas
- Ratio calculado correctamente
- Manejo de división por cero (0 humanos)
- Casos especiales (0 registros, solo mutantes, solo humanos)

#### ✅ Validaciones HTTP
- 200 OK para mutantes
- 403 FORBIDDEN para humanos
- 400 BAD REQUEST para entradas inválidas
- JSON malformado, caracteres inválidos, matriz no cuadrada

---

## 🏗️ Arquitectura

### Capas de la Aplicación

```
┌─────────────────────────────────────┐
│      Controller Layer (REST)        │  ← Endpoints HTTP
├─────────────────────────────────────┤
│       Service Layer (Business)      │  ← Lógica de negocio
├─────────────────────────────────────┤
│    Repository Layer (Data Access)   │  ← Persistencia
├─────────────────────────────────────┤
│          H2 Database (Memory)       │  ← Base de datos
└─────────────────────────────────────┘
```

### Componentes Principales

#### **MutantDetector**
Algoritmo optimizado para detectar secuencias mutantes:
- Early termination (retorna al encontrar 2 secuencias)
- Single pass (recorre matriz una sola vez)
- Complejidad: O(N²) con optimizaciones
- Sin recursión (evita StackOverflow)

#### **MutantService**
Lógica de negocio con sistema de caché:
- Genera hash SHA-256 único por DNA
- Consulta caché antes de analizar
- Persiste resultados para análisis futuros
- Evita análisis duplicados (mejora rendimiento)

#### **StatsService**
Cálculo de estadísticas:
- Conteo de mutantes y humanos
- Cálculo de ratio con manejo de división por cero
- Precisión decimal garantizada

#### **MutantController**
API REST con validaciones:
- Validación automática con Bean Validation
- Códigos HTTP correctos (200, 403, 400)
- Documentación Swagger integrada

---

## ✅ Validaciones

### Validación Custom: @ValidDnaSequence

La aplicación valida que:
- El array no sea null ni vacío
- La matriz sea cuadrada (NxN)
- Solo contenga caracteres válidos: A, T, C, G
- Todas las filas tengan la misma longitud

**Errores comunes:**
- `"DNA sequence must be a non-empty NxN array"` - Matriz vacía o no cuadrada
- `"Carácter inválido encontrado"` - Caracteres diferentes de A, T, C, G

---

## 📈 Optimizaciones Implementadas

### 1. Sistema de Caché
- Cada DNA genera un hash SHA-256 único
- Se consulta la BD antes de analizar
- Si existe, retorna resultado sin procesar
- **Ahorro:** Evita análisis duplicados

### 2. Algoritmo de Detección
- **Early Termination:** Retorna al encontrar 2 secuencias
- **Single Pass:** Recorre la matriz una sola vez
- **Boundary Checking:** Solo busca donde es posible
- **Loop Unrolling:** Verifica 4 posiciones sin bucles

### 3. Base de Datos
- H2 en memoria (ultrarrápida)
- Índice único en `dna_hash`
- Estrategia `create-drop` (desarrollo)

---

## 🔍 Ejemplo de Uso

### DNA Mutante

```json
{
  "dna": [
    "ATGCGA",
    "CAGTGC",
    "TTATGT",
    "AGAAGG",
    "CCCCTA",
    "TCACTG"
  ]
}
```

**Secuencias encontradas:**
- Horizontal en fila 4: `CCCC`
- Diagonal principal: `AGGG`

**Resultado:** `200 OK` (Es mutante)

### DNA Humano

```json
{
  "dna": [
    "ATGCGA",
    "CAGTGC",
    "TTATTT",
    "AGACGG",
    "GCGTCA",
    "TCACTG"
  ]
}
```

**Secuencias encontradas:** 0 (o solo 1)

**Resultado:** `403 FORBIDDEN` (No es mutante)

---

## 🛠️ Configuración

### application.properties

```properties
# Puerto del servidor
server.port=8080

# Base de datos H2
spring.datasource.url=jdbc:h2:mem:mutantdb
spring.datasource.username=sa
spring.datasource.password=

# Consola H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```

---

## 📝 Logs

Los logs están configurados para mostrar:
- `WARN` nivel root (evita ruido)
- `INFO` para la aplicación
- `WARN` para Spring Framework

Para habilitar logs SQL:
```properties
logging.level.org.hibernate.SQL=DEBUG
```

---

## 🚨 Solución de Problemas

### La aplicación no inicia
```bash
# Verificar que Java 17 esté instalado
java -version

# Limpiar y reconstruir
./gradlew clean build
```

### Tests fallan
```bash
# Ejecutar tests con más información
./gradlew test --info

# Ver reporte HTML
# Abrir: build/reports/tests/test/index.html
```

### Puerto 8080 ya en uso
Cambiar el puerto en `application.properties`:
```properties
server.port=8081
```

---

## 📦 Dependencias Principales

```groovy
dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // Base de datos
    runtimeOnly 'com.h2database:h2'
    
    // Documentación
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0'
    
    // Utilidades
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

---

## 👥 Autor

Desarrollado como parte del examen de Mercado Libre - Detector de Mutantes

---

## 📄 Licencia

Este proyecto es de uso académico/educativo.

---

## 🔗 Enlaces Útiles

- [Documentación Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [Documentación Swagger](https://swagger.io/docs/)
- [JaCoCo Coverage](https://www.jacoco.org/jacoco/trunk/doc/)

---

**¡Listo para detectar mutantes! 🧬🔬**

