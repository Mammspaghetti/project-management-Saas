package com.taskflow.web.auth;

import com.taskflow.application.auth.AuthService.AuthResult;
import com.taskflow.domain.model.GlobalRole;
import com.taskflow.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank @Email @Size(max = 190) String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @NotBlank @Size(max = 100) String displayName) {}

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record UserResponse(Long id, String email, String displayName, GlobalRole role) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.getRole());
        }
    }

    public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {
        public static AuthResponse from(AuthResult r) {
            return new AuthResponse(r.token().value(), "Bearer", r.token().expiresInSeconds(),
                    UserResponse.from(r.user()));
        }
    }
}
