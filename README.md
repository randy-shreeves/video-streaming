# Video Streaming

A full-stack video streaming application built with Spring Boot and React. Authenticated users can browse a movie catalog, view movie details, and stream video content directly from the browser. Administrators can manage the movie catalog through a protected admin interface.

## Features

- Browse movie catalog and view movie details
- Stream MP4 video through the browser
- JWT-based authentication
- Role-based authorization for administrative operations
- Protected access to media resources
- Admin interface for creating, editing, uploading, and deleting movies
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

- `MEDIA_ROOT` — Directory containing video and poster files (must contain a /movies/posters/ directory)
- `DB_URL` — PostgreSQL database URL
- `DB_USERNAME` — PostgreSQL username
- `DB_PASSWORD` — PostgreSQL password
- `JWT_SECRET` — Base64-encoded secret used to sign JWTs
- `JWT_EXPIRATION` — Authentication JWT lifetime in milliseconds
- `STREAM_TOKEN_SECRET` — Base64-encoded secret used to sign stream URL tokens
- `STREAM_TOKEN_EXPIRATION` — Stream token lifetime in milliseconds

```bash
cd backend
./mvnw spring-boot:run
```
Backend runs on http://localhost:8080.

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on http://localhost:5173.