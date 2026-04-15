# Auth Service — Hexagonal Architecture + Java 21

Microservicio de autenticación con **Arquitectura Hexagonal**, **CQRS**,
**Virtual Threads (Java 21)** y **Clean Code**.

---

## Estructura del proyecto

```
auth-service/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
│
└── src/
    ├── main/
    │   ├── java/com/auth/
    │   │   │
    │   │   ├── domain/                  ← Núcleo — NO depende de nada externo
    │   │   │   ├── model/               │  User.java, Role.java (entidades y VOs)
    │   │   │   ├── repository/          │  UserRepository.java (interface/puerto)
    │   │   │   ├── event/               │  UserLoggedInEvent.java
    │   │   │   └── exception/           │  UserNotFoundException.java, etc.
    │   │   │
    │   │   ├── application/             ← Casos de uso — solo depende del Domain
    │   │   │   ├── usecase/             │  LoginUseCase.java, RegisterUseCase.java
    │   │   │   ├── command/             │  LoginCommand.java (CQRS - escritura)
    │   │   │   └── port/               │  TokenService.java, EventPublisher.java
    │   │   │
    │   │   └── infrastructure/          ← Adaptadores — implementan los puertos
    │   │       ├── adapter/
    │   │       │   ├── web/             │  AuthController.java, DTOs, mappers
    │   │       │   ├── persistence/     │  UserJpaRepository.java, UserEntity.java
    │   │       │   ├── security/        │  JwtProvider.java, SecurityConfig.java
    │   │       │   └── messaging/       │  KafkaEventPublisher.java
    │   │       └── config/              │  BeanConfig.java, KafkaConfig.java
    │   │
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/
    │           └── V1__init_schema.sql
    │
    └── test/
        └── java/com/auth/
            ├── domain/                  ← Tests unitarios del dominio (sin Spring)
            ├── application/             ← Tests unitarios de use cases (con mocks)
            └── infrastructure/          ← Tests de integración (Testcontainers)
```

---

## Levantar el proyecto

### 1. Prerrequisitos

- Java 21
- Maven 3.9+
- Docker + Docker Compose

### 2. Levantar la infraestructura

```bash
# Solo bases de datos y servicios de soporte (sin el app)
docker-compose up postgres redis kafka zookeeper -d
```

### 3. Ejecutar la app en local (desarrollo)

```bash
cd auth-service
mvn spring-boot:run
```

### 4. O levantar todo con Docker

```bash
docker-compose up --build
```

---

## Endpoints disponibles

| Método | Endpoint              | Descripción              |
|--------|-----------------------|--------------------------|
| POST   | `/api/v1/auth/login`  | Login → devuelve JWT     |
| POST   | `/api/v1/auth/register` | Registro de usuario    |
| POST   | `/api/v1/auth/refresh`  | Renovar access token   |
| POST   | `/api/v1/auth/logout`   | Revocar refresh token  |
| GET    | `/actuator/health`    | Health check             |

---

## Patrones aplicados

| Patrón              | Dónde                            | Por qué                              |
|---------------------|----------------------------------|--------------------------------------|
| Hexagonal           | Toda la arquitectura             | Desacoplar dominio de frameworks     |
| CQRS                | `LoginCommand`, `RegisterCommand`| Separar escritura de lectura         |
| Repository          | `UserRepository` (puerto)        | Abstraer acceso a datos              |
| Strategy            | `TokenService` (puerto)          | Intercambiar impl de JWT libremente  |
| Observer            | `EventPublisher` → Kafka         | Desacoplar eventos de negocio        |
| SOLID — DIP         | Puertos e interfaces en Domain   | Alta capa no depende de baja capa    |
| SOLID — SRP         | Una responsabilidad por clase    | Cada clase hace una sola cosa        |
| SOLID — OCP         | Usar interfaces, no concreciones | Extender sin modificar               |
"# Login" 
