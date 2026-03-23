# spring-movie-app

En enkel Spring Boot-baserad Movie-webbapp med CRUD, API-import och filtrering/paginering.

## Tech stack
- Spring Boot
- Spring MVC
- Thymeleaf
- Spring Data JPA
- Validation
- PostgreSQL
- Docker

## Requirements
- Java 21
- Maven
- Docker (för PostgreSQL)

## Köra lokalt (steg för steg)
1. Starta databasen i Docker:
```bash
docker compose up -d
```
Detta startar PostgreSQL i en container.

2. Starta applikationen:
```bash
mvn spring-boot:run
```
Appen kör på `http://localhost:8080`.

## Databas (PostgreSQL via Docker)
- Databasen körs via `docker-compose.yml`.
- Data sparas i en named volume så den överlever omstarter.

## TMDb-import (API-nyckel)
- Import triggas via knappen **Importera filmer** på `/movies` (POST `/movies/import`).
- Sätt API-nyckel som miljövariabel:
```bash
set TMDB_API_KEY=din_nyckel
```
På PowerShell:
```powershell
$env:TMDB_API_KEY="din_nyckel"
```

## Funktioner
- CRUD för Movie (list/create/edit/delete)
- Manuell import av startdata från TMDb
- Filtrering/sökning på `title`, `director`, `genre`
- Paginering med `page` och `size`

## Exempel på URL med filter
```
/movies?title=inception&director=nolan&genre=sci-fi&page=0&size=10
```

## Status
Projektet är under utveckling.
