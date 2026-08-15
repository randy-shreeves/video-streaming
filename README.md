# Video Streaming

A full-stack video streaming application built with Spring Boot and React. Users can browse a movie catalog, view movie details, and stream video content directly from the browser. Administrators can manage the movie catalog through a protected admin interface.

## Features

- Browse movie catalog and view movie details
- Stream MP4 video through the browser
- JWT-based authentication
- Role-based authorization for administrative operations
- Admin interface for creating, editing, and deleting movies
- RESTful backend API
- PostgreSQL database with Flyway migrations
- Unit and integration testing

## Tech Stack

**Frontend**
- React
- TypeScript

**Backend**
- Java
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security
- PostgreSQL
- Flyway
- JUnit

## Architecture

The backend follows a layered architecture:

`Controller → Service → Repository → PostgreSQL`

The frontend communicates with the backend through a REST API.

## Running Locally

### Backend

Set the following environment variables before starting the backend:

- `MEDIA_ROOT` — directory containing video and poster files
- `DB_URL` — PostgreSQL database URL
- `DB_USERNAME` — PostgreSQL username
- `DB_PASSWORD` — PostgreSQL password

```bash
cd backend
./mvnw spring-boot:run
```
Backend runs on http://localhost:8080.

### Frontend
```bash
cd frontend
npm run dev
```
Frontend runs on http://localhost:5173.