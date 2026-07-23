package com.randyshreeves.videostreaming.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.randyshreeves.videostreaming.auth.dto.LoginRequest;
import com.randyshreeves.videostreaming.auth.dto.LoginResponse;
import com.randyshreeves.videostreaming.movie.Movie;
import com.randyshreeves.videostreaming.movie.MovieRepository;
import com.randyshreeves.videostreaming.user.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"media.root=src/test/resources/media"})
@ActiveProfiles("test")
@Transactional
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    private final String adminUserUsername = "adminUser";

    private final String adminUserPassword = "adminPassword";

    private final String regularUserUsername = "regularUser";

    private final String regularUserPassword = "regularPassword";

    @BeforeEach
    void setUp() {
        userService.registerUser(regularUserUsername, regularUserPassword);
        userService.registerUser(adminUserUsername, adminUserPassword);
        User adminUser = userRepository.findByUsername(adminUserUsername).orElseThrow();
        adminUser.setRole(Role.ROLE_ADMIN);
        userRepository.save(adminUser);
    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest(regularUserUsername, regularUserPassword);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void shouldReturnUnauthorizedWithInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest(regularUserUsername, "badPassword");
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void shouldAllowAnonymousAccessToMoviesList() throws Exception {
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldRejectAnonymousRequestsToStreamMovie() throws Exception {
        mockMvc.perform(get("/movies/1/stream"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessToStreamMovieWhenLoggedInWithValidJWT() throws Exception {
        String jwt = loginAndGetToken(regularUserUsername, regularUserPassword);
        Movie movie = new Movie(
                "Test Title",
                "Test Description",
                2001,
                90,
                "test_movie.mp4",
                "test_poster.jpg"
        );
        movie = movieRepository.save(movie);
        Long movieId = movie.getId();
        mockMvc.perform(get("/movies/{id}/stream", movieId)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().contentType("video/mp4"));
    }

    @Test
    void shouldForbidNonAdminUserFromDeletingMovie() throws Exception {
        String jwt = loginAndGetToken(regularUserUsername, regularUserPassword);
        mockMvc.perform(delete("/movies/1")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminUserToDeleteMovieSuccessfully() throws Exception {
        String jwt = loginAndGetToken(adminUserUsername, adminUserPassword);
        Movie movie = new Movie(
                "Test Title",
                "Test Description",
                2001,
                90,
                "test_movie.mp4",
                "test_poster.jpg"
        );
        movie = movieRepository.save(movie);
        Long movieId = movie.getId();
        mockMvc.perform(delete("/movies/{id}", movieId)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
        assertTrue(movieRepository.findById(movieId).isEmpty());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);
        MvcResult result = mockMvc.perform(post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                                    .andExpect(status().isOk())
                                    .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        LoginResponse response = objectMapper.readValue(responseJson, LoginResponse.class);
        return response.getToken();
    }
}
