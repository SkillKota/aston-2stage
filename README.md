# AstonStudyV2

## Домашнее задание 2: User Service

Консольное Java-приложение для управления пользователями. Приложение использует Hibernate ORM и PostgreSQL без Spring.

## Что используется

- Java 21
- Maven
- Hibernate ORM
- PostgreSQL 16
- Docker Compose
- SLF4J + Logback

Локально устанавливать Maven и PostgreSQL не нужно. Maven используется внутри Docker-образа при сборке приложения, а PostgreSQL запускается в отдельном контейнере.

## Запуск

Собрать Docker-образ приложения:

```bash
docker compose build app
```

Запустить консольное приложение:

```bash
docker compose run --rm app
```

При запуске приложения Docker Compose автоматически поднимает PostgreSQL, если контейнер с базой еще не запущен.

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

- `app` - консольное Java-приложение `user-service`
- `postgres` - база данных PostgreSQL 16

Настройки подключения к базе передаются в приложение через переменные окружения в `docker-compose.yml`:

```text
DB_URL=jdbc:postgresql://postgres:5432/user_service
DB_USER=postgres
DB_PASSWORD=postgres
```

## Консольное меню

```text
1. Создать пользователя
2. Найти пользователя по id
3. Показать всех пользователей
4. Обновить пользователя
5. Удалить пользователя
0. Выход
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
  config/HibernateUtil.java
  console/ConsoleMenu.java
  dao/UserDao.java
  dao/UserDaoImpl.java
  entity/User.java

src/main/resources
  hibernate.cfg.xml
  logback.xml
```
