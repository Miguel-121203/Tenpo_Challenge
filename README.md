# Tenpo Challenge - API REST

API REST desarrollada en Spring Boot (Java 21) que implementa un servicio de cálculo con porcentaje dinámico y registro de historial de llamadas.

## Funcionalidades

- **Cálculo con porcentaje dinámico**: Endpoint que suma dos números y aplica un porcentaje obtenido de un servicio externo (mock)
- **Caché de porcentaje**: El porcentaje se almacena en memoria por 30 minutos. Si el servicio externo falla, se usa el valor cacheado
- **Historial de llamadas**: Registro asíncrono de todas las llamadas con paginación

## Requisitos Previos

- Docker y Docker Compose
- (Opcional) Java 21 y Maven para desarrollo local

## Ejecución con Docker

```bash
# Clonar el repositorio
git clone https://github.com/Miguel-121203/Tenpo_Challenge.git
cd Tenpo

# Construir y ejecutar
docker-compose up --build

# La API estará disponible en http://localhost:8080
```

## Endpoints

### Cálculo con porcentaje

```bash
POST /api/calculate
Content-Type: application/json

{
  "num1": 5,
  "num2": 5
}
```

Respuesta:
```json
{
  "result": 10.5,
  "percentage": 5.0,
  "originalSum": 10.0
}
```

### Historial de llamadas

```bash
GET /api/history?page=0&size=10
```

Respuesta paginada con el historial de llamadas incluyendo:
- Id
- Fecha y hora
- Endpoint
- Parámetros
- Respuesta o error

## Documentación API (Swagger)

Una vez ejecutando la aplicación, acceder a:
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## Ejemplos con cURL

```bash
# Realizar un cálculo
curl -X POST http://localhost:8080/api/calculate \
  -H "Content-Type: application/json" \
  -d '{"num1": 5, "num2": 5}'

# Consultar historial
curl "http://localhost:8080/api/history?page=0&size=10"
```

## Configuración

Las siguientes variables de entorno pueden configurarse:

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `DB_HOST` | Host de PostgreSQL | localhost |
| `DB_PORT` | Puerto de PostgreSQL | 5432 |
| `DB_NAME` | Nombre de la base de datos | tenpo |
| `DB_USER` | Usuario de la base de datos | tenpo |
| `DB_PASSWORD` | Contraseña de la base de datos | tenpo123 |

El porcentaje del servicio externo (mock) es configurable en `application.yaml`:
- `external.percentage.value`: Valor del porcentaje (default: 5)
- `external.percentage.failure-rate`: Probabilidad de fallo del mock (default: 0.3)

## Ejecutar Tests

```bash
# Con Maven
./mvnw test

# Con Docker (los tests se ejecutan durante el build)
docker-compose build
```

## Arquitectura

```
src/main/java/com/example/Tenpo/
├── TenpoApplication.java
│
├── domain/                          # Núcleo de negocio
│   ├── model/
│   │   ├── Calculation.java
│   │   └── CallHistory.java
│   ├── port/
│   │   ├── input/
│   │   │   ├── CalculationUseCase.java
│   │   │   └── CallHistoryUseCase.java
│   │   └── output/
│   │       ├── CallHistoryRepositoryPort.java
│   │       ├── PercentageCachePort.java
│   │       └── PercentageProviderPort.java
│   └── exception/
│       ├── ExternalServiceException.java
│       └── PercentageNotAvailableException.java
│
├── application/                     # Capa de aplicación
│   ├── service/
│   │   ├── CalculationServiceImpl.java
│   │   ├── CallHistoryServiceImpl.java
│   │   └── PercentageService.java
│   ├── dto/
│   │   ├── CalculationRequest.java
│   │   ├── CalculationResponse.java
│   │   ├── CallHistoryResponse.java
│   │   ├── ErrorResponse.java
│   │   └── PageResponse.java
│   └── constants/
│       └── LogMessages.java
│
└── infraestructure/                 # Capa de infraestructura
    ├── adapter/
    │   ├── input/rest/
    │   │   ├── CalculationController.java
    │   │   ├── HistoryController.java
    │   │   └── GlobalExceptionHandler.java
    │   └── output/
    │       ├── persistence/
    │       │   ├── CallHistoryRepositoryAdapter.java
    │       │   ├── entity/
    │       │   │   └── CallHistoryEntity.java
    │       │   ├── mapper/
    │       │   │   └── CallHistoryMapper.java
    │       │   └── repository/
    │       │       └── JpaCallHistoryRepository.java
    │       ├── cache/
    │       │   └── PercentageCacheAdapter.java
    │       └── external/
    │           └── ExternalPercentageAdapter.java
    └── config/
        ├── AsyncConfig.java
        ├── CacheConfig.java
        ├── JacksonConfig.java
        └── OpenApiConfig.java

```

## Tecnologías

- Java 21
- Spring Boot 3.4.1
- PostgreSQL 16
- Caffeine Cache
- SpringDoc OpenAPI (Swagger)
- Docker & Docker Compose
- JUnit 5 & Mockito

## Notas Técnicas

- El registro de historial es **asíncrono** para no afectar el rendimiento
- El caché usa **Caffeine** con TTL de 30 minutos
- El servicio externo es un **mock** con probabilidad de fallo configurable para demostrar el comportamiento de fallback
