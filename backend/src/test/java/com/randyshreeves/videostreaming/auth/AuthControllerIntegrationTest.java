package com.randyshreeves.videostreaming.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.randyshreeves.videostreaming.auth.dto.LoginRequest;
import com.randyshreeves.videostreaming.auth.dto.NewUserRegistrationRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private final String regularUserUsername = "regularUser";

    private final String regularUserPassword = "regularPassword";

    @Test
    void shouldRegisterNewUserSuccessfully() throws Exception {
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(regularUserUsername, regularUserPassword);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnConflictDuringRegistrationIfUsernameAlreadyExists() throws Exception {
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(regularUserUsername, regularUserPassword);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestIfUsernameIsBlankDuringRegistration() throws Exception {
        String invalidUsername = "";
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(invalidUsername, regularUserPassword);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfUsernameIsTooShortDuringRegistration() throws Exception {
        String invalidUsername = "a";
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(invalidUsername, regularUserPassword);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfUsernameIsTooLongDuringRegistration() throws Exception {
        String invalidUsername = "a".repeat(31);
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(invalidUsername, regularUserPassword);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfUsernameDoesntMatchRegexDuringRegistration() throws Exception {
        String invalidUsername = "_invalidUsername";
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(invalidUsername, regularUserPassword);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfPasswordIsBlankDuringRegistration() throws Exception {
        String invalidPassword = "";
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(regularUserUsername, invalidPassword);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfPasswordIsTooShortDuringRegistration() throws Exception {
        String invalidPassword = "a";
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(regularUserUsername, invalidPassword);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfPasswordIsTooLongDuringRegistration() throws Exception {
        String invalidPassword = "a".repeat(31);
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(regularUserUsername, invalidPassword);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfUsernameIsBlankDuringLogin() throws Exception {
        String invalidUsername = "";
        LoginRequest request = new LoginRequest(invalidUsername, regularUserPassword);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfUsernameIsTooShortDuringLogin() throws Exception {
        String invalidUsername = "a";
        LoginRequest request = new LoginRequest(invalidUsername, regularUserPassword);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfUsernameIsTooLongDuringLogin() throws Exception {
        String invalidUsername = "a".repeat(31);
        LoginRequest request = new LoginRequest(invalidUsername, regularUserPassword);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfUsernameDoesntMatchRegexDuringLogin() throws Exception {
        String invalidUsername = "_invalidUsername";
        LoginRequest request = new LoginRequest(invalidUsername, regularUserPassword);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfPasswordIsBlankDuringLogin() throws Exception {
        String invalidPassword = "";
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(regularUserUsername, invalidPassword);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfPasswordIsTooShortDuringLogin() throws Exception {
        String invalidPassword = "a";
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(regularUserUsername, invalidPassword);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfPasswordIsTooLongDuringLogin() throws Exception {
        String invalidPassword = "a".repeat(31);
        NewUserRegistrationRequest request = new NewUserRegistrationRequest(regularUserUsername, invalidPassword);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
