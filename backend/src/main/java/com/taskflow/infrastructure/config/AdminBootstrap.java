package com.taskflow.infrastructure.config;

import com.taskflow.application.port.PasswordHasher;
import com.taskflow.domain.model.GlobalRole;
import com.taskflow.domain.model.User;
import com.taskflow.domain.repository.UserRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Creates the first ADMIN account at startup when ADMIN_EMAIL and ADMIN_PASSWORD are provided.
 * No credential is ever committed to the repository; nothing happens when the variables are absent.
 */
@Component
class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final String email;
    private final String password;

    AdminBootstrap(UserRepository users, PasswordHasher hasher,
                   @Value("${app.bootstrap-admin.email}") String email,
                   @Value("${app.bootstrap-admin.password}") String password) {
        this.users = users;
        this.hasher = hasher;
        this.email = email.trim().toLowerCase();
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank() || users.existsByEmail(email)) {
            return;
        }
        users.save(new User(null, email, hasher.hash(password), "Administrator", GlobalRole.ADMIN, Instant.now()));
        log.info("Bootstrap administrator account created for {}", email);
    }
}
