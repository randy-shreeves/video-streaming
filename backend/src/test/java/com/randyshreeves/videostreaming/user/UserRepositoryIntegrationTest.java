package com.randyshreeves.videostreaming.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUserSuccessfully() {
        User user = new User(
                "testuser",
                "hashedpassword",
                Role.ROLE_USER
        );
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
    }

    @Test
    void shouldFindByUsername() {
        User user = new User(
            "testuser",
            "hashedpassword",
            Role.ROLE_USER
        );
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void shouldNotAllowDuplicateUsername() {
        User firstUser = new User(
                "duplicate",
                "password",
                Role.ROLE_USER
        );
        User secondUser = new User(
                "duplicate",
                "differentpassword",
                Role.ROLE_USER
        );
        userRepository.save(firstUser);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(secondUser));
    }
}
