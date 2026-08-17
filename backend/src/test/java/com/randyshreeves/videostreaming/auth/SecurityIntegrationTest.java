package com.randyshreeves.videostreaming.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.randyshreeves.videostreaming.auth.dto.LoginRequest;
import com.randyshreeves.videostreaming.auth.dto.LoginResponse;
import com.randyshreeves.videostreaming.auth.dto.NewUserRegistrationRequest;
import com.randyshreeves.videostreaming.movie.Movie;
import com.randyshreeves.videostreaming.movie.MovieRepository;
import com.randyshreeves.videostreaming.user.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(JwtTestConfiguration.class)
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

    @Autowired
    JwtTestHelper jwtTestHelper;

    @Autowired
    StreamTokenService streamTokenService;

    private final String adminUserUsername = "adminUser";

    private final String adminUserPassword = "adminPassword";

    private final String regularUserUsername = "regularUser";

    private final String regularUserPassword = "regularPassword";

    @BeforeEach
    void setUp() {
        NewUserRegistrationRequest regularUserRequest = new NewUserRegistrationRequest(regularUserUsername, regularUserPassword);
        NewUserRegistrationRequest adminUserRequest = new NewUserRegistrationRequest(adminUserUsername, adminUserPassword);
        userService.registerUser(regularUserRequest);
        userService.registerUser(adminUserRequest);
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
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message").value("Invalid username or password."));
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
        String streamToken = streamTokenService.generateToken(movieId);
        mockMvc.perform(get("/movies/{id}/stream?token={streamToken}", movieId, streamToken)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().contentType("video/mp4"));
    }

    @Test
    void shouldRejectExpiredJwt() throws Exception {
        String expiredJwt = jwtTestHelper.generateExpiredToken(regularUserUsername);
        mockMvc.perform(get("/movies/1/stream")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredJwt))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectJwtWithInvalidSignature() throws Exception {
        String invalidJwt = jwtTestHelper.generateTokenWithInvalidSignature(regularUserUsername);
        mockMvc.perform(get("/movies/1/stream")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidJwt))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldForbidNonAdminUserFromDeletingMovie() throws Exception {
        String jwt = loginAndGetToken(regularUserUsername, regularUserPassword);
        mockMvc.perform(delete("/movies/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNoContent());
        assertTrue(movieRepository.findById(movieId).isEmpty());
    }

    @Test
    void shouldAllowAdminUserToUpdateMovieSuccessfully() throws Exception {
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
        Movie upadtedMovieRequest = new Movie(
                "Updated Test Title",
                "Test Description",
                2001,
                90,
                "test_movie.mp4",
                "test_poster.jpg"
        );
        mockMvc.perform(put("/movies/{id}", movieId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(upadtedMovieRequest)))
                .andExpect(status().isOk());
        Movie retrievedMovie = movieRepository.findById(movieId).orElseThrow();
        assertEquals("Updated Test Title", retrievedMovie.getTitle());
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
