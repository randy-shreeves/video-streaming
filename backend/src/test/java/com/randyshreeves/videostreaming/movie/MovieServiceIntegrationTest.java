package com.randyshreeves.videostreaming.movie;

import com.randyshreeves.videostreaming.exception.InvalidMediaFileException;
import com.randyshreeves.videostreaming.exception.MediaFileNotFoundException;
import com.randyshreeves.videostreaming.exception.MediaStorageException;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    void shouldReturnAllPublishedMoviesSuccessfully() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        Movie savedMovie = movieRepository.findById(movieId).orElseThrow();
        savedMovie.setPublished(true);
        movieRepository.save(savedMovie);
        List<MovieResponse> movieResponseList = movieService.getAllPublishedMovies();
        assertFalse(movieResponseList.isEmpty());
        MovieResponse movieResponse = movieResponseList.get(0);
        assertEquals(movieResponse.getTitle(), movieRequest.getTitle());
        assertEquals(1, movieResponseList.size());
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
    void shouldReturnPublishedMovieSuccessfully() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        Movie savedMovie = movieRepository.findById(movieId).orElseThrow();
        savedMovie.setPublished(true);
        movieRepository.save(savedMovie);
        MovieResponse movieResponse = movieService.getPublishedMovie(movieId);
        assertEquals(movieResponse.getTitle(), movieRequest.getTitle());
    }

    @Test
    void shouldReturnMovieSuccessfully() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        MovieResponse movieResponse = movieService.getMovie(movieId);
        assertEquals(movieResponse.getTitle(), movieRequest.getTitle());
    }

    @Test
    void shouldUpdateMovieSuccessfully() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        MovieRequest updateRequest = new MovieRequest(
            "Updated Test Movie Title",
            "Updated Test Movie Description",
            9999,
            999
        );
        MovieResponse updatedMovieResponse = movieService.updateMovie(savedMovieResponse.getId(), updateRequest);
        Movie retrievedMovie = movieRepository.findById(updatedMovieResponse.getId()).orElseThrow();
        assertEquals("Updated Test Movie Title", retrievedMovie.getTitle());
        assertEquals("Updated Test Movie Description", retrievedMovie.getDescription());
        assertEquals(9999, retrievedMovie.getReleaseYear());
        assertEquals(999, retrievedMovie.getRuntimeMinutes());
    }

    @Test
    void shouldDeleteMovieSuccessfully() throws IOException {
        MovieRequest movieRequest = new MovieRequest(
                "Test Movie",
                "Test Movie Description",
                2009,
                90
        );
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        MockMultipartFile video = new MockMultipartFile(
                "video",
                "test-video.mp4",
                "video/mp4",
                "fake_mp4_content".getBytes()
        );
        MockMultipartFile poster = new MockMultipartFile(
                "poster",
                "test-poster.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake_jpeg_content".getBytes()
        );
        Path videoPath = null;
        Path posterPath = null;
        try {
            movieService.uploadVideo(movieId, video);
            movieService.uploadPoster(movieId, poster);
            String videoLocation = movieRepository.findById(movieId).orElseThrow().getStorageLocation();
            String posterLocation = movieRepository.findById(movieId).orElseThrow().getPosterLocation();
            videoPath = Paths.get("src/test/resources/media", videoLocation);
            posterPath = Paths.get("src/test/resources/media", posterLocation);
            movieService.deleteMovie(savedMovieResponse.getId());
            assertTrue(movieRepository.findById(savedMovieResponse.getId()).isEmpty());
            assertFalse(Files.exists(videoPath));
            assertFalse(Files.exists(posterPath));
        } catch (Exception e) {
            throw new MediaStorageException("Failed to add movie and/or poster file.");
        } finally {
            if (videoPath != null) {
                Files.deleteIfExists(videoPath);
            }
            if (posterPath != null) {
                Files.deleteIfExists(posterPath);
            }
        }
    }

    @Test
    void shouldReturnPublishedMovieStreamResource() throws Exception {
        MovieRequest request = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(request);
        Long movieId = savedMovieResponse.getId();
        Movie savedMovie = movieRepository.findById(movieId).orElseThrow();
        savedMovie.setStorageLocation("movies/test_movie.mp4");
        savedMovie.setPublished(true);
        movieRepository.save(savedMovie);
        Resource resource = movieService.getMovieStream(movieId);
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void shouldReturnMovieNotFoundExceptionIfAttemptToAccessStreamOfUnpublishedMovie() throws MalformedURLException {
        MovieRequest request = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(request);
        Long movieId = savedMovieResponse.getId();
        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieStream(movieId));
    }

    @Test
    void shouldReturnMovieNotFoundException() throws Exception {
        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieStream(999L));
    }

    @Test
    void shouldReturnMediaFileNotFoundExceptionWhenVideoFileNotFound () throws Exception {
        MovieRequest request = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(request);
        Long movieId = savedMovieResponse.getId();
        Movie savedMovie = movieRepository.findById(movieId).orElseThrow();
        savedMovie.setStorageLocation("movies/test_movie_missing.mp4");
        savedMovie.setPublished(true);
        movieRepository.save(savedMovie);
        MediaFileNotFoundException exception = assertThrows(MediaFileNotFoundException.class, () -> movieService.getMovieStream(savedMovie.getId()));
        assertEquals("Video file not found.", exception.getMessage());
    }

    @Test
    void shouldReturnPublishedPosterResource() throws Exception {
        MovieRequest request = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(request);
        Long movieId = savedMovieResponse.getId();
        Movie savedMovie = movieRepository.findById(movieId).orElseThrow();
        savedMovie.setPosterLocation("/movies/posters/test_poster.jpg");
        savedMovie.setPublished(true);
        movieRepository.save(savedMovie);
        Resource resource = movieService.getPublishedMoviePoster(movieId);
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void shouldReturnPosterResource() throws Exception {
        MovieRequest request = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(request);
        Long movieId = savedMovieResponse.getId();
        Movie savedMovie = movieRepository.findById(movieId).orElseThrow();
        savedMovie.setPosterLocation("/movies/posters/test_poster.jpg");
        movieRepository.save(savedMovie);
        Resource resource = movieService.getMoviePoster(movieId);
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void shouldRejectRequestForPublishedMoviePosterIfMovieIsNotPublished() throws MalformedURLException {
        MovieRequest request = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(request);
        Long movieId = savedMovieResponse.getId();
        assertThrows(MovieNotFoundException.class, () -> movieService.getPublishedMoviePoster(movieId));
    }

    @Test
    void shouldReturnMediaFileNotFoundExceptionWhenPosterFileNotFound() throws Exception {
        MovieRequest request = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(request);
        Long movieId = savedMovieResponse.getId();
        Movie savedMovie = movieRepository.findById(movieId).orElseThrow();
        savedMovie.setPosterLocation("/movies/posters/test_poster_missing.jpg");
        savedMovie.setPublished(true);
        movieRepository.save(savedMovie);
        RuntimeException exception = assertThrows(MediaFileNotFoundException.class, () -> movieService.getPublishedMoviePoster(savedMovie.getId()));
        assertEquals("Movie poster not found.", exception.getMessage());
    }

    @Test
    void shouldUploadVideoFileSuccessfully() throws IOException {
        MovieRequest movieRequest = new MovieRequest(
                "Test Movie",
                "Test Movie Description",
                2009,
                90
        );
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        MockMultipartFile video = new MockMultipartFile(
                "video",
                "test-video.mp4",
                "video/mp4",
                "fake_mp4_content".getBytes()
        );
        Path videoPath = null;
        try {
            movieService.uploadVideo(movieId, video);
            String videoLocation = movieRepository.findById(movieId).orElseThrow().getStorageLocation();
            videoPath = Paths.get("src/test/resources/media", videoLocation);
            assertTrue(Files.exists(videoPath));
        } finally {
            if (videoPath != null) {
                Files.deleteIfExists(videoPath);
            }
        }
    }

    @Test
    void shouldReplaceVideoFileSuccessfullyAndDeleteOldVideoFile() throws IOException {
        MovieRequest movieRequest = new MovieRequest(
                "Test Movie",
                "Test Movie Description",
                2009,
                90
        );
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();

        MockMultipartFile originalVideo = new MockMultipartFile(
                "original-video",
                "original-video.mp4",
                "video/mp4",
                "fake_mp4_content".getBytes()
        );
        Path originalVideoPath = null;

        MockMultipartFile newVideo = new MockMultipartFile(
                "new-video",
                "new-video.mp4",
                "video/mp4",
                "fake_mp4_content".getBytes()
        );
        Path newVideoPath = null;

        try {
            movieService.uploadVideo(movieId, originalVideo);
            String originalVideoLocation = movieRepository.findById(movieId).orElseThrow().getStorageLocation();
            originalVideoPath = Paths.get("src/test/resources/media", originalVideoLocation);
            movieService.uploadVideo(movieId, newVideo);
            String newVideoLocation = movieRepository.findById(movieId).orElseThrow().getStorageLocation();
            newVideoPath = Paths.get("src/test/resources/media", newVideoLocation);
            assertNotEquals(originalVideoLocation, newVideoLocation);
            assertFalse(Files.exists(originalVideoPath));
            assertTrue(Files.exists(newVideoPath));
        } finally {
            if (originalVideoPath != null) {
                Files.deleteIfExists(originalVideoPath);
            }
            if (newVideoPath != null) {
                Files.deleteIfExists(newVideoPath);
            }
        }
    }

    @Test
    void shouldReturnInvalidMediaFileExceptionIfVideoFileIsNotMp4() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        MockMultipartFile video = new MockMultipartFile(
                "video",
                "test-video.mov",
               "video/mov",
                "fake_mov_content".getBytes()
        );
        assertThrows(InvalidMediaFileException.class, () -> movieService.uploadVideo(movieId, video));
    }

    @Test
    void shouldUploadMoviePosterSuccessfully() throws IOException {
        MovieRequest movieRequest = new MovieRequest(
            "Test Movie",
            "Test Movie Description",
            2009,
            90
        );
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        MockMultipartFile poster = new MockMultipartFile(
            "poster",
            "test-poster.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "fake_jpeg_content".getBytes()
        );
        Path posterPath = null;
        try {
            movieService.uploadPoster(movieId, poster);
            String posterLocation = movieRepository.findById(movieId).orElseThrow().getPosterLocation();
            posterPath = Paths.get("src/test/resources/media", posterLocation);
            assertTrue(Files.exists(posterPath));
        } finally {
            if (posterPath != null) {
                Files.deleteIfExists(posterPath);
            }
        }
    }

    @Test
    void shouldReplaceMoviePosterSuccessfullyAndDeleteOldPoster() throws IOException {
        MovieRequest movieRequest = new MovieRequest(
                "Test Movie",
                "Test Movie Description",
                2009,
                90
        );
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();

        MockMultipartFile originalPoster = new MockMultipartFile(
                "original-poster",
                "original-poster.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake_jpeg_content".getBytes()
        );
        Path originalPosterPath = null;

        MockMultipartFile newPoster = new MockMultipartFile(
                "new-poster",
                "new-poster.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake_jpeg_content".getBytes()
        );
        Path newPosterPath = null;

        try {
            movieService.uploadPoster(movieId, originalPoster);
            String originalPosterLocation = movieRepository.findById(movieId).orElseThrow().getPosterLocation();
            originalPosterPath = Paths.get("src/test/resources/media", originalPosterLocation);
            movieService.uploadPoster(movieId, newPoster);
            String newPosterLocation = movieRepository.findById(movieId).orElseThrow().getPosterLocation();
            newPosterPath = Paths.get("src/test/resources/media", newPosterLocation);
            assertNotEquals(originalPosterLocation, newPosterLocation);
            assertFalse(Files.exists(originalPosterPath));
            assertTrue(Files.exists(newPosterPath));
        } finally {
            if (originalPosterPath != null) {
                Files.deleteIfExists(originalPosterPath);
            }
            if (newPosterPath != null) {
                Files.deleteIfExists(newPosterPath);
            }
        }
    }

    @Test
    void shouldReturnInvalidMediaFileExceptionIfNoFileIsProvided() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        MockMultipartFile poster = new MockMultipartFile(
                "poster",
                "test-poster.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );
        assertThrows(InvalidMediaFileException.class, () -> movieService.uploadPoster(movieId, poster));
    }

    @Test
    void shouldReturnInvalidMediaFileExceptionIfFileIsNotJpeg() {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        MockMultipartFile poster = new MockMultipartFile(
                "poster",
                "test-poster.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake_png_content".getBytes()
        );
        assertThrows(InvalidMediaFileException.class, () -> movieService.uploadPoster(movieId, poster));
    }

    @Test
    void shouldPublishMovieIfVideoAndPosterExist() {
        MovieRequest movieRequest = new MovieRequest(
                "Test Movie",
                "Test Movie Description",
                2009,
                90
        );
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        Movie savedMovie = movieRepository.findById(movieId).orElseThrow();
        savedMovie.setStorageLocation("/movies/video_file.mp4");
        savedMovie.setPosterLocation("/movies/posters/poster_file.jpg");
        movieRepository.save(savedMovie);
        MovieResponse movieResponse = movieService.publishMovie(movieId);
        assertTrue(movieResponse.isPublished());
    }

    @Test
    void shouldUnpublishMovie() {
        MovieRequest movieRequest = new MovieRequest(
                "Test Movie",
                "Test Movie Description",
                2009,
                90
        );
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        Movie savedMovie = movieRepository.findById(movieId).orElseThrow();
        savedMovie.setStorageLocation("/movies/video_file.mp4");
        savedMovie.setPosterLocation("/movies/posters/poster_file.jpg");
        movieRepository.save(savedMovie);
        MovieResponse publishedMovieResponse = movieService.publishMovie(movieId);
        assertTrue(publishedMovieResponse.isPublished());
        MovieResponse unpublishedMovieResponse = movieService.unpublishMovie(movieId);
        assertFalse(unpublishedMovieResponse.isPublished());
    }

    @Test
    void shouldNotPublishMovieIfVideoOrPosterIsNull() {
        MovieRequest movieRequest = new MovieRequest(
                "Test Movie",
                "Test Movie Description",
                2009,
                90
        );
        MovieResponse savedMovieResponse = movieService.createMovie(movieRequest);
        Long movieId = savedMovieResponse.getId();
        assertThrows(IllegalStateException.class, () -> movieService.publishMovie(movieId));
    }

    private MovieRequest createTestMovieRequest() {
        return new MovieRequest(
            "Test Movie",
            "Test Movie Description",
            2009,
            90
        );
    }
}
