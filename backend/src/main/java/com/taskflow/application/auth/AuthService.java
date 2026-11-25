package com.taskflow.application.auth;

import com.taskflow.application.common.Actor;
import com.taskflow.application.port.PasswordHasher;
import com.taskflow.application.port.TokenIssuer;
import com.taskflow.application.port.TokenIssuer.AccessToken;
import com.taskflow.domain.exception.ConflictException;
import com.taskflow.domain.exception.NotFoundException;
import com.taskflow.domain.exception.UnauthorizedException;
import com.taskflow.domain.model.User;
import com.taskflow.domain.repository.UserRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AuthService {

    public record RegisterCommand(String email, String password, String displayName) {}

    public record LoginCommand(String email, String password) {}

    public record AuthResult(User user, AccessToken token) {}

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final TokenIssuer tokens;

    public AuthService(UserRepository users, PasswordHasher hasher, TokenIssuer tokens) {
        this.users = users;
        this.hasher = hasher;
        this.tokens = tokens;
    }

    public AuthResult register(RegisterCommand cmd) {
        String email = cmd.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ConflictException("An account already exists for this email");
        }
        User user = users.save(User.register(email, hasher.hash(cmd.password()), cmd.displayName()));
        return new AuthResult(user, tokens.issue(user));
    }

    @Transactional(readOnly = true)
    public AuthResult login(LoginCommand cmd) {
        // Same error for unknown email and wrong password: no user enumeration.
        User user = users.findByEmail(cmd.email().trim().toLowerCase())
                .filter(u -> hasher.matches(cmd.password(), u.getPasswordHash()))
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        return new AuthResult(user, tokens.issue(user));
    }

    @Transactional(readOnly = true)
    public User me(Actor actor) {
        return users.findById(actor.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<User> listUsers(Actor actor) {
        if (!actor.isAdmin()) {
            throw new com.taskflow.domain.exception.ForbiddenException("Administrator role required");
        }
        return users.findAll();
    }
}
