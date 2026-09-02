package com.randyshreeves.videostreaming.auth;

import com.randyshreeves.videostreaming.auth.dto.LoginRequest;
import com.randyshreeves.videostreaming.auth.dto.LoginResponse;
import com.randyshreeves.videostreaming.auth.dto.NewUserRegistrationRequest;
import com.randyshreeves.videostreaming.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Validated(ValidationGroups.class)
            @RequestBody LoginRequest loginRequest
    ) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(
            @Validated(ValidationGroups.class)
            @RequestBody NewUserRegistrationRequest request
    ) {
        userService.registerUser(request);
    }
}
