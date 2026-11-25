package com.taskflow.web.admin;

import com.taskflow.application.auth.AuthService;
import com.taskflow.web.CurrentActor;
import com.taskflow.web.auth.AuthDtos.UserResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Global-ADMIN-only endpoints. Guarded twice: URL rule in SecurityConfig and method security here. */
@RestController
@RequestMapping("/api/v1/admin")
class AdminController {

    private final AuthService auth;

    AdminController(AuthService auth) {
        this.auth = auth;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    List<UserResponse> users(@AuthenticationPrincipal Jwt jwt) {
        return auth.listUsers(CurrentActor.from(jwt)).stream().map(UserResponse::from).toList();
    }
}
