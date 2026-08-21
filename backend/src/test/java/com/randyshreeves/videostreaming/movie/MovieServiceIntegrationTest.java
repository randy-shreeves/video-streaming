package com.randyshreeves.videostreaming.movie;

import com.randyshreeves.videostreaming.exception.MediaFileNotFoundException;
import com.randyshreeves.videostreaming.exception.MovieNotFoundException;
import com.randyshreeves.videostreaming.movie.dto.MovieRequest;
import com.randyshreeves.videostreaming.movie.dto.MovieResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"media.root=src/test/resources/media"})
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
        assertEquals(movieRequest.getPosterLocation(), retrievedMovie.getPosterLocation());
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
        void shouldReturnAllMoviesSuccessfully() {
        MovieRequest movieRequest = createTestMovieRequest();
        movieService.createMovie(movieRequest);
        List<MovieResponse> movieResponseList = movieService.getAllMovies();
        assertFalse(movieResponseList.isEmpty());
        MovieResponse movieResponse = movieResponseList.get(0);
        assertEquals(movieResponse.getTitle(), movieRequest.getTitle());
        assertEquals(1, movieResponseList.size());
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
                "Updated Test Storage Location",
                "Updated Test Poster Location"
        );
        MovieResponse updatedMovieResponse = movieService.updateMovie(savedMovieResponse.getId(), updateRequest);
        Movie retrievedMovie = movieRepository.findById(updatedMovieResponse.getId()).orElseThrow();
        assertEquals("Updated Test Movie Title", retrievedMovie.getTitle());
        assertEquals("Updated Test Movie Description", retrievedMovie.getDescription());
        assertEquals(9999, retrievedMovie.getReleaseYear());
        assertEquals(999, retrievedMovie.getRuntimeMinutes());
        assertEquals("Updated Test Storage Location", retrievedMovie.getStorageLocation());
        assertEquals("Updated Test Poster Location", retrievedMovie.getPosterLocation());
    }

    @Test
    void shouldDeleteMovieSuccessfully() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        movieService.deleteMovie(savedMovieResponse.getId());
        assertTrue(movieRepository.findById(savedMovieResponse.getId()).isEmpty());
    }

    @Test
    void shouldReturnMovieStreamResource() throws Exception {
        MovieRequest request = createTestMovieRequest();
        MovieResponse savedMovie = movieService.createMovie(request);
        Resource resource = movieService.getMovieStream(savedMovie.getId());
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void shouldReturnMovieNotFoundException() throws Exception {
        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieStream(999L));
    }

    @Test
    void shouldReturnRuntimeExceptionWhenVideoFileNotFound () throws Exception {
        MovieRequest request = createTestMovieRequest();
        request.setStorageLocation("missing_movie_file.mp4");
        MovieResponse savedMovie = movieService.createMovie(request);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieService.getMovieStream(savedMovie.getId()));
        assertEquals("Video file not found.", exception.getMessage());
    }

    @Test
    void shouldReturnPosterStreamResource() throws Exception {
        MovieRequest request = createTestMovieRequest();
        MovieResponse savedMovie = movieService.createMovie(request);
        Resource resource = movieService.getMoviePoster(savedMovie.getId());
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void shouldReturnRuntimeExceptionWhenPosterFileNotFound() throws Exception {
        MovieRequest request = createTestMovieRequest();
        request.setPosterLocation("missing_poster_file.jpg");
        MovieResponse savedMovie = movieService.createMovie(request);
        RuntimeException exception = assertThrows(MediaFileNotFoundException.class, () -> movieService.getMoviePoster(savedMovie.getId()));
        assertEquals("Movie poster not found.", exception.getMessage());
    }

    @Test
    void shouldUploadMoviePosterSuccessfully() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        MockMultipartFile poster = new MockMultipartFile(
                "poster",
                "test-poster.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake_jpeg_content".getBytes()
        );
        movieService.uploadPoster(movieId, poster);
        Movie updatedMovie = movieRepository.findById(movieId).orElseThrow();
        assertEquals("movies/posters/" + movieId + ".jpg", updatedMovie.getPosterLocation());
    }

    private MovieRequest createTestMovieRequest() {
        return new MovieRequest(
                "Test Movie",
                "Test Movie Description",
                2009,
                90,
                "/movies/test_movie.mp4",
                "/movies/posters/test_poster.jpg"
        );
    }
}
