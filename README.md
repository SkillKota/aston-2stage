# AstonStudyV2

## Домашнее задание 6: Swagger и HATEOAS

Проект переведен в multi-module Maven и содержит два Spring Boot микросервиса:

- `user-service` - REST API для управления пользователями, PostgreSQL, Kafka producer, Swagger UI, HATEOAS.
- `notification-service` - Kafka consumer и REST API для отправки email-уведомлений.

При создании или удалении пользователя `user-service` отправляет событие в Kafka с операцией и email. `notification-service` получает событие и отправляет письмо пользователю.

## Что используется

- Java 21
- Maven
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Kafka
- Spring Mail
- Spring HATEOAS
- Springdoc OpenAPI
- PostgreSQL 16
- Kafka
- Docker Compose
- MockMvc
- GreenMail

Локально устанавливать Maven, PostgreSQL, Kafka или SMTP-сервер не нужно. Все запускается через Docker.

## Запуск

Собрать Docker-образы:

```bash
docker compose build user-service notification-service
```

Запустить инфраструктуру и оба сервиса:

```bash
docker compose up postgres kafka mailpit user-service notification-service
```

Сервисы будут доступны:

```text
user-service:         http://localhost:8080
notification-service: http://localhost:8081
Mailpit UI:           http://localhost:8025
Swagger UI:           http://localhost:8080/swagger-ui/index.html
```

## User API

```text
GET    /api/users
GET    /api/users/{id}
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}
```

Swagger-документация доступна по адресу:

```text
http://localhost:8080/swagger-ui/index.html
```

Тело запроса для создания и обновления:

```json
{
  "name": "Иван",
  "email": "ivan@example.com",
  "age": 25
}
```

Ответы `user-service` содержат HATEOAS-ссылки в поле `_links`. Пример ответа одиночного пользователя:

```json
{
  "id": 1,
  "name": "Иван",
  "email": "ivan@example.com",
  "age": 25,
  "createdAt": "2026-06-21T10:00:00",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/users/1"
    },
    "users": {
      "href": "http://localhost:8080/api/users"
    },
    "update": {
      "href": "http://localhost:8080/api/users/1"
    },
    "delete": {
      "href": "http://localhost:8080/api/users/1"
    }
  }
}
```

При `POST /api/users` отправляется Kafka-событие:

```json
{
  "operation": "CREATED",
  "email": "ivan@example.com"
}
```

При `DELETE /api/users/{id}` отправляется Kafka-событие:

```json
{
  "operation": "DELETED",
  "email": "ivan@example.com"
}
```

## Notification API

Ручная отправка email без Kafka:

```text
POST /api/notifications/email
```

Тело запроса:

```json
{
  "email": "ivan@example.com",
  "operation": "CREATED"
}
```

Для `CREATED` отправляется текст:

```text
Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.
```

Для `DELETED` отправляется текст:

```text
Здравствуйте! Ваш аккаунт был удалён.
```

Письма в Docker окружении можно смотреть в Mailpit: `http://localhost:8025`.

## Быстрая Проверка

Создать пользователя:

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Иван","email":"ivan@example.com","age":25}'
```

Удалить пользователя:

```bash
curl -X DELETE http://localhost:8080/api/users/1
```

Отправить письмо напрямую через `notification-service`:

```bash
curl -X POST http://localhost:8081/api/notifications/email \
  -H "Content-Type: application/json" \
  -d '{"email":"ivan@example.com","operation":"CREATED"}'
```

## Тесты

Запустить все тесты:

```bash
docker compose run --rm tests mvn test
```

В `notification-service` интеграционные тесты используют GreenMail: письмо отправляется через реальный `JavaMailSender` в локальный тестовый SMTP-сервер, а тест проверяет получателя и текст.

## Остановка

Остановить контейнеры:

```bash
docker compose down
```

Остановить контейнеры и удалить данные PostgreSQL/Kafka:

```bash
docker compose down -v
```

## Структура Проекта

```text
pom.xml
docker-compose.yml

user-service/
  pom.xml
  Dockerfile
  src/main/java/homework2

notification-service/
  pom.xml
  Dockerfile
  src/main/java/notification
```
