# Video Streaming
A full-stack video streaming application built with Spring Boot and React. Users can browse a movie catalog, view movie details, and stream video content directly from the browser.

## Features
* Browse a movie catalog
* View movie details
* Stream MP4 video through the browser
* RESTful backend API

## Running Locally

### Backend
* Set the `MEDIA_ROOT` environment variable to the directory containing your video files before starting the backend.
* Set the `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environmental variables to match your own database.

```bash
cd backend
./mvnw spring-boot:run
```
Backend runs on http://localhost:8080

### Frontend
```bash
cd frontend
npm run dev
```
Frontend runs on http://localhost:5173

## API Endpoints
| Method | Endpoint              | Description                    |
|--------|-----------------------|--------------------------------|
| GET    | `/movies`             | Get list of all movies         |
| GET    | `/movies/{id}`        | Get a specific movie's details |
| GET    | `/movies/{id}/stream` | Stream a movie                 |
| POST   | `/movies`             | Add a movie                    |
| PUT    | `/movies/{id}`        | Update a movie                 |
| DELETE | `/movies/{id}`        | Delete a movie                 |

## Tech Stack

### Frontend
* React
* TypeScript
* Vite

### Backend
* Spring Boot
* Java
* PostgreSQL
* JUnit


