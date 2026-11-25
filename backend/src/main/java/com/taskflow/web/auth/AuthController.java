package com.taskflow.web.auth;

import com.taskflow.application.auth.AuthService;
import com.taskflow.application.auth.AuthService.LoginCommand;
import com.taskflow.application.auth.AuthService.RegisterCommand;
import com.taskflow.web.CurrentActor;
import com.taskflow.web.auth.AuthDtos.AuthResponse;
import com.taskflow.web.auth.AuthDtos.LoginRequest;
import com.taskflow.web.auth.AuthDtos.RegisterRequest;
import com.taskflow.web.auth.AuthDtos.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private final AuthService auth;

    AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return AuthResponse.from(auth.register(new RegisterCommand(req.email(), req.password(), req.displayName())));
    }

    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return AuthResponse.from(auth.login(new LoginCommand(req.email(), req.password())));
    }

    @GetMapping("/me")
    UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return UserResponse.from(auth.me(CurrentActor.from(jwt)));
    }
}
