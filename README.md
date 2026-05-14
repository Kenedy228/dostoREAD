# dostoREAD

dostoREAD — это веб-приложение на Spring Boot для управления и онлайн-чтения EPUB-книг. Бэкенд сохраняет стек Spring Boot и предоставляет JSON API, а клиентская часть реализована как SPA на Svelte с использованием Tailwind CSS.

Технологический стек
- Java 21
- Spring Boot 3.4.4
- Spring Web, Spring Security, Spring Data JPA
- Svelte, Vite и Tailwind CSS
- PostgreSQL
- MinIO
- Spring Mail
- epublib и jsoup для парсинга EPUB/HTML
- Tailwind CSS
- Docker Compose

## Возможности
- Регистрация пользователей, вход, подтверждение email и восстановление пароля
- Разделение на роли: администратор и читатель
- Загрузка, обновление, удаление и поиск книг администратором
- Хранение EPUB-файлов и обложек в MinIO
- Интерфейс чтения с настройками шрифта и размера экрана
- Сохранение прогресса чтения для каждого пользователя
- Архитектура

## Проект организован по слоям в стиле DDD:

```bash
src/main/java/com/example/demo
domain/          чистые бизнес-модели, value objects, контракты репозиториев
application/     use-case/сервисы приложения, команды и границы транзакций
infrastructure/  JPA-сущности/репозитории, MinIO, почта, безопасность, парсинг EPUB, конфигурация
presentation/    REST API контроллеры, SPA fallback, формы, валидаторы и view-модели
```

Классы домена не зависят от Spring, JPA, HTTP, MinIO или фронтенда. JPA-сущности находятся в infrastructure.persistence.jpa.entity, а API-контроллеры — в presentation.web.api.

## Svelte клиент

Старые шаблоны Thymeleaf были заменены на SPA на Svelte/Vite. Исходники клиента находятся в:

```bash
src/main/frontend
src/main/frontend/src/components
src/main/frontend/src/pages
```

## Продакшн-сборка размещается в:

```bash
src/main/resources/static/index.html
src/main/resources/static/assets
```

## Tailwind CSS подключается через Vite из:
```bash
src/main/frontend/src/styles.css
tailwind.config.js
```

## Команды:

```bash
npm install
npm run build
npm run build:css
npm run watch:css
```

Скрипт build:css оставлен для совместимости и выполняет production-сборку через Vite.

## Конфигурация

Конфигурация задаётся через переменные окружения. Профили:

```bash
local — локальная разработка по умолчанию
docker — для Docker Compose
test — для тестов
```

## Основные переменные:

```bash
DB_URL, DB_USERNAME, DB_PASSWORD
MINIO_ENDPOINT, MINIO_ACCESS_KEY, MINIO_SECRET_KEY
MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD
ADMIN_BOOTSTRAP_ENABLED, ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD
SPRING_SERVLET_MULTIPART_RESOLVE_LAZILY
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE, SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE
SPRING_PROFILES_ACTIVE
```

При запуске приложение создаёт администратора, если ADMIN_BOOTSTRAP_ENABLED=true и пользователь с именем ADMIN_USERNAME отсутствует. Перед использованием в общей среде обязательно измените ADMIN_PASSWORD.

Ограничения загрузки:

- книги — до 20 МБ
- обложки — до 10 МБ

Клиент отображает эти ограничения рядом с полями загрузки и показывает понятное сообщение об ошибке при превышении лимита.

Docker Compose

Локальный стек:

```bash
app на порту 8080
postgres на порту 5432
minio API на 9000, консоль на 9001
mailpit SMTP на 1025, UI на 8025
```

Порты можно настроить через .env:

```bash
APP_PORT
POSTGRES_PORT
MINIO_API_PORT
MINIO_CONSOLE_PORT
MAILPIT_SMTP_PORT
MAILPIT_UI_PORT
```

## Запуск:

```bash
cp .env.example .env
docker compose up --build
```

Стандартные учётные данные администратора (если не переопределены):

```bash
username: admin
password: Admin123!
```

## Локальный запуск без Docker

Запустите PostgreSQL и MinIO локально, настройте переменные окружения, затем:

```bash
npm install
npm run build
./mvnw spring-boot:run
```

Тесты и сборка
```bash
./mvnw test
./mvnw package
```

Фокусные тесты домена покрывают валидацию value objects: названия книг, email и прогресса чтения.