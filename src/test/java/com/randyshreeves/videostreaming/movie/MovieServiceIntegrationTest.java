package com.randyshreeves.videostreaming.movie;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MovieServiceIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void shouldCreateMovieSuccessfully() {
        Movie movie = createTestMovie();
        Movie savedMovie = movieService.createMovie(movie);
        assertNotNull(savedMovie.getId());
        Movie retrievedMovie = movieRepository.findById(savedMovie.getId()).orElseThrow();
        assertEquals(movie.getTitle(), retrievedMovie.getTitle());
        assertEquals(movie.getDescription(), retrievedMovie.getDescription());
        assertEquals(movie.getReleaseYear(), retrievedMovie.getReleaseYear());
        assertEquals(movie.getRuntimeMinutes(), retrievedMovie.getRuntimeMinutes());
        assertEquals(movie.getStorageLocation(), retrievedMovie.getStorageLocation());
    }

    @Test
    void shouldRetrieveMovieById() {
        Movie movie = createTestMovie();
        Movie savedMovie = movieService.createMovie(movie);
        assertNotNull(savedMovie.getId());
        Movie retrievedMovie = movieService.getMovie(savedMovie.getId());
        assertEquals(retrievedMovie, savedMovie);
    }

    @Test
    void shouldUpdateMovieSuccessfully() {
        Movie movie = createTestMovie();
        Movie savedMovie = movieService.createMovie(movie);
        Movie updateRequest = new Movie(
                "Updated Test Movie Title",
                savedMovie.getDescription(),
                savedMovie.getReleaseYear(),
                savedMovie.getRuntimeMinutes(),
                savedMovie.getStorageLocation()
        );
        Movie updatedMovie = movieService.updateMovie(savedMovie.getId(), updateRequest);
        Movie retrievedMovie = movieRepository.findById(updatedMovie.getId()).orElseThrow();
        assertEquals("Updated Test Movie Title", retrievedMovie.getTitle());
        assertEquals(movie.getDescription(), retrievedMovie.getDescription());
        assertEquals(movie.getReleaseYear(), retrievedMovie.getReleaseYear());
        assertEquals(movie.getRuntimeMinutes(), retrievedMovie.getRuntimeMinutes());
        assertEquals(movie.getStorageLocation(), retrievedMovie.getStorageLocation());
    }

    @Test
    void shouldDeleteMovieSuccessfully() {
        Movie movie = createTestMovie();
        Movie savedMovie = movieService.createMovie(movie);
        movieService.deleteMovie(savedMovie.getId());
        assertTrue(movieRepository.findById(savedMovie.getId()).isEmpty());
    }

    private Movie createTestMovie() {
        return new Movie(
                "Test Movie Title",
                "Test Movie Description",
                2009,
                90,
                "Test Movie Storage Location"
        );
    }
}
