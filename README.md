# dostoREAD

dostoREAD is a Spring Boot web application for managing and reading EPUB books online. The project keeps the original Thymeleaf MVC stack and incrementally improves the codebase toward a cleaner DDD-oriented architecture.

## Tech Stack

- Java 21
- Spring Boot 3.4.4
- Spring Web, Spring Security, Spring Data JPA
- Thymeleaf
- PostgreSQL
- MinIO
- Spring Mail
- epublib and jsoup for EPUB/HTML parsing
- Tailwind CSS
- Docker Compose

## Features

- User registration, login, email confirmation and password recovery
- Role-based admin and reader areas
- Admin book upload, update, delete and search
- EPUB file and cover storage in MinIO
- Reader view with font and screen-size options
- Reading progress persistence per user

## Architecture

The project is organized by DDD-oriented layers:

```txt
src/main/java/com/example/demo
  domain/          pure business models, value objects, repository contracts
  application/     use-case/application services, commands and transaction boundaries
  infrastructure/  JPA entities/repositories, MinIO, mail, security, EPUB parsing, config
  presentation/    Thymeleaf controllers, forms, validators and view models
```

Domain classes do not depend on Spring, JPA, HTTP, MinIO or Thymeleaf. JPA entities are kept in `infrastructure.persistence.jpa.entity`, while controllers live in `presentation.web.controller`.

## Tailwind CSS

Bootstrap CDN imports were removed. Tailwind is built locally from:

- `src/main/resources/static/css-src/app.css`
- `tailwind.config.js`

Commands:

```bash
npm install
npm run build:css
npm run watch:css
```

The compiled stylesheet is served as `/css/app.css`.

## Configuration

Configuration is environment-driven. Profiles:

- `local` - default local development profile
- `docker` - Docker Compose service names
- `test` - test profile

Copy `.env.example` to `.env` for Docker usage and replace placeholder secrets.

Important variables:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- `SPRING_PROFILES_ACTIVE`

## Docker Compose

Local stack:

- `app` on port `8080`
- `postgres` on port `5432`
- `minio` API on `9000`, console on `9001`
- `mailpit` SMTP on `1025`, UI on `8025`

Host ports are configurable through `.env`: `APP_PORT`, `POSTGRES_PORT`, `MINIO_API_PORT`, `MINIO_CONSOLE_PORT`, `MAILPIT_SMTP_PORT`, and `MAILPIT_UI_PORT`.

```bash
cp .env.example .env
docker compose up --build
```

Validate compose syntax:

```bash
docker compose config
```

## Local Run Without Docker

Start PostgreSQL and MinIO locally, configure environment variables, then run:

```bash
npm install
npm run build:css
./mvnw spring-boot:run
```

## Tests and Build

```bash
./mvnw test
./mvnw package
```

Focused domain tests cover value object validation for book titles, email and reading progress.

## What Was Improved

- Introduced layered package structure: `domain`, `application`, `infrastructure`, `presentation`
- Moved JPA entities and repositories to infrastructure
- Moved controllers/forms/validators/view models to presentation
- Added domain value objects and repository contracts
- Externalized MinIO, database and mail settings
- Replaced Bootstrap templates with Tailwind-oriented styling
- Added Dockerfile, Docker Compose and `.env.example`
- Updated `.gitignore`
