package com.randyshreeves.videostreaming.auth;

import com.randyshreeves.videostreaming.auth.dto.LoginRequest;
import com.randyshreeves.videostreaming.auth.dto.LoginResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
