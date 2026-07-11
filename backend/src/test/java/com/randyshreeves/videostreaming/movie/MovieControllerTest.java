package com.randyshreeves.videostreaming.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.randyshreeves.videostreaming.exception.MovieNotFoundException;
import com.randyshreeves.videostreaming.movie.dto.MovieRequest;
import com.randyshreeves.videostreaming.movie.dto.MovieResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieService movieService;

    @Test
    void shouldReturnAllMoviesSuccessfully() throws Exception {
        MovieResponse movieResponse1 = createTestMovieResponse();
        MovieResponse movieResponse2 = new MovieResponse(
                2L,
                "Another Test Movie",
                "TestMovieDescription",
                2009,
                90
        );
        List<MovieResponse> movieResponseList = new ArrayList<>();
        movieResponseList.add(movieResponse1);
        movieResponseList.add(movieResponse2);
        when(movieService.getAllMovies()).thenReturn(movieResponseList);
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Movie Title"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Another Test Movie"));
        verify(movieService).getAllMovies();
    }

    @Test
    void shouldReturnOneMovieSuccessfully() throws Exception {
        MovieResponse movieResponse = createTestMovieResponse();
        Long movieId = movieResponse.getId();
        when(movieService.getMovie(movieId)).thenReturn(movieResponse);
        mockMvc.perform(get("/movies/{id}", movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Movie Title"));
        verify(movieService).getMovie(movieId);
    }

    @Test
    void shouldCreateMovieSuccessfully() throws Exception {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse movieResponse = createTestMovieResponse();
        when(movieService.createMovie(any(MovieRequest.class))).thenReturn(movieResponse);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Movie Title"));
        verify(movieService).createMovie(any(MovieRequest.class));
    }

    @Test
    void shouldUpdateMovieSuccessfully() throws Exception {
        MovieRequest movieRequest = createTestMovieRequest();
        MovieResponse movieResponse = createTestMovieResponse();
        Long movieId = movieResponse.getId();
        when(movieService.updateMovie(eq(movieId), any(MovieRequest.class))).thenReturn(movieResponse);
        mockMvc.perform(put("/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Movie Title"));
        verify(movieService).updateMovie(eq(movieId), any(MovieRequest.class));
    }

    @Test
    void shouldDeleteMovieSuccessfully() throws Exception {
        Long movieId = 1L;
        mockMvc.perform(delete("/movies/{id}", movieId))
                .andExpect(status().isOk());
        verify(movieService).deleteMovie(movieId);
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        MovieRequest movieRequest = createTestMovieRequest();
        movieRequest.setTitle("");
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title cannot be blank."));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsTooLong() throws Exception {
        MovieRequest movieRequest = createTestMovieRequest();
        movieRequest.setTitle("A".repeat(256));
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title cannot be greater than 255 characters."));
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionIsTooLong() throws Exception {
        MovieRequest movieRequest = createTestMovieRequest();
        movieRequest.setDescription("A".repeat(1001));
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Description cannot be greater than 1000 characters."));
    }

    @Test
    void shouldReturnBadRequestWhenReleaseYearIsMissing() throws Exception {
        String json = """
                    {
                      "title": "Test Movie",
                      "description": "Description",
                      "runtimeMinutes": 90,
                      "storageLocation": "movies/test.mp4",
                      "posterLocation": "movies/posters/test.jpg"
                    }
                    """;
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Release year is required."));
    }

    @Test
    void shouldReturnBadRequestWhenReleaseYearIsLessThan1888() throws Exception {
        MovieRequest movieRequest = createTestMovieRequest();
        movieRequest.setReleaseYear(1887);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Release year must be after 1887."));
    }

    @Test
    void shouldReturnBadRequestWhenRuntimeMinutesIsMissing() throws Exception {
        String json = """
                    {
                      "title": "Test Movie",
                      "description": "Description",
                      "releaseYear": 2009,
                      "storageLocation": "movies/test.mp4",
                      "posterLocation": "movies/posters/test.jpg"
                    }
                    """;
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Runtime is required."));
    }

    @Test
    void shouldReturnBadRequestWhenRuntimeMinutesIsNotPositive() throws Exception {
        MovieRequest movieRequest = createTestMovieRequest();
        movieRequest.setRuntimeMinutes(-1);
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Runtime must be greater than 0."));
    }

    @Test
    void shouldReturnBadRequestWhenStorageLocationIsBlank() throws Exception {
        MovieRequest movieRequest = createTestMovieRequest();
        movieRequest.setStorageLocation("");
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Storage location cannot be blank."));
    }

    @Test
    void shouldReturnBadRequestWhenStorageLocationIsTooLong() throws Exception {
        MovieRequest movieRequest = createTestMovieRequest();
        movieRequest.setStorageLocation("A".repeat(256));
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Storage location cannot be greater than 255 characters."));
    }

    @Test
    void shouldReturnNotFoundExceptionWhenMovieDoesNotExist() throws Exception {
        when(movieService.getMovie(999L)).thenThrow(new MovieNotFoundException(999L));
        mockMvc.perform(get("/movies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Movie with ID: 999 was not found."));
    }

    private MovieRequest createTestMovieRequest() {
        return new MovieRequest(
                "Test Movie Title",
                "Test Movie Description",
                2009,
                90,
                "Test Movie Storage Location",
                "Test Poster Storage Location"
        );
    }

    private MovieResponse createTestMovieResponse() {
        return new MovieResponse(
                1L,
                "Test Movie Title",
                "TestMovieDescription",
                2009,
                90
        );
    }
}
