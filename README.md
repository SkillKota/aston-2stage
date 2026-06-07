# AstonStudyV2

## Домашнее задание 4: User Service API

Spring Boot REST API для управления пользователями. Приложение использует Spring Web, Spring Data JPA и PostgreSQL.

## Что используется

- Java 21
- Maven
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL 16
- Docker Compose
- JUnit 5
- Mockito

Локально устанавливать Maven и PostgreSQL не нужно. Maven используется внутри Docker-образа при сборке приложения, а PostgreSQL запускается в отдельном контейнере.

## Запуск

Собрать Docker-образ приложения:

```bash
docker compose build app
```

Запустить приложение:

```bash
docker compose up app
```

API будет доступно на `http://localhost:8080/api/users`.

## API

```text
GET    /api/users
GET    /api/users/{id}
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}
```

Тело запроса для создания и обновления:

```json
{
  "name": "Иван",
  "email": "ivan@example.com",
  "age": 25
}
```

Контроллер возвращает DTO, entity наружу не отдается.

## Тесты

Запустить unit- и integration-тесты:

```bash
docker compose run --rm tests
```

API-тесты используют MockMvc. Тесты service-слоя используют Mockito и не подключаются к базе данных.

## Остановка

Остановить контейнеры:

```bash
docker compose down
```

Остановить контейнеры и удалить данные PostgreSQL:

```bash
docker compose down -v
```

## Контейнеры

- `app` - Spring Boot приложение `user-service`
- `postgres` - база данных PostgreSQL 16
- `tests` - запуск тестов через Maven

Настройки подключения к базе передаются в приложение через переменные окружения в `docker-compose.yml`:

```text
DB_URL=jdbc:postgresql://postgres:5432/user_service
DB_USER=postgres
DB_PASSWORD=postgres
```

## Сущность User

Пользователь содержит поля:

```text
id
name
email
age
created_at
```

Поле `created_at` заполняется автоматически при создании пользователя.

## Структура проекта

```text
src/main/java/homework2
  Main.java
  controller/UserController.java
  controller/GlobalExceptionHandler.java
  dto/UserRequestDto.java
  dto/UserResponseDto.java
  entity/User.java
  mapper/UserMapper.java
  repository/UserRepository.java
  service/UserService.java
  service/UserServiceImpl.java

src/main/resources
  application.properties
  logback.xml

src/test/java/homework2
  controller/UserControllerTest.java
  service/UserServiceImplTest.java
```
