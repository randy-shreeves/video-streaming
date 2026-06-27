package com.randyshreeves.videostreaming.movie;

import com.randyshreeves.videostreaming.movie.dto.MovieRequest;
import com.randyshreeves.videostreaming.movie.dto.MovieResponse;
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
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        assertNotNull(savedMovieResponse.getId());
        Movie retrievedMovie = movieRepository.findById(savedMovieResponse.getId()).orElseThrow();
        assertEquals(movieRequest.getTitle(), retrievedMovie.getTitle());
        assertEquals(movieRequest.getDescription(), retrievedMovie.getDescription());
        assertEquals(movieRequest.getReleaseYear(), retrievedMovie.getReleaseYear());
        assertEquals(movieRequest.getRuntimeMinutes(), retrievedMovie.getRuntimeMinutes());
        assertEquals(movieRequest.getStorageLocation(), retrievedMovie.getStorageLocation());
    }

    @Test
    void shouldRetrieveMovieById() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        assertNotNull(savedMovieResponse.getId());
        MovieResponse retrievedMovieResponse = movieService.getMovie(savedMovieResponse.getId());
        assertEquals(savedMovieResponse.getId(), retrievedMovieResponse.getId());
        assertEquals(savedMovieResponse.getTitle(), retrievedMovieResponse.getTitle());
        assertEquals(savedMovieResponse.getDescription(), retrievedMovieResponse.getDescription());
        assertEquals(savedMovieResponse.getReleaseYear(), retrievedMovieResponse.getReleaseYear());
        assertEquals(savedMovieResponse.getRuntimeMinutes(), retrievedMovieResponse.getRuntimeMinutes());
    }

    @Test
    void shouldUpdateMovieSuccessfully() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        MovieRequest updateRequest = new MovieRequest(
                "Updated Test Movie Title",
                "Updated Test Movie Description",
                9999,
                999,
                "Updated Test Storage Location"
        );
        MovieResponse updatedMovieResponse = movieService.updateMovie(savedMovieResponse.getId(), updateRequest);
        Movie retrievedMovie = movieRepository.findById(updatedMovieResponse.getId()).orElseThrow();
        assertEquals("Updated Test Movie Title", retrievedMovie.getTitle());
        assertEquals("Updated Test Movie Description", retrievedMovie.getDescription());
        assertEquals(9999, retrievedMovie.getReleaseYear());
        assertEquals(999, retrievedMovie.getRuntimeMinutes());
        assertEquals("Updated Test Storage Location", retrievedMovie.getStorageLocation());
    }

    @Test
    void shouldDeleteMovieSuccessfully() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        movieService.deleteMovie(savedMovieResponse.getId());
        assertTrue(movieRepository.findById(savedMovieResponse.getId()).isEmpty());
    }

    private MovieRequest createTestMovieRequest() {
        return new MovieRequest(
                "Test Movie Title",
                "Test Movie Description",
                2009,
                90,
                "Test Movie Storage Location"
        );
    }
}
