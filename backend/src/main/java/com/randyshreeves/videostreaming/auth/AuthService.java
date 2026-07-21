package com.randyshreeves.videostreaming.auth;

import com.randyshreeves.videostreaming.auth.dto.LoginRequest;
import com.randyshreeves.videostreaming.auth.dto.LoginResponse;
import com.randyshreeves.videostreaming.user.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }
}
