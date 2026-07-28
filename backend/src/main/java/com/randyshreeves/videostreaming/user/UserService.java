package com.randyshreeves.videostreaming.user;

import com.randyshreeves.videostreaming.auth.dto.NewUserRegistrationRequest;
import com.randyshreeves.videostreaming.exception.UsernameAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(NewUserRegistrationRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists.");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, hashedPassword, Role.ROLE_USER);
        userRepository.save(user);
    }
}
